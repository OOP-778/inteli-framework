package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeValuable;
import lombok.NonNull;

import static com.oop.inteliframework.config.util.Helper.isPrimitive;

/**
 * Primitive property is for base values
 *
 * @param <T> the type of object that property holds
 */
public class PrimitiveProperty<T> implements Property<T> {
    protected T object;

    protected PrimitiveProperty(T object) {
        this.object = object;
    }

    public static Property<Integer> fromInt(int value) {
        return new PrimitiveProperty<>(value);
    }

    public static Property<Double> fromDouble(double value) {
        return new PrimitiveProperty<>(value);
    }

    public static Property<Float> fromFloat(float value) {
        return new PrimitiveProperty<>(value);
    }

    public static Property<Long> fromLong(long value) {
        return new PrimitiveProperty<>(value);
    }

    public static Property<Boolean> fromBoolean(boolean value) {
        return new PrimitiveProperty<>(value);
    }

    /**
     * Tries to get a primitive property from an object of unknown type
     *
     * @param object the unknown type object
     * @return property of unknown type
     * @throws IllegalStateException if the object is not a primitive, it will throw an error
     */
    public static <T> Property<T> fromObject(@NonNull T object) throws IllegalStateException {
        Preconditions.checkArgument(
                isPrimitive(object.getClass()),
                "Unknown object is not a primitive type, it's " + object.getClass().getSimpleName()
        );

        return new PrimitiveProperty<>(object);
    }

    @Override
    public Node toNode(String key) {
        return new NodeValuable(key, null, object);
    }

    @Override
    public T get() {
        Preconditions.checkArgument(object != null, "Object is not present.");
        return object;
    }

    @Override
    public Class<T> type() {
        return (Class<T>) object.getClass();
    }

    /**
     * Mutable version of primitive property
     */
    public static class Mutable<T> extends PrimitiveProperty<T> implements MutableProperty<T, Mutable<T>> {
        protected Mutable(T object) {
            super(object);
        }

        public static Mutable<Integer> fromInt(int value) {
            return new Mutable<>(value);
        }

        public static Mutable<Integer> emptyInt() {
            return new Mutable<>(null);
        }

        public static Mutable<Double> fromDouble(double value) {
            return new Mutable<>(value);
        }

        public static Mutable<Double> emptyDouble() {
            return new Mutable<>(null);
        }

        public static Mutable<Float> fromFloat(float value) {
            return new Mutable<>(value);
        }

        public static Mutable<Float> emptyFloat() {
            return new Mutable<>(null);
        }

        public static Mutable<Long> fromLong(long value) {
            return new Mutable<>(value);
        }

        public static Mutable<Long> emptyLong() {
            return new Mutable<>(null);
        }

        public static Mutable<Boolean> fromBoolean(boolean value) {
            return new Mutable<>(value);
        }

        public static Mutable<Boolean> emptyBoolean() {
            return new Mutable<>(null);
        }

        /**
         * Tries to get a primitive property from an object of unknown type
         *
         * @param object the unknown type object
         * @return property of unknown type
         * @throws IllegalStateException if the object is not a primitive, it will throw an error
         */
        public static <T> Mutable<T> fromObject(@NonNull T object) throws IllegalStateException {
            Preconditions.checkArgument(
                    isPrimitive(object.getClass()),
                    "Unknown object is not a primitive type, it's " + object.getClass().getSimpleName()
            );

            return new Mutable<>(object);
        }

        @Override
        public Mutable<T> set(T object) {
            this.object = object;
            return this;
        }

        @Override
        public boolean isPresent() {
            return object != null;
        }
    }
}
