package com.oop.intelimenus.interfaces;

public interface Modifier<T> {

    default void onAdd(T object) {
    }

    default void onRemove(T object) {
    }

}
