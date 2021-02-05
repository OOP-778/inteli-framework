package com.oop.inteliframework.config.util;

import com.oop.inteliframework.config.property.Property;
import java.lang.reflect.Field;
import java.util.LinkedList;

public class ReflectionHelper {

    public static LinkedList<Field> getPropertiesOf(Class<?> clazz) {
        final LinkedList<Field> fields = new LinkedList<>();
        for (Field field : clazz.getFields()) {
            if (!field.getType().isAssignableFrom(Property.class)) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }
}
