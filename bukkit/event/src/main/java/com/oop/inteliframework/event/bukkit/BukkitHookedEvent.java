package com.oop.inteliframework.event.bukkit;

import com.oop.inteliframework.event.HookedEvent;
import com.oop.inteliframework.event.props.HookProperties;
import lombok.Getter;
import org.bukkit.event.Event;

@Getter
public class BukkitHookedEvent<T extends Event> extends HookedEvent<T> {

  protected BukkitEvent event;

  public BukkitHookedEvent(HookProperties<T> properties) {
    super(properties);
  }

  protected void setUnhook(Runnable runnable) {
    unhook = runnable;
  }
}
