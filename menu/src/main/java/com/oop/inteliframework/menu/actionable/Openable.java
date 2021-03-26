package com.oop.inteliframework.menu.actionable;

public interface Openable<T> {

  void open(T object, Runnable callback);
}
