package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Calculation implements Opcodes {
  private static byte[] generate(String className) {
    ClassWriter cw = new ClassWriter(0);
    MethodVisitor mv;

    cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className, null,
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

    // intの演算
    // 123 + 456
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitIntInsn(BIPUSH, 123); // Byte.MIN_VALUE - Byte.MAX_VALUE の範囲の値
    mv.visitIntInsn(SIPUSH, 456); // SHORT.MIN_VALUE - SHORT.MAX_VALUE の範囲の値
    mv.visitInsn(IADD);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",
        false);

    // intの演算
    // 123 - 456
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitIntInsn(SIPUSH, 123);
    mv.visitLdcInsn(456); // このような指定も可能
    mv.visitInsn(ISUB);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",
        false);

    // floatの演算
    // 123.0 / 456.0
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn(123.0F);
    mv.visitLdcInsn(456.0F);
    mv.visitInsn(FDIV);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(F)V",
        false);

    // longの演算
    // Integer.MAX_VALUE + 1
    // 1つの数が2スロット消費するので注意
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn(Integer.MAX_VALUE);
    mv.visitInsn(I2L); // int -> long のキャスト
    mv.visitInsn(LCONST_1);
    mv.visitInsn(LADD);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(J)V",
        false);

    // doubleの演算
    // 123.321 * 456.654
    // 1つの数が2スロット消費するので注意
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn(123.321);
    mv.visitLdcInsn(456.654);
    mv.visitInsn(DMUL);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(D)V",
        false);

    mv.visitInsn(RETURN);
    mv.visitMaxs(5, 1);
    mv.visitEnd();
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "Calculation";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}
