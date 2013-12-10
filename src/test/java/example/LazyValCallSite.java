package example;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.*;
//import sun.invoke.anon.AnonymousClassLoader;


import org.objectweb.asm.*;

public class LazyValCallSite implements Opcodes 
{
    
    public interface LazyVal
    {
        public int value();
    }
    
    public static class DynamicLoader extends URLClassLoader
    {
        public DynamicLoader(URL[] urls, ClassLoader parent)
        {
            super(urls, parent);
        }        
        
        public Class<?> loadFromBytes(byte[] classDefinition)
        {
            Class<?> clazz=defineClass(null,classDefinition, 0,classDefinition.length);
            resolveClass(clazz);
            return clazz;
        }
        
    }
    
    static public void main(String[] args) {
	int a = apply(1).value();
	//	int b = apply(2).value();
    }
    // Boot strap myself
    static private DynamicLoader dl = new DynamicLoader(new URL[]{}, LazyValCallSite.class.getClassLoader());
    static private Class template;
    //    static private AnonymousClassLoader duplicator;
    static private byte[] classFile;
  
    static LazyVal apply(int i) {
	try{

	    Constructor cons;
	    cons=template.getConstructor(new Class<?>[0]);
	    return (LazyVal) cons.newInstance(new Object[0]);
	}
	catch(Throwable t)
	    {
		t.printStackTrace();
		return null;
	    }
    }
    static 
    {
        try
        {
    
	    classFile = dump();
	    template = dl.loadFromBytes(classFile);
	    
        }
        catch(Throwable t)
	    {
		t.printStackTrace();
	    }
        
    }
    
    public static byte[] dump () throws Exception 
    {

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        cw.visit
        (
            V1_7, ACC_PUBLIC + ACC_SUPER, 
            "example/Simple", 
            null, 
            "java/lang/Object", 
            new String[]{"example/LazyValCallSite$LazyVal"}
        );

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
	    }

	cw.visitField(org.objectweb.asm.Opcodes.ACC_PRIVATE, "value_field", "I",
		      null, null).visitEnd();
	


        {
            mv = cw.visitMethod(ACC_PUBLIC, "value", "()I", null, null);
            mv.visitCode();
	    mv.visitVarInsn(ALOAD, 0);
	    //            mv.visitVarInsn(ILOAD, 2);
            //mv.visitMethodInsn(INVOKESTATIC, "Simple", "add", "(JJ)J");
            makeInvokeDynamic(mv);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }
    

    public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, java.lang.invoke.MethodType type, int initValue )
    {
        System.out.println(">>>> Entering Boot Strap " + name + " with init value "+ initValue);
//        _lookup = lookup;
        // that to which we bootstrap depends on the first parameter
        // passed to the application!

        java.lang.reflect.Method m=null;
        try
        {
            m=LazyValCallSite.class.getMethod("read", new Class[]{int.class});
            System.out.println("<<<<< Leaving Boot Strap");
            return new VolatileCallSite(MethodHandles.insertArguments(lookup.unreflect(m), 0, initValue));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // Blow up here rather than let then thing go on
            // throwing more exceptions
            System.exit(-1);
        }
	return null;
    }    

    
    /** The method which is invoked statically and dynamically */
    public static int read(int value)
    {
	System.out.println("=========== READ + " + value);
        return value;
    }
        
    private static void  makeInvokeDynamic(MethodVisitor mv)
    {
        // This creates a boot strap definition
        try
        {
            java.lang.invoke.MethodType mt = java.lang.invoke.MethodType.methodType
            (
                java.lang.invoke.CallSite.class,
		java.lang.invoke.MethodHandles.Lookup.class,
                java.lang.String.class,
                java.lang.invoke.MethodType.class,
		int.class
            );
	    System.out.println("MakeInvokeDynamic");
            org.objectweb.asm.Handle bootstrap = new org.objectweb.asm.Handle
            (
                Opcodes.H_INVOKESTATIC, 
                "example/LazyValCallSite", 
                "bootstrap",
                mt.toMethodDescriptorString()
            );
            mv.visitInvokeDynamicInsn("run", "()I", bootstrap, 1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
 
