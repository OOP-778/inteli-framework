package com.oop.inteliframework.event.props;

import com.oop.inteliframework.event.HookedEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
@Setter
@Getter
public class HookTimeout<T> {

  private long time = -1;
  private Consumer<HookedEvent<T>> onceCalled;

  public HookTimeout<T> time(long amount, TimeUnit unit) {
    this.time = unit.toMillis(amount);
    return this;
  }
}
