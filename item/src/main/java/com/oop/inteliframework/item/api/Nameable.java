package com.oop.inteliframework.item.api;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Simple interface which allow you to make module nameable
 *
 * <p>Provide all required methods
 */
public interface Nameable<T> {

  /**
   * @param name Item name
   * @throws NullPointerException If name is null
   */
  T name(final @NonNull String name);

  /**
   * @param supplier Item name supplier
   * @throws NullPointerException If name is null
   */
  T nameSupplier(final @NonNull Supplier<String> supplier);

  /** @return Item current name */
  @Nullable
  String name();
}
