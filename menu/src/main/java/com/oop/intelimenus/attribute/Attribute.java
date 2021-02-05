package com.oop.intelimenus.attribute;

import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.interfaces.Menu;
import com.oop.intelimenus.interfaces.MenuButton;
import com.oop.intelimenus.interfaces.MenuSlot;

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
