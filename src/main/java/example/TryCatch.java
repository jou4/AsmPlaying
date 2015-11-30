package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class TryCatch implements Opcodes {
  private static byte[] generate(String className) {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    MethodVisitor mv;

    cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
        null);

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

    Label from = new Label();
    Label to = new Label();
    Label illegalArgExHandler = new Label();
    Label anyExHandler = new Label();
    Label finallyHandler = new Label();

    // try-catch block definitions
    mv.visitTryCatchBlock(from, to, illegalArgExHandler,
        "java/lang/IllegalArgumentException");
    mv.visitTryCatchBlock(from, to, anyExHandler, "java/lang/Exception");

    // from
    mv.visitLabel(from);
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn("try-catch example");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);

    // throw
    mv.visitTypeInsn(NEW, "java/lang/Exception");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "()V",
        false);
    mv.visitInsn(ATHROW);

    // to
    mv.visitLabel(to);
    mv.visitJumpInsn(GOTO, finallyHandler);

    // catch IllegalArgumentException
    mv.visitLabel(illegalArgExHandler);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/IllegalArgumentException",
        "printStackTrace", "()V", false);
    mv.visitJumpInsn(GOTO, finallyHandler);

    // catch Exception
    mv.visitLabel(anyExHandler);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Exception", "printStackTrace",
        "()V", false);
    mv.visitJumpInsn(GOTO, finallyHandler);

    // finally
    mv.visitLabel(finallyHandler);
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn("finally");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);

    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 1);
    mv.visitEnd();
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "TryCatch";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}