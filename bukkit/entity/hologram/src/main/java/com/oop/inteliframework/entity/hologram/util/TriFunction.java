package com.oop.inteliframework.entity.hologram.util;

public interface TriFunction<F, S, T, O> {
  O apply(F f, S s, T t);
}
