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
import javassist.bytecode.ClassFilePrinter;
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

	private void setupMapsInitialiser(ClassPool pool, CtClass cc) throws CannotCompileException {
		CtConstructor staticConstructor = cc.getClassInitializer();

		// No static initialiser, no static fields to be initialised.
	    if (staticConstructor == null) {
	    	return;
	    }

	    for (CtField field : cc.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				staticConstructor.insertBeforeBody(String.format("%s = new WeakHashMap<>();", field.getName()));
			}
		}


	}

	private void convertStaticsFieldsToMaps(ClassPool pool, CtClass cc) throws NotFoundException, CannotCompileException {
		CtClass weakmap = pool.get("java.util.WeakHashMap");
		for (CtField field : cc.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				System.err.println("Found field " + field);
				cc.removeField(field);
				cc.addField(new CtField(weakmap, field.getName(), cc));
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
                	else if (f.isStatic() && Modifier.isFinal(f.getField().getModifiers())) {
                        if (f.isWriter()) {
                        	f.replace(String.format("%s.%s.put(Thread.currentThread().getContextClassLoader(), $1);", f.getClassName(), f.getFieldName()));
                        } else {
                        	System.err.println(String.format("$_ = %s.%s.get(Thread.currentThread().getContextClassLoader());", f.getClassName(), f.getFieldName()));
                        	f.replace(String.format("$_ = %s.%s.get(Thread.currentThread().getContextClassLoader());", f.getClassName(), f.getFieldName()));
                        }
                    }
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        };

		cc.getClassInitializer().instrument(editor);
		for (CtConstructor constructor : cc.getConstructors()) {
			constructor.instrument(editor);
		}
		for (CtMethod method : cc.getMethods()) {
			method.instrument(editor);
		}
	}

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
		CtClass cc = pool.get(classname);

		convertStaticsFieldsToMaps(pool, cc);

		setupMapsInitialiser(pool, cc);

		convertFieldAccesses(pool, cc);

		if (cloneStaticInitializer(cc)) {
			classes.add(cc.getName());
		}

		System.err.println("Loaded " + classname);
		ClassFilePrinter.print(cc.getClassFile());
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
