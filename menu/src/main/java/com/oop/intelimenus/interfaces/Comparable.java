package com.oop.intelimenus.interfaces;

public interface Comparable<T> {

    default boolean compare(T object) {
        return true;
    }
}
