package org.apache.openwhisk.runtime.java.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.Mnemonic;
import javassist.bytecode.Opcode;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class SimpleTranslator implements Translator {

	// classes that need to be statically initialized upon every invocation.
	private Set<String> classesForStaticInitialization = new HashSet<>();
	
	// fields that were modified to use a map
	private Set<String> modifiedFieldAccesses = new HashSet<>();

	private static boolean debug = false;
	
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

        cc.addConstructor(staticConstructorClone);
        return true;
	}

	private List<CtField> createStaticFieldMaps(ClassPool pool, CtClass cc) throws NotFoundException, CannotCompileException {
		CtClass weakmap = pool.get("java.util.WeakHashMap");
		List<CtField> tobeRemoved = new ArrayList<>();
		for (CtField field : cc.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
				if (debug) {
					System.err.println(String.format("Adding field %s.%s type of %s", field.getDeclaringClass().getName(), field.getName() + "__map", "java.util.WeakHashMap"));	
				}
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
                	CtField field = f.getField();
                	// if field was modified (it is static and non-final)
                	if (!modifiedFieldAccesses.contains(field.getSignature())) {
                		return;
                	}
                	
                	// This if is not necessary, if it was modified, then it is static and not final...
                	if (f.isStatic() && !Modifier.isFinal(field.getModifiers())) {
                		if (debug) {
                			System.out.println(String.format("Found field access %s.%s (%s:%d)", f.getClassName(), f.getFieldName(), f.getFileName(), f.getLineNumber()));	
                		}
                        if (f.isWriter()) {
                        	f.replace(String.format("%s.%s__map.put(Thread.currentThread().getContextClassLoader(), ($w)$1);", field.getDeclaringClass().getName(), f.getFieldName()));
                        } else {
                        	f.replace(String.format("$_ = ($r)%s.%s__map.get(Thread.currentThread().getContextClassLoader());", field.getDeclaringClass().getName(), f.getFieldName()));
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
	
	private void insertYieldPoint(ConstPool cp, CodeIterator ci, int index) throws BadBytecode {
		int cindex = cp.addClassInfo("org.apache.openwhisk.runtime.java.action.Scheduler");
		int mindex = cp.addMethodrefInfo(cindex, "yield", Descriptor.ofMethod(CtClass.voidType, new CtClass[] {}));
		ci.insertAt(index, new byte[] { (byte)Opcode.INVOKESTATIC, (byte) ((byte)mindex >> 8), (byte)mindex});
	}
	
	private void insertYieldPoints(CtBehavior behavior) throws CannotCompileException {
		behavior.insertAfter("org.apache.openwhisk.runtime.java.action.Scheduler.yield();");
		CodeAttribute ca = behavior.getMethodInfo().getCodeAttribute();
		CodeIterator ci = ca.iterator();
		
		while (ci.hasNext()) {
		    int index;
			try {
				index = ci.next();
			    int op = ci.byteAt(index);
			    if (op == Opcode.GOTO) {
			    	short nbyte =  (short)ci.byteAt(index + 1);
			    	short nnbyte = (short)ci.byteAt(index + 2);
			    	short operand = (short) (((short) (nbyte << 8)) + nnbyte);
			    	if (operand < 0) {
			    		insertYieldPoint(ca.getConstPool(), ci, index);
			    	}				    	
		    	} else if (op == Opcode.GOTO_W) {
		    		// branchbyte1 << 24 + branchbyte2 << 16 + branchbyte3 << 8 + branchbyte4
		    		short nbyte =    (short)ci.byteAt(index + 1);
			    	short nnbyte =   (short)ci.byteAt(index + 2);
			    	short nnnbyte =  (short)ci.byteAt(index + 3);
			    	short nnnnbyte = (short)ci.byteAt(index + 4);
			    	short operand =  (short) (((short) (nbyte << 24)) + ((short) nnbyte << 16) + ((short) nnnbyte << 8) + nnnnbyte);
			    	System.out.println(Mnemonic.OPCODE[op]);
			    	if (operand < 0) {
			    		insertYieldPoint(ca.getConstPool(), ci, index);
			    	}
			    }
			} catch (BadBytecode e) {
				e.printStackTrace();
			}
		}
	}
	
	private void insertYieldPoints(CtClass cc) throws CannotCompileException {

        if (cc.getClassInitializer() != null) {
        	insertYieldPoints(cc.getClassInitializer());	
        }

		for (CtConstructor constructor : cc.getDeclaredConstructors()) {
			insertYieldPoints(constructor);
		}

		for (CtMethod method : cc.getDeclaredMethods()) {
			insertYieldPoints(method);		
		}
	}

	@Override
	public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
		CtClass cc = pool.get(classname);

		List<CtField> tobeRemoved = createStaticFieldMaps(pool, cc);

		convertStaticFieldAccesses(pool, cc);

		// Fields have to be removed after converting accesses.
		for (CtField f : tobeRemoved) {
			if (debug) {
				System.err.println(String.format("Removing field %s.%s type of %s", f.getDeclaringClass().getName(), f.getName(), f.getType().getName()));	
			}
			// TODO - this was causing a bug...?
			//cc.removeField(f);
		}
		
		if (cloneStaticInitializer(cc)) {
			classesForStaticInitialization.add(cc.getName());
		}

		//insertYieldPoints(cc);
		if (debug) {
			System.err.println("Loaded " + classname);	
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException, CannotCompileException { 
		CtClass.debugDump = "/tmp/dump";
	}

	public void callStaticInitialisers(ClassLoader cl) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for (String classname : new HashSet<String>(classesForStaticInitialization)) {
			Class<?> clazz = Class.forName(classname, false, cl);
			if (debug) {
				System.err.println("Calling " + clazz.getMethod("__static_init", new Class[] {}));	
			}
			Method m = clazz.getMethod("__static_init", new Class[] {});
			m.setAccessible(true);
			m.invoke(null, new Object[] {});
		}
	}

}
