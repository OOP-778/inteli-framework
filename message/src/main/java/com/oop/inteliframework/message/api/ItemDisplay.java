package com.oop.inteliframework.message.api;

import net.kyori.adventure.text.event.HoverEvent;

/** Because there's no universal item, we have interface */
public interface ItemDisplay extends Replaceable<ItemDisplay>, Cloneable {
  HoverEvent<HoverEvent.ShowItem> toHoverEvent();

  ItemDisplay clone();
}
