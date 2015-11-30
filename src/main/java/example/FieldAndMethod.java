package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FieldAndMethod implements Opcodes {
  private static byte[] generate(String className) {
    ClassWriter cw = new ClassWriter(0);
    MethodVisitor mv;

    cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
        null);

    // fields
    // public static int a = 100;
    cw.visitField(ACC_PUBLIC + ACC_STATIC, "a", "I", null, 100);
    // public static String;
    cw.visitField(ACC_PUBLIC + ACC_STATIC, "b", "Ljava/lang/String;", null,
        null);
    // public int c;
    cw.visitField(ACC_PUBLIC, "c", "I", null, null);

    // static block
    mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    // -- System.out.println(a)
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitFieldInsn(GETSTATIC, className, "a", "I");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",
        false);
    // -- b = "ABC"
    mv.visitLdcInsn("ABC");
    mv.visitFieldInsn(PUTSTATIC, className, "b", "Ljava/lang/String;");
    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 0);
    mv.visitEnd();

    // constructor
    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V",
        false);
    // -- this.c = 200
    mv.visitVarInsn(ALOAD, 0);
    mv.visitIntInsn(SIPUSH, 200);
    mv.visitFieldInsn(PUTFIELD, className, "c", "I");
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 1);
    mv.visitEnd();

    // main
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
        "([Ljava/lang/String;)V", null, null);
    mv.visitCode();
    // -- System.out.println(b)
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitFieldInsn(GETSTATIC, className, "b", "Ljava/lang/String;");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
    
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "FieldAndMethod";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}