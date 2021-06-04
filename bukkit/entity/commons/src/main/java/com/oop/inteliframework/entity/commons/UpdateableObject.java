package com.oop.inteliframework.entity.commons;

import lombok.Getter;

public class UpdateableObject<T> {

  private final Object lock = false;
  private T object;

  @Getter private boolean updated = true;

  public UpdateableObject(T object) {
    this.object = object;
  }

  public T get() {
    synchronized (lock) {
      updated = false;
      return object;
    }
  }

  public T current() {
    synchronized (lock) {
      return object;
    }
  }

  public void set(T object) {
    synchronized (lock) {
      this.object = object;
      updated = true;
    }
  }
}
