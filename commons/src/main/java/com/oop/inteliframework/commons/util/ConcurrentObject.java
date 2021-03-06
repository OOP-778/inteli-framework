package com.oop.inteliframework.commons.util;

import lombok.Getter;
import lombok.NonNull;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConcurrentObject<T> {
    private final ReentrantLock lock = new ReentrantLock();
    @Getter
    private @NonNull T object;

    public ConcurrentObject(T object) {
        this.object = object;
    }

    public void modify(Consumer<T> consumer) {
        lock.lock();
        consumer.accept(object);
        lock.unlock();
    }

    public <E> E use(Function<T, E> function) {
        preUse();
        E o = function.apply(object);
        postUse();
        return o;
    }

    public void lock(Function<T, T> function) {
        lock.lock();
        try {
            this.object = function.apply(object);
        } finally {
            lock.unlock();
        }
    }

    public void preUse() {
        lock.lock();
    }

    public void postUse() {
        lock.unlock();
    }
}
