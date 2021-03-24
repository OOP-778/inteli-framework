package com.oop.inteliframework.config.property.property;

/**
 * Used for mutable properties that can be changed and nullable
 *
 * @param <T> the type of the object that property holds
 */
public interface MutableProperty<T, P> extends Property<T> {

    P set(T object);

    boolean isPresent();
}
