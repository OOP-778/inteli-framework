package com.oop.inteliframework.config.property;


import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.property.Property;
import com.oop.inteliframework.config.property.property.custom.PropertyHandler;
import com.oop.inteliframework.plugin.module.InteliModule;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.property.util.Serializer.getPropertiesOf;

public class InteliPropertyModule implements InteliModule {

    // This map stores custom object handlers
    public final Map<Class, PropertyHandler> propertyHandlerMap = new ConcurrentHashMap<>();

    // Stores all validated classes to not redo the validation
    public final Set<Class<? extends Configurable>> validatedClasses = new HashSet<>();

    public <T> Optional<PropertyHandler<T>> handlerByClass(Class<T> clazz) {
        return Optional
                .ofNullable(propertyHandlerMap.get(clazz))
                .map(o -> (PropertyHandler<T>) o);
    }

    public <T extends Configurable> void validate(Class<T> clazz) throws IllegalStateException {
        if (validatedClasses.contains(clazz))
            return;

        LinkedList<Field> propertiesOf = getPropertiesOf(clazz);

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
                format("Failed to find at least single property in class {}", clazz)
        );
        validatedClasses.add(clazz);
    }
}
