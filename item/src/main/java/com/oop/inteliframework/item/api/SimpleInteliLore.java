package com.oop.inteliframework.item.api;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;

/**
 * Lore adapter
 * */
public interface SimpleInteliLore<T extends SimpleInteliLore> {

  /**
   * @param lines Lore lines
   * @throws NullPointerException If lines is null
   */
  T append(final @NonNull String... lines);

  /**
   * @param line New lore line
   * @throws NullPointerException If lines is null
   */
  T append(final @NonNull String line);

  /**
   * @param lineNumber Line number
   * @param supplier Lore supplier (Will replace specified line number)
   *     <p>If lineNumber will be out of array, nothing gonna changed.
   * @throws NullPointerException If replacer is null
   */
  T replace(final int lineNumber, final @NonNull Function<String, String> supplier);

  /**
   * @param filter Lore lines filter
   * @param supplier Lore supplier (Will replace all founded lines)
   * @throws NullPointerException If linesPredicate or replacer null
   */
  T replace(final @NonNull Predicate<String> filter, final @NonNull Function<String, String> supplier);

  /**
   * @param supplier Lore supplier (Help with replacing)
   * @throws NullPointerException If supplier is null
   */
  T supplier(final @NonNull Consumer<List<String>> supplier);

  /**
   * Will reset current lore and
   * apply new one
   *
   * @param newLore New lore
   * @throws NullPointerException If newLore is null
   * */
  T lore(final @NonNull List<String> newLore);

  /**
   * @return Built lore
   * */
  @NonNull List<String> lore();

}
