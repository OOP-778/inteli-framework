package com.oop.inteliframework.task.api;

import com.oop.inteliframework.task.type.InteliTask;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Here stored all setup methods for task
 *
 * @see InteliTask
 */
public interface Task<T extends Task> {

  /**
   * Task body (Required)
   *
   * @param consumer Task body which will be consumed on every call.
   */
  T body(final @NonNull Consumer<T> consumer);

  /**
   * Stop if condition is true
   *
   * @param predicate If predicate is true, task will be cancelled automatically.
   */
  T stopIf(final @NonNull Predicate<T> predicate);

  /**
   * Do after complete
   *
   * @param consumer After task will complete his work, this consumer will be called.
   */
  T afterComplete(final @NonNull Consumer<T> consumer);

  /**
   * Run task times
   *
   * @param times Set how many times task will be executed.
   */
  T runTimes(final int times);

  /** Runs task */
  T run();

  /** Cancel task */
  T cancel();

  /**
   * Delay
   *
   * @param delay Delay between task execution
   */
  T delay(final long delay);

  /**
   * Delay
   *
   * @param unit Time unit (Check {@link TimeUnit})
   * @param delay Delay between task execution
   */
  T delay(final @NonNull TimeUnit unit, final long delay);

  /**
   * Run task multiple times
   *
   * @param repeatable Is task repeatable. By default, false.
   */
  T repeatable(final boolean repeatable);

  /** @return Is task cancelled */
  boolean cancelled();

  /** @return Is task running multiple times */
  boolean repeatable();

  /** @return Stop if condition */
  @Nullable
  Predicate<T> stopIf();

  /** @return What task gonna do after complete */
  @Nullable
  Consumer<T> afterComplete();

  /** @return Task body */
  @NonNull
  Consumer<T> body();

  /** @return How many times task must be run */
  int runTimes();

  /**
   * Increment counter to cancel it
   *
   * @return Current counter
   */
  int incAndGetTimes();

  /** @return Task id */
  long taskId();

  /** @return Delay between execution */
  long delay();

  /** @return Built runnable */
  @NonNull
  Runnable runnable();
}
