package com.oop.inteliframework.adapters;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.bukkit.Bukkit.getServer;

public class VersionedAdapters {
  public static final String version =
      getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);
  private static final Map<Class, VersionedAdapter> loadedAdapters = new HashMap<>();
  private static final Map<Class, Class<? extends VersionedAdapter>> loadedAdaptersClasses =
      new HashMap<>();

  /**
   * find loaded adapter as an object
   *
   * @param clazz the type of the adapter
   * @param <T> the type of the adapter
   * @return optional adapter
   */
  public static <T extends VersionedAdapter> Optional<T> find(Class<T> clazz) {
    return Optional.ofNullable((T) loadedAdapters.get(clazz));
  }

  /**
   * find loaded adapter class
   *
   * @param clazz the type of the adapter
   * @param <T> the type of the adapter
   * @return optional adapter
   */
  public static <T extends VersionedAdapter> Optional<Class<T>> findClass(Class<T> clazz) {
    return Optional.ofNullable(((Class<T>) loadedAdaptersClasses.get(clazz)));
  }

  /**
   * Loads an versioned adapter Example of versioned package is V1_8_R3
   *
   * @param basepath package of where to look for it
   * @param <T> the type of the adapter
   * @return optional adapter
   */
  public static <T extends VersionedAdapter> Optional<T> load(
      String basepath, String className, Class<T> baseClass) {
    // BasePath + version + ClassName
    final String path = basepath + "." + version + "." + className;

    if (loadedAdapters.containsKey(baseClass))
      return Optional.of((T) loadedAdapters.get(baseClass));

    try {
      Class<T> clazz = (Class<T>) Class.forName(path);
      Constructor<T> declaredConstructor = clazz.getDeclaredConstructor();
      declaredConstructor.setAccessible(true);

      T loadedAdapter = declaredConstructor.newInstance();
      loadedAdapters.put(baseClass, loadedAdapter);

      return Optional.of(loadedAdapter);
    } catch (Throwable throwable) {
      return Optional.empty();
    }
  }

  /**
   * Loads an versioned adapter class Example of versioned package is V1_8_R3
   *
   * @param basepath package of where to look for it
   * @param <T> the type of the adapter
   * @return optional adapter
   */
  public static <T extends VersionedAdapter> Optional<Class<T>> loadClass(
      String basepath, String className, Class<T> baseClass) {
    // BasePath + version + ClassName
    final String path = basepath + ".V" + version + "." + className;

    if (loadedAdaptersClasses.containsKey(baseClass))
      return Optional.of((Class<T>) loadedAdaptersClasses.get(baseClass));

    try {
      Class<T> clazz = (Class<T>) Class.forName(path);
      loadedAdaptersClasses.put(baseClass, clazz);

      return Optional.of(clazz);
    } catch (Throwable throwable) {
      return Optional.empty();
    }
  }
}
