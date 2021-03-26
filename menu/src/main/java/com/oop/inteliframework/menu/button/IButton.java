package com.oop.inteliframework.menu.button;

import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.interfaces.MenuButton;
import com.oop.inteliframework.menu.menu.simple.InteliMenu;
import com.oop.inteliframework.menu.slot.InteliSlot;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class IButton implements MenuButton<IButton> {

  @Setter private Supplier<ItemStack> currentItem;

  @Getter @Setter private InteliSlot parent;

  @Getter @Setter private InteliMenu currentMenu;

  @Getter private Map<Class, Component> componentMap = new ConcurrentHashMap<>();

  @Override
  public Optional<Supplier<ItemStack>> getCurrentItem() {
    return Optional.ofNullable(currentItem);
  }

  @Override
  public IButton clone() {
    IButton button = new IButton();
    button.currentItem = Optional.ofNullable(currentItem).orElse(null);

    button.componentMap = cloneComponents();
    button.currentMenu = currentMenu;
    button.parent = parent;
    button.componentMap.values().forEach(comp -> comp.onAdd(button));
    return button;
  }

  @Override
  public String toString() {
    return "IButton{"
        + "currentItem="
        + currentItem
        + ", components="
        + Arrays.toString(componentMap.values().stream().map(Objects::toString).toArray())
        +
        //                ", parent" + parent == null ? "null" : "present" +
        //                ", menu" + currentMenu == null ? "null" : "present" +
        '}';
  }
}
