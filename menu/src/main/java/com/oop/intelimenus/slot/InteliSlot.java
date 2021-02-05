package com.oop.intelimenus.slot;

import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.interfaces.MenuSlot;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

public class InteliSlot implements MenuSlot<InteliSlot, IButton> {

    @Getter
    @Setter
    private int index;

    private IButton holder;

    @Getter
    private Map<Class, Component> componentMap = new HashMap<>();

    public InteliSlot(int index) {
        this.index = index;
    }

    @Override
    public Optional<IButton> getHolder() {
        return Optional.ofNullable(holder);
    }

    @Override
    public void setHolder(IButton button) {
        this.holder = button;
    }

    @Override
    public InteliSlot clone() {
        InteliSlot slot = new InteliSlot(index);
        slot.componentMap = cloneComponents();
        slot.componentMap.values().forEach(comp -> comp.onAdd(this));
        slot.holder = getHolder().map(IButton::clone).orElse(null);
        return slot;
    }

    @Override
    public String toString() {
        return "ISlot{" +
            "index=" + index +
            ", holder=" + holder +
            ", componentMap=" + Arrays
            .toString(componentMap.values().stream().map(Objects::toString).toArray()) +
            '}';
    }
}
