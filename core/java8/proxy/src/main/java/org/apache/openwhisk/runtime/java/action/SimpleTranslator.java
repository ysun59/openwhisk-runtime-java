package org.apache.openwhisk.runtime.java.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class SimpleTranslator implements Translator {

	// classes that need to be statically initialized upon every invocation.
	private Set<String> classesForStaticInitialization = new HashSet<>();
	
	// fields that were modified to use a map
	private Set<String> modifiedFieldAccesses = new HashSet<>();

	private boolean cloneStaticInitializer(CtClass cc) throws CannotCompileException {
		CtConstructor staticConstructor = cc.getClassInitializer();

		// No static initialiser, no static fields to be initialised.
	    if (staticConstructor == null) {
	    	return false;
	    }

    	// Adding a new method which is a copy of the static initialiser.
    	CtConstructor staticConstructorClone = new CtConstructor(staticConstructor, cc, null);
        staticConstructorClone.getMethodInfo().setName("__static_init");
        staticConstructorClone.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
        cc.addConstructor(staticConstructorClone);

        // This field access hack is necessary because static initialisers often write to final fields.
        staticConstructorClone.instrument(new ExprEditor() {

            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                try {
                    if (f.isStatic() && f.isWriter() && Modifier.isFinal(f.getField().getModifiers())) {
                        f.replace("{  }");
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        return true;
	}

	private List<CtField> createStaticFieldMaps(ClassPool pool, CtClass cc) throws NotFoundException, CannotCompileException {
		CtClass weakmap = pool.get("java.util.WeakHashMap");
		List<CtField> tobeRemoved = new ArrayList<>();
		for (CtField field : cc.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				System.err.println(String.format("Adding field %s.%s type of %s", field.getDeclaringClass().getName(), field.getName() + "__map", "java.util.WeakHashMap"));
				tobeRemoved.add(field);
				CtField newfield = new CtField(weakmap, field.getName() + "__map", cc);
				newfield.setModifiers(field.getModifiers());
				cc.addField(newfield, CtField.Initializer.byNew(weakmap));
				modifiedFieldAccesses.add(field.getSignature());
			}
		}
		return tobeRemoved;
	}
	
	private void convertStaticFieldAccesses(ClassPool pool, CtClass cc) throws CannotCompileException {
		ExprEditor editor = new ExprEditor() {

            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                try {
                	// if field was modified (it is static and non-final)
                	if (!modifiedFieldAccesses.contains(f.getField().getSignature())) {
                		return;
                	}
                	
                	// This if is not necessary, if it was modified, then it is static and not final...
                	if (f.isStatic() && !Modifier.isFinal(f.getField().getModifiers())) {
                		System.out.println(String.format("Found field access %s.%s (%s:%d)", f.getClassName(), f.getFieldName(), f.getFileName(), f.getLineNumber()));
                        if (f.isWriter()) {
                        	f.replace(String.format("%s.%s__map.put(Thread.currentThread().getContextClassLoader(), ($w)$1);", f.getField().getDeclaringClass().getName(), f.getFieldName()));
                        } else {
                        	f.replace(String.format("$_ = ($r)%s.%s__map.get(Thread.currentThread().getContextClassLoader());", f.getField().getDeclaringClass().getName(), f.getFieldName()));
                        }
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

        if (cc.getClassInitializer() != null) {
        	cc.getClassInitializer().instrument(editor);	
        }

		for (CtConstructor constructor : cc.getDeclaredConstructors()) {
			constructor.instrument(editor);
		}

		for (CtMethod method : cc.getDeclaredMethods()) {
			method.instrument(editor);
		}
	}

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
		CtClass cc = pool.get(classname);

		List<CtField> tobeRemoved = createStaticFieldMaps(pool, cc);

		convertStaticFieldAccesses(pool, cc);

		// Fields have to be removed after converting accesses.
		for (CtField f : tobeRemoved) {
			System.err.println(String.format("Removing field %s.%s type of %s", f.getDeclaringClass().getName(), f.getName(), f.getType().getName()));
			cc.removeField(f);
		}
		
		if (cloneStaticInitializer(cc)) {
			classesForStaticInitialization.add(cc.getName());
		}

		System.err.println("Loaded " + classname);
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException, CannotCompileException { }

	public void callStaticInitialisers(ClassLoader cl) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String classname : classesForStaticInitialization) {
			Class<?> clazz = Class.forName(classname, false, cl);
			System.err.println("Calling " + clazz.getMethod("__static_init", new Class[] {}));
			clazz.getMethod("__static_init", new Class[] {}).invoke(null, new Object[] {});
		}
	}

}
