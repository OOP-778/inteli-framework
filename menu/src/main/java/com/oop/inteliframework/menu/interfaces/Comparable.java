package com.oop.inteliframework.menu.interfaces;

public interface Comparable<T> {

  default boolean compare(T object) {
    return true;
  }
}
