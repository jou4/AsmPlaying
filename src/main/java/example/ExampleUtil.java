package example;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ExampleUtil {

  private static class MyClassLoader extends ClassLoader {
    public Class<?> defineClass(String name, byte[] b) {
      return defineClass(name, b, 0, b.length);
    }
  }

  public static void execMain(String className, byte[] bytes)
      throws IllegalAccessException, IllegalArgumentException,
      NoSuchMethodException, SecurityException {
    MyClassLoader myClassLoader = new MyClassLoader();
    Class<?> c = myClassLoader.defineClass(className, bytes);
    try {
      c.getMethod("main", String[].class).invoke(null,
          new Object[] { new String[0] });
    } catch (InvocationTargetException e) {
      e.getCause().printStackTrace();
    }
  }

  public static void write(String className, byte[] bytes) throws IOException {
    File dir = new File("target" + File.separator + "output");
    if (!dir.exists())
      dir.mkdirs();
    DataOutputStream out = new DataOutputStream(
        new FileOutputStream(new File(dir, className + ".class")));
    out.write(bytes);
    out.flush();
    out.close();
  }

}