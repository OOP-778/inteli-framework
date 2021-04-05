package com.oop.inteliframework.config.property.cache;

import com.oop.inteliframework.config.property.property.Property;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClassesCache {
  // We need to store fields of classes
  private final Map<Class, List<Field>> fieldsCachePerClass = new HashMap<>();

  // Full fields cache
  private final Map<Class, List<Field>> fullClassFields = new HashMap<>();

  public List<Field> getFields(Class<?> clazz) {
    return new LinkedList<>(
        fullClassFields.computeIfAbsent(
            clazz,
            $ -> {
              List<Field> fullFields = new LinkedList<>();
              Class<?> currentClass = clazz;
              while (!currentClass.isInterface() && currentClass != Object.class) {
                Class<?> finalCurrentClass = currentClass;
                fullFields.addAll(
                    fieldsCachePerClass.computeIfAbsent(
                        currentClass,
                        $1 -> {
                          List<Field> fieldList = new LinkedList<>();
                          for (Field declaredField : finalCurrentClass.getDeclaredFields()) {
                            try {
                              declaredField.setAccessible(true);
                              if (!Property.class.isAssignableFrom(declaredField.getType())) {
                                continue;
                              }

                              fieldList.add(declaredField);
                            } catch (Throwable throwable) {
                              throwable.printStackTrace();
                            }
                          }
                          return fieldList;
                        }));

                currentClass = currentClass.getSuperclass();
              }

              return fullFields;
            }));
  }
}
