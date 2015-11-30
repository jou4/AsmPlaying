package example;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Invoke implements Opcodes {
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

    // System.out.println(System.getProperty("java.home"));
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitLdcInsn("java.home");
    mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "getProperty",
        "(Ljava/lang/String;)Ljava/lang/String;", false);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/Object;)V", false);

    // ArrayList l1 = new ArrayList();
    int l1 = 1;
    mv.visitTypeInsn(NEW, "java/util/ArrayList");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V",
        false);
    mv.visitVarInsn(ASTORE, l1);
    // l1.add(0, "ABC");
    mv.visitVarInsn(ALOAD, l1);
    mv.visitInsn(ICONST_0);
    mv.visitLdcInsn("ABC");
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add",
        "(ILjava/lang/Object;)V", false);

    // List l2 = l1;
    int l2 = 2;
    mv.visitVarInsn(ALOAD, l1);
    mv.visitVarInsn(ASTORE, l2);
    // l2.add(1, "XYZ");
    mv.visitVarInsn(ALOAD, l2);
    mv.visitInsn(ICONST_1);
    mv.visitLdcInsn("XYZ");
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add",
        "(ILjava/lang/Object;)V", true);

    // System.out.println(l2);
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitVarInsn(ALOAD, l2);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/Object;)V", false);

    // l2.sort(Comparator.reverseOrder());
    mv.visitVarInsn(ALOAD, l2);
    mv.visitMethodInsn(INVOKESTATIC, "java/util/Comparator", "reverseOrder",
        "()Ljava/util/Comparator;", true);
    mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "sort",
        "(Ljava/util/Comparator;)V", true);

    // System.out.println(l2);
    mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
        "Ljava/io/PrintStream;");
    mv.visitVarInsn(ALOAD, l2);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
        "(Ljava/lang/Object;)V", false);

    mv.visitInsn(RETURN);
    mv.visitMaxs(3, 3);
    mv.visitEnd();
    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String... args) throws Exception {
    String className = "Invoke";
    byte[] bytes = generate(className);
    ExampleUtil.execMain(className, bytes);
    ExampleUtil.write(className, bytes);
  }
}