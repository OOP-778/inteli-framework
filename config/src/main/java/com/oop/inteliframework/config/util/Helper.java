package com.oop.inteliframework.config.util;

import com.google.gson.internal.Primitives;
import com.oop.inteliframework.commons.util.SimpleReflection;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.SneakyThrows;

public class Helper {

    public static boolean isPrimitive(Class<?> clazz) {
        Class<?> primitiveClass = Primitives.unwrap(clazz);
        return primitiveClass == int.class
            || primitiveClass == double.class
            || primitiveClass == float.class
            || primitiveClass == long.class
            || primitiveClass == boolean.class
            || primitiveClass == String.class;
    }

    public static <T extends Collection> T cloneCollection(T collection) {
        try {
            return (T) SimpleReflection
                .getConstructor(collection.getClass(), Collection.class)
                .newInstance(collection);
        } catch (Throwable throwable) {
            throw new IllegalStateException(
                "Collection of type " + collection.getClass().getSimpleName()
                    + " doesn't contain an constructor with collection param");
        }
    }

    @SneakyThrows
    public static <K, V, T extends Map<K, V>> T cloneMap(T map) {
        T newMap = (T) map.getClass().newInstance();
        map.forEach(newMap::put);

        return newMap;
    }

    public static <T> T apply(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }

    public static <T> T use(T object, Runnable consumer) {
        consumer.run();
        return object;
    }

    public static <T> T useAndProduce(T object, Function<T, T> producer) {
        return producer.apply(object);
    }
}
