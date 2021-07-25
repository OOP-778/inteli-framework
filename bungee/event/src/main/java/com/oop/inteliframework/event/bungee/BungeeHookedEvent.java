package com.oop.inteliframework.event.bungee;

import com.oop.inteliframework.event.HookedEvent;
import com.oop.inteliframework.event.props.HookProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Event;

@Getter
public class BungeeHookedEvent<T extends Event> extends HookedEvent<T> {

  @Setter(AccessLevel.PROTECTED)
  protected BungeeEventHandler<T> eventHandler;

  public BungeeHookedEvent(HookProperties<T> properties) {
    super(properties);
  }

  protected void setUnhook(Runnable runnable) {
    unhook = runnable;
  }
}
