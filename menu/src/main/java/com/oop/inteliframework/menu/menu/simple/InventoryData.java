package com.oop.inteliframework.menu.menu.simple;

import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.attribute.Attributes;
import com.oop.inteliframework.menu.slot.InteliSlot;
import com.oop.inteliframework.menu.util.InventoryUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryData implements com.oop.inteliframework.menu.interfaces.InventoryData {

  @Getter private final InteliMenu menu;
  @Getter private InteliSlot[] slots;
  @Getter private Inventory inventory;
  @Getter @Setter private String title;

  public InventoryData(InteliMenu menu, int size, Inventory inventory) {
    slots = new InteliSlot[size];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = new InteliSlot(i);
    }
    this.menu = menu;
    this.inventory = inventory;
  }

  @Override
  public void updateTitle(String newTitle) {
    if (!menu.isCurrentlyOpen()) {
      inventory = copy(newTitle);
      return;
    }

    InventoryUtil.updateTitle(inventory, menu.getViewer().orElse(null), newTitle);
  }

  @Override
  public void updateSlots(int... slots) {
    if (inventory == null) {
      return;
    }

    boolean hasOpened = menu.isCurrentlyOpen();
    for (int slot : slots) {
      ItemStack itemStack;
      Optional<AbstractInteliItem<?, ?>> optional = menu.requestItem(slot);
      if (!optional.isPresent()) {
        itemStack = new ItemStack(Material.AIR);
      } else {
        menu.preSetItem(optional.get());
        itemStack = optional.get().asBukkitStack();
      }

      if (hasOpened) {
        InventoryUtil.updateItem(menu.getViewer().orElse(null), slot, itemStack);
        continue;
      }

      inventory.setItem(slot, itemStack);
    }
  }

  @Override
  public Player getViewer() {
    return menu.getViewer().orElse(null);
  }

  public void getDataFrom(InventoryData data) {
    this.slots = data.slots;
    this.title = data.title;
  }

  public InteliSlot[] getFreeSlots() {
    List<InteliSlot> freeSlots = new LinkedList<>();
    for (InteliSlot slot : slots) {
      if (!slot.getHolder().isPresent()
          || slot.getHolder()
              .get()
              .getComponent(AttributeComponent.class)
              .map(ac -> ac.hasAttribute(Attributes.PLACEHOLDER))
              .orElse(false)) {
        freeSlots.add(slot);
      }
    }
    return freeSlots.toArray(new InteliSlot[0]);
  }

  public Optional<InteliSlot> findSlot(Predicate<InteliSlot> slotPredicate) {
    return Arrays.stream(slots).filter(slotPredicate).findFirst();
  }

  public Collection<InteliSlot> findSlots(Predicate<InteliSlot> slotPredicate) {
    return Arrays.stream(slots).filter(slotPredicate).collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return "IInventoryData{"
        + "slots="
        + Arrays.toString(Arrays.stream(slots).map(InteliSlot::toString).toArray())
        + '}';
  }

  private Inventory copy(String title) {
    Inventory copy =
        Bukkit.createInventory(
            menu, inventory.getSize(), ChatColor.translateAlternateColorCodes('&', title));
    for (int i = 0; i < inventory.getSize(); i++) {
      copy.setItem(i, inventory.getContents()[i]);
    }
    return copy;
  }

  public void updateItem(int index, ItemStack itemStack) {
    InventoryUtil.updateItem(menu.getViewer().orElse(null), index, itemStack);
  }
}
