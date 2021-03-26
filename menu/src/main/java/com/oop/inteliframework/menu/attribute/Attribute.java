package com.oop.inteliframework.menu.attribute;

import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.interfaces.Menu;
import com.oop.inteliframework.menu.interfaces.MenuButton;
import com.oop.inteliframework.menu.interfaces.MenuSlot;

public interface Attribute {

  String getId();

  boolean applyableToMenus();

  boolean applyableToSlots();

  boolean applyableToButtons();

  default boolean accepts(Component<? extends AttributeComponent> component) {
    if (component instanceof MenuButton) {
      return applyableToButtons();
    } else if (component instanceof Menu) {
      return applyableToMenus();
    } else if (component instanceof MenuSlot) {
      return applyableToSlots();
    }

    throw new IllegalStateException(
        "Attribute is not yet implemented on " + component.getClass().getSimpleName());
  }
}
