package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HelloWorld implements Opcodes {
  private static byte[] generate(String className) {
    ClassWriter cw = new ClassWriter(0);
    MethodVisitor mv;

    cw.visit(V1_8, ACC_PUBLIC + ACC_ABSTRACT, className, null,
        "java/lang/Object", null);

    // constructor
    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V",
        false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    // main
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
        "([Ljava/lang/String;)V", null, null);
    mv.visitCode();

    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");

    // println
    mv.visitLdcInsn("Hello world.");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/Object;)V", false);

    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "HelloWorld";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}