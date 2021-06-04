package com.oop.inteliframework.commons.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Helper {
  public static <T> T apply(T object, Consumer<T> consumer) {
    consumer.accept(object);
    return object;
  }

  public static <T> T use(T object, Runnable consumer) {
    consumer.run();
    return object;
  }

  public static <T> T useAndProduce(T object, Function<T, T> producer) {
    return producer.apply(object);
  }
}
