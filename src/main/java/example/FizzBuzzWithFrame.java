package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FizzBuzzWithFrame implements Opcodes {
  private static byte[] generate(String className) {
    ClassWriter cw = new ClassWriter(0);
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

    Label loopBegin = new Label();
    Label loopNext = new Label();
    Label loopEnd = new Label();

    Label fizzBranch = new Label();
    Label buzzBranch = new Label();
    Label elseBranch = new Label();

    // i = 1
    int _i = 1;
    mv.visitInsn(ICONST_1);
    mv.visitVarInsn(ISTORE, _i);

    // max = 100
    int _max = 2;
    mv.visitIntInsn(BIPUSH, 100);
    mv.visitVarInsn(ISTORE, _max);

    // -- loopBegin --
    mv.visitLabel(loopBegin);
    // ループ開始時と2回目以降でローカル変数が異なる
    // (j, kが追加)のでF_APPEND
    mv.visitFrame(F_APPEND, 2,
        new Object[] { Opcodes.INTEGER, Opcodes.INTEGER }, 0, null);

    // j = i % 3
    int _j = 3;
    mv.visitVarInsn(ILOAD, _i);
    mv.visitInsn(ICONST_3);
    mv.visitInsn(IREM);
    mv.visitVarInsn(ISTORE, _j);

    // k = i % 5
    int _k = 4;
    mv.visitVarInsn(ILOAD, _i);
    mv.visitInsn(ICONST_5);
    mv.visitInsn(IREM);
    mv.visitVarInsn(ISTORE, _k);

    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");

    // if j == 0 && k == 0 then print "FizzBuzz"
    mv.visitVarInsn(ILOAD, _j);
    mv.visitJumpInsn(IFNE, fizzBranch);
    mv.visitVarInsn(ILOAD, _k);
    mv.visitJumpInsn(IFNE, fizzBranch);

    mv.visitLdcInsn("FizzBuzz");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);
    mv.visitJumpInsn(GOTO, loopNext);

    // if j == 0 then print "Fizz"
    mv.visitLabel(fizzBranch);
    // メソッド開始時のフレームとだいぶ差がある
    // (ローカル変数にi, max, j, k、 スタックにSystem.out)ので
    // F_FULLで全部指定
    mv.visitFrame(F_FULL, 5,
        new Object[] { "[Ljava/lang/String;", Opcodes.INTEGER, Opcodes.INTEGER,
            Opcodes.INTEGER, Opcodes.INTEGER },
        1, new Object[] { "java/io/PrintStream" });
    mv.visitVarInsn(ILOAD, _j);
    mv.visitJumpInsn(IFNE, buzzBranch);

    mv.visitLdcInsn("Fizz");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);
    mv.visitJumpInsn(GOTO, loopNext);

    // if k == 0 then print "Buzz"
    mv.visitLabel(buzzBranch);
    // 前フレームと差はないがスタックに1つ値があるのでF_SAME1
    mv.visitFrame(F_SAME1, 0, null, 1, new Object[] { "java/io/PrintStream" });
    mv.visitVarInsn(ILOAD, _k);
    mv.visitJumpInsn(IFNE, elseBranch);

    mv.visitLdcInsn("Buzz");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/String;)V", false);
    mv.visitJumpInsn(GOTO, loopNext);

    // else print i
    mv.visitLabel(elseBranch);
    // 前フレームと差はないがスタックに1つ値があるのでF_SAME1
    mv.visitFrame(F_SAME1, 0, null, 1, new Object[] { "java/io/PrintStream" });
    mv.visitVarInsn(ILOAD, _i);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",
        false);

    // -- loopNext --
    mv.visitLabel(loopNext);
    // 前フレームと同一、スタックも空なのでF_SAME
    mv.visitFrame(F_SAME, 0, null, 0, null);

    // i++
    mv.visitIincInsn(_i, 1);

    // if i <= max then goto loopBegin
    mv.visitVarInsn(ILOAD, _i);
    mv.visitVarInsn(ILOAD, _max);
    mv.visitJumpInsn(IF_ICMPLE, loopBegin);

    // -- loopEnd --
    mv.visitLabel(loopEnd);

    mv.visitInsn(RETURN);
    mv.visitMaxs(2, 5);
    mv.visitEnd();
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "FizzBuzzWithFrame";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}
