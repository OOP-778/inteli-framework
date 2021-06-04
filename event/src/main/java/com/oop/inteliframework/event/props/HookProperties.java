package com.oop.inteliframework.event.props;

import com.oop.inteliframework.event.HookedEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

@Accessors(fluent = true)
@Getter
public class HookProperties<T> {

  @Setter @NonNull private BiConsumer<T, HookedEvent<T>> onCall;

  @Setter
  // Filter out hook
  private BiPredicate<T, HookedEvent<T>> filter = ($, $1) -> true;

  @Setter
  // Priority of the hook
  private Priority priority = Priority.LOW;

  @Setter
  // Run till condition is true
  private BiPredicate<T, HookedEvent<T>> till = ($, $1) -> false;

  // Hook timeout
  private HookTimeout<T> timeout;

  public HookProperties<T> timeout(Consumer<HookTimeout<T>> consumer) {
    this.timeout = new HookTimeout<>();
    consumer.accept(timeout);

    return this;
  }

  public static enum Priority {
    LOW,
    HIGH,
    HIGHEST,
    MONITOR
  }
}
