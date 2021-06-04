package com.oop.inteliframework.event;

import com.oop.inteliframework.event.props.AsyncHookProperties;
import com.oop.inteliframework.event.props.HookProperties;

public class HookedEvent<T> {
  private final HookProperties<T> properties;
  protected Runnable unhook;

  public HookedEvent(HookProperties<T> properties) {
    this.properties = properties;
  }

  public void handle(T event) {
    // If properties are async
    if (properties instanceof AsyncHookProperties) {
      if (((AsyncHookProperties<T>) properties).preCall() != null)
        ((AsyncHookProperties<T>) properties).preCall().accept(event, this);
    }

    // If filter is not null filter out
    if (properties.filter() != null) {
      if (!properties.filter().test(event, this)) return;
    }

    properties.onCall().accept(event, this);
  }

  public void unhook() {
    if (unhook == null) return;
    unhook.run();
  }
}
