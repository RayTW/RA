package ra.server.basis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import ra.util.annotation.RequestCommand;

/**
 * Service loader.
 *
 * @author Ray Li
 */
public class ServiceLoader<T> {
  private Map<String, ServiceHolder<T>> serviceClass;

  /** Initialize. */
  public ServiceLoader() {
    serviceClass = new ConcurrentHashMap<>();
  }

  /**
   * Wear the class marked @RequestCommand, and search for the specified packageName and its lower
   * packages by default.
   *
   * @param packageName package path
   * @throws ClassNotFoundException Throw when no specified class
   * @throws IOException io
   */
  @SuppressWarnings("unchecked")
  public void loadClasses(String packageName) throws ClassNotFoundException, IOException {
    ArrayList<Class<?>> classes = getClasses(packageName);

    classes
        .stream()
        .filter(obj -> obj.isAnnotationPresent(RequestCommand.class))
        .map(obj -> (Class<T>) obj)
        .forEach(
            obj -> {
              RequestCommand requestCommand = obj.getAnnotation(RequestCommand.class);
              String command = requestCommand.value();
              ServiceHolder<T> holder = new ServiceHolder<>(obj, requestCommand.cache());

              serviceClass.put(command, holder);
            });
  }

  /**
   * Returns service by specific command.
   *
   * @param command command
   * @return T
   * @throws CommandNotFoundException when command not found.
   */
  public T getService(String command) throws CommandNotFoundException {
    T obj = null;
    try {
      ServiceHolder<T> holder = serviceClass.get(command);

      obj = holder.getServiceInstance();

    } catch (Exception e) {
      throw new CommandNotFoundException("\"" + command + "\" not found");
    }
    return obj;
  }

  private ArrayList<Class<?>> getClasses(String packageName)
      throws ClassNotFoundException, IOException {
    ProtectionDomain domain = getClass().getProtectionDomain();
    CodeSource codeSource = domain.getCodeSource();
    Optional<URL> urlfrom = Optional.ofNullable(codeSource.getLocation());
    ArrayList<Class<?>> classes = null;

    if (urlfrom.isPresent()) {
      classes = loadCleasses(urlfrom.get(), packageName);
    }

    // local devlop
    if (classes.isEmpty()) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      String path = packageName.replace('.', '/');
      Enumeration<URL> resources = classLoader.getResources(path);

      while (resources.hasMoreElements()) {
        URL resource = resources.nextElement();

        List<Class<?>> ret = findClasses(new File(resource.getFile()), packageName);

        if (!ret.isEmpty()) {
          classes.addAll(ret);
        }
      }
    }

    return classes;
  }

  private ArrayList<Class<?>> loadCleasses(URL url, String packageName) {
    ArrayList<Class<?>> classes = null;
    File jar = new File(url.getFile());

    if (jar.isFile()) {
      classes = loadCleassesFromFile(jar, packageName);
    } else {
      File[] jarFileList = jar.listFiles();

      for (File file : jarFileList) {
        classes = loadCleassesFromFile(file, packageName);
      }
    }

    return classes;
  }

  private ArrayList<Class<?>> loadCleassesFromFile(File file, String packageName) {
    ArrayList<Class<?>> classes = new ArrayList<>();

    try (ZipInputStream zip = new ZipInputStream(new FileInputStream(file))) {
      for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
          String className = entry.getName().replace('/', '.'); // including ".class"

          if (className.startsWith(packageName)) {
            classes.add(
                Class.forName(className.substring(0, className.length() - ".class".length())));
          }
        }
      }
    } catch (FileNotFoundException e) {
      // The expected error in the jar file cannot be found here, and the subsequent process should
      // be executed with the class file compiled by the machine.
    } catch (Exception e) {
      e.printStackTrace();
    }
    return classes;
  }

  private List<Class<?>> findClasses(File directory, String packageName)
      throws ClassNotFoundException {
    List<Class<?>> classes = new ArrayList<Class<?>>();

    if (!directory.exists()) {
      return classes;
    }

    File[] files = directory.listFiles();

    for (File file : files) {
      if (file.isDirectory()) {
        classes.addAll(findClasses(file, packageName + "." + file.getName()));
      } else if (file.getName().endsWith(".class")) {
        classes.add(
            Class.forName(
                packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
      }
    }

    return classes;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();

    serviceClass.forEach((key, value) -> str.append(key + "=" + value + System.lineSeparator()));

    return "Services[" + str.toString() + "]";
  }

  private static class ServiceHolder<T> {
    private Class<T> serviceClass;
    private T cacheInstance;
    private boolean cache;

    public ServiceHolder(Class<T> clazz, boolean cache) {
      serviceClass = clazz;
      this.cache = cache;
    }

    public T getServiceInstance() throws InstantiationException, IllegalAccessException {
      T obj = null;

      if (cache) {
        obj = cacheInstance;

        if (obj == null) {
          synchronized (this) {
            obj = cacheInstance;

            if (obj == null) {
              obj = serviceClass.newInstance();
              cacheInstance = obj;
            }
          }
        }
      } else {
        obj = serviceClass.newInstance();
      }

      return obj;
    }

    @Override
    public String toString() {
      return "service["
          + serviceClass
          + "],cache["
          + cache
          + "],cacheInstance["
          + cacheInstance
          + "]";
    }
  }
}
