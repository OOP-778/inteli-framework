package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.cache.ClassesCache;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class InteliPropertyModule implements InteliModule {

  // This map stores custom object handlers
  public final Map<Class, RegisteredHandler> propertyHandlerMap = new ConcurrentHashMap<>();

  // Stores all validated classes to not redo the validation
  public final Set<Class<?>> validatedClasses = new HashSet<>();

  // Classes cache
  @Getter private final ClassesCache classesCache = new ClassesCache();

  public static List<Class<?>> getSuperClasses(Class<?> clazz) {
    if (clazz == null) {
      return null;
    }

    List<Class<?>> classes = new ArrayList<>();
    Class<?> superclass = clazz.getSuperclass();
    while (superclass != null && superclass != Object.class) {
      classes.add(superclass);
      superclass = superclass.getSuperclass();
    }
    return classes;
  }

  public InteliPropertyModule registerHandler(PropertyHandler handler) {
    propertyHandlerMap.put(
        handler.getObjectClass(),
        new RegisteredHandler(handler.getObjectClass(), handler));
    return this;
  }

  public <T> Optional<PropertyHandler<T>> handlerByClass(Class<T> clazz) {
    /*
    We have two methods here.
    1. Check for registered handler by class
    2. Check for registered handler by super classes
    */

    RegisteredHandler<T> registeredHandler = propertyHandlerMap.get(clazz);
    if (registeredHandler != null) return Optional.of(registeredHandler.getHandler());

    List<Class<?>> superClasses = getSuperClasses(clazz);
    for (Class<?> superClass : superClasses) {
      registeredHandler = propertyHandlerMap.get(superClass);

      if (registeredHandler != null) {
        return Optional.of(registeredHandler.getHandler());
      }
    }

    return Optional.empty();
  }

  public <T> void validate(Class<T> clazz) throws IllegalStateException {
    if (validatedClasses.contains(clazz)) return;

    final List<Field> propertiesOf = classesCache.getFields(clazz);

    Field nodeKeyField = null;
    for (Field field : propertiesOf) {
      if (field.isAnnotationPresent(NodeKey.class)) {
        nodeKeyField = field;
        break;
      }
    }

    if (nodeKeyField != null && clazz.isAnnotationPresent(Named.class)) {
      throw new IllegalStateException(
          format(
              "class {} contains both a NodeKey & Named annotations, please remove one of them.",
              clazz.getSimpleName()));
    }

    // Properties amount check
    Preconditions.checkArgument(
        !propertiesOf.stream().allMatch(field -> field.isAnnotationPresent(NodeKey.class)),
        format("Failed to find at least single property in class {}", clazz));
    validatedClasses.add(clazz);
  }

  @AllArgsConstructor
  @Getter
  private static class RegisteredHandler<T> {
    private Class<T> baseClass;
    private PropertyHandler<T> handler;
  }
}
