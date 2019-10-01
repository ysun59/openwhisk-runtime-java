package org.apache.openwhisk.runtime.java.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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

	private List<String> classes = new ArrayList<>();

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

	private void convertStaticsFieldsToMaps(ClassPool pool, CtClass cc) throws NotFoundException, CannotCompileException {
		CtClass weakmap = pool.get("java.util.WeakHashMap");
		for (CtField field : cc.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				System.err.println(String.format("Replacing field %s.%s type of %s by %s", 
						field.getDeclaringClass().getName(), field.getName(), field.getType().getName(), "java.util.WeakHashMap"));
				//cc.removeField(field); // TODO - maybe we shouldn't remove until we finished fixing the code...
				CtField newfield = new CtField(weakmap, field.getName() + "__map", cc);
				newfield.setModifiers(field.getModifiers());
				cc.addField(newfield, CtField.Initializer.byNew(weakmap));
			}
		}
	}

	private void convertFieldAccesses(ClassPool pool, CtClass cc) throws CannotCompileException {
		ExprEditor editor = new ExprEditor() {

            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                try {
                	// do not change how we access static fields of java libraries
                	// TODO - it would be better if we knew if we have a map for it or not...
                	if (f.getClassName().startsWith("java.")
                            || f.getClassName().startsWith("javax.")
                            || f.getClassName().startsWith("sun.")
                            || f.getClassName().startsWith("com.sun.")
                            || f.getClassName().startsWith("org.w3c.")
                            || f.getClassName().startsWith("org.xml.")) {
                		return;
                	}
                	else if (f.isStatic() && !Modifier.isFinal(f.getField().getModifiers())) {
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

		// TODO - this returns all non-private constructors
		for (CtConstructor constructor : cc.getConstructors()) {
			constructor.instrument(editor);
		}
		// TODO - this returns all non-private methods
		// TODO - this also includes methods from the superclasses
		for (CtMethod method : cc.getMethods()) {
			method.instrument(editor);
		}
	}

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
		CtClass cc = pool.get(classname);

		// TODO - get fields returns the public fields including fields from superclasses!
		
		convertStaticsFieldsToMaps(pool, cc);

		//setupMapsInitialiser(pool, cc);

		convertFieldAccesses(pool, cc);

		if (cloneStaticInitializer(cc)) {
			classes.add(cc.getName());
		}

		System.err.println("Loaded " + classname);
		
		// TODO - need to see how to actually print the bytecode of each method.
		//ClassFilePrinter.print(cc.getClassFile());
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException, CannotCompileException { }

	public void callStaticInitialisers(ClassLoader cl) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String classname : classes) {
			Class<?> clazz = Class.forName(classname, false, cl);
			System.err.println("Calling " + clazz.getMethod("__static_init", new Class[] {}));
			clazz.getMethod("__static_init", new Class[] {}).invoke(null, new Object[] {});
		}
	}

}
