package com.oop.intelimenus.actionable;

public interface Openable<T> {

    void open(T object, Runnable callback);
}
