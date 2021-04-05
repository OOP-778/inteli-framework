package com.oop.inteliframework.config.property.property;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ValueNode;
import lombok.NonNull;
import lombok.ToString;

import static com.oop.inteliframework.config.property.util.Helper.isPrimitive;

/**
 * Primitive property is for base values
 *
 * @param <T> the type of object that property holds
 */
@ToString
public class PrimitiveProperty<T> implements Property<T> {

    protected T object;
    protected Class<T> type;

    protected PrimitiveProperty(T object, Class<T> type) {
        this.object = object;
        this.type = type;
    }

    public static Property<Integer> fromInt(int value) {
        return new PrimitiveProperty<>(value, int.class);
    }

    public static Property<Double> fromDouble(double value) {
        return new PrimitiveProperty<>(value, double.class);
    }

    public static Property<Float> fromFloat(float value) {
        return new PrimitiveProperty<>(value, float.class);
    }

    public static Property<Long> fromLong(long value) {
        return new PrimitiveProperty<>(value, long.class);
    }

    public static Property<Boolean> fromBoolean(boolean value) {
        return new PrimitiveProperty<>(value, boolean.class);
    }

    public static Property<String> fromString(String string) {
        return new PrimitiveProperty<>(string, String.class);
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

        return new PrimitiveProperty<>(object, (Class<T>) object.getClass());
    }

    @Override
    public SerializedProperty toNode() {
       return SerializedProperty.of(new BaseValueNode(object));
    }

    @Override
    public void fromNode(Node node) {
        Preconditions.checkArgument(
                node instanceof ValueNode,
                "Primitive property only accepts ValueNodes!"
        );
        this.object = ((ValueNode) node).getAs(type);
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
    public static class Mutable<T> extends PrimitiveProperty<T> implements
            MutableProperty<T, Mutable<T>> {

        protected Mutable(T object, Class<T> type) {
            super(object, type);
        }

        public static Mutable<Integer> fromInt(int value) {
            return new Mutable<>(value, int.class);
        }

        public static Mutable<Integer> emptyInt() {
            return new Mutable<>(null, int.class);
        }

        public static Mutable<Double> fromDouble(double value) {
            return new Mutable<>(value, double.class);
        }

        public static Mutable<Double> emptyDouble() {
            return new Mutable<>(null, double.class);
        }

        public static Mutable<Float> fromFloat(float value) {
            return new Mutable<>(value, float.class);
        }

        public static Mutable<Float> emptyFloat() {
            return new Mutable<>(null, float.class);
        }

        public static Mutable<Long> fromLong(long value) {
            return new Mutable<>(value, long.class);
        }

        public static Mutable<Long> emptyLong() {
            return new Mutable<>(null, long.class);
        }

        public static Mutable<Boolean> fromBoolean(boolean value) {
            return new Mutable<>(value, boolean.class);
        }

        public static Mutable<Boolean> emptyBoolean() {
            return new Mutable<>(null, boolean.class);
        }

        public static Mutable<String> emptyString() {
            return new Mutable<>(null, String.class);
        }

        public static Mutable<String> fromString(@NonNull String string) {
            return new Mutable<>(string, String.class);
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

            return new Mutable<>(object, (Class<T>) object.getClass());
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
