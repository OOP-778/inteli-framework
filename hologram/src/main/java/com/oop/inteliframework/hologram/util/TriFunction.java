package com.oop.inteliframework.hologram.util;

public interface TriFunction<F, S, T, O> {
    O apply(F f, S s, T t);
}
