package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Lambda implements Opcodes {
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

    // int add(int a, int b, int c) { return a + b + c; }
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add", "(III)I", null, null);
    mv.visitCode();
    mv.visitVarInsn(ILOAD, 0);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitInsn(IADD);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitInsn(IADD);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(2, 3);
    mv.visitEnd();

    // metafactory
    Handle metafactoryHandle = new Handle(H_INVOKESTATIC,
        "java/lang/invoke/LambdaMetafactory", "metafactory",
        "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;");

    // add$0() { return a -> add$1(a); }
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add$0",
        "()Ljava/util/function/IntFunction;", null, null);
    mv.visitCode();
    // -- function objectの生成
    mv.visitInvokeDynamicInsn(
        // function objectが実装するメソッドの名前
        "apply",
        // CallSiteのシグニチャ
        // 引数の型はキャプチャされる値の型
        // 戻り値の型はfunction objectが実装するインターフェース
        "()Ljava/util/function/IntFunction;",
        // LambdaMetafactory.metafactoryのハンドル
        metafactoryHandle,
        // function objectが実装するメソッドの型
        Type.getType("(I)Ljava/lang/Object;"),
        // function objectのメソッド実行時にコールされるべきメソッドのハンドル
        // 引数はキャプチャされた値とfunction object実行時に渡されたものが使われる
        new Handle(H_INVOKESTATIC, className, "add$1",
            "(I)Ljava/util/function/IntFunction;"),
        // function objectが実装するメソッドの具体化された型
        Type.getType("(I)Ljava/util/function/IntFunction;"));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 0);
    mv.visitEnd();

    // add$1(int a) { return b -> add$2(a); }
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add$1",
        "(I)Ljava/util/function/IntFunction;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ILOAD, 0); // function objectにキャプチャされる
    // -- function objectの生成
    mv.visitInvokeDynamicInsn(
        // function objectが実装するメソッドの名前
        "apply",
        // CallSiteのシグニチャ
        // 引数の型はfunction objectにキャプチャされる値の型
        // 戻り値の型はfunction objectが実装するインターフェース
        "(I)Ljava/util/function/IntFunction;",
        // LambdaMetafactory.metafactoryのハンドル
        metafactoryHandle,
        // function objectが実装するメソッドの型
        Type.getType("(I)Ljava/lang/Object;"),
        // function objectのメソッド実行時にコールされるべきメソッドのハンドル
        // 引数はキャプチャされた値とfunction object実行時に渡されたものが使われる
        new Handle(H_INVOKESTATIC, className, "add$2",
            "(II)Ljava/util/function/IntUnaryOperator;"),
        // function objectが実装するメソッドの具体化された型
        Type.getType("(I)Ljava/util/function/IntUnaryOperator;"));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    // add$2(int a, int b) { return c -> add(a, b, c); }
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "add$2",
        "(II)Ljava/util/function/IntUnaryOperator;", null, null);
    mv.visitCode();
    mv.visitVarInsn(ILOAD, 0); // function objectにキャプチャされる
    mv.visitVarInsn(ILOAD, 1); // function objectにキャプチャされる
    // -- function objectの生成
    mv.visitInvokeDynamicInsn(
        // function objectが実装するメソッドの名前
        "applyAsInt",
        // CallSiteのシグニチャ
        // 引数の型はfunction objectにキャプチャされる値の型
        // 戻り値の型はfunction objectが実装するインターフェース
        "(II)Ljava/util/function/IntUnaryOperator;",
        // LambdaMetafactory.metafactoryのハンドル
        metafactoryHandle,
        // function objectが実装するメソッドの型
        Type.getType("(I)I"),
        // function objectのメソッド実行時にコールされるべきメソッドのハンドル
        // 引数はキャプチャされた値とfunction object実行時に渡されたものが使われる
        new Handle(H_INVOKESTATIC, className, "add", "(III)I"),
        // function objectが実装するメソッドの具体化された型
        Type.getType("(I)I"));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(2, 2);
    mv.visitEnd();

    // main
    mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
        "([Ljava/lang/String;)V", null, null);
    mv.visitCode();
    // -- System.out.prinln(add$0().apply(10).apply(20).applyAsInt(30))
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    // ---- add$0()
    mv.visitMethodInsn(INVOKESTATIC, className, "add$0",
        "()Ljava/util/function/IntFunction;", false);
    // ---- .apply(10) -> checkcast (IntFunction)
    mv.visitIntInsn(BIPUSH, 10);
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/IntFunction",
        "apply", "(I)Ljava/lang/Object;", true);
    mv.visitTypeInsn(CHECKCAST, "java/util/function/IntFunction");
    // ---- .apply(20) -> checkcast (IntUnaryOperator)
    mv.visitIntInsn(BIPUSH, 20);
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/IntFunction",
        "apply", "(I)Ljava/lang/Object;", true);
    mv.visitTypeInsn(CHECKCAST, "java/util/function/IntUnaryOperator");
    // ---- .applyAsInt(30)
    mv.visitIntInsn(BIPUSH, 30);
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/function/IntUnaryOperator",
        "applyAsInt", "(I)I", true);
    // ---- .println()
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V",
        false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 1);
    mv.visitEnd();

    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "Lambda";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}