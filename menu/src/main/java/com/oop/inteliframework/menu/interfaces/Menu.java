package com.oop.inteliframework.menu.interfaces;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.menu.actionable.Actionable;
import com.oop.inteliframework.menu.button.state.StateRequestComponent;
import com.oop.inteliframework.menu.component.ComponentHolder;
import com.oop.inteliframework.menu.slot.InteliSlot;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Menu<M extends Menu, S extends MenuSlot, B extends MenuButton>
    extends InventoryHolder, Actionable<Menu>, Comparable<Menu>, ComponentHolder<M> {

  /*
  Get viewer of the menu
  */
  Optional<Player> getViewer();

  /*
  Get slots of menu
  */
  S[] getSlots();

  /*
  Get current inventory data
  */
  InventoryData getInventoryData();

  /*
  When menu gets clicked
  */
  void onClick(InventoryClickEvent event);

  /*
  When menu gets closed
  */
  void onClose(InventoryCloseEvent event);

  /*
  When item gets dragged out & in menu
  */
  void onDrag(InventoryDragEvent event);

  /*
  When menu gets open
  */
  void onOpen(InventoryOpenEvent event);

  /*
  When player clicks bottom inventory
  */
  void onBottomClick(InventoryClickEvent event);

  /*
  Build the menu
  */
  Inventory buildMenu();

  @Override
  default Inventory getInventory() {
    return getInventory(false);
  }

  /*
  Get inventory of the menu
  */
  Inventory getInventory(boolean rebuild);

  /*
  Set menu slot
  */
  M setSlot(S slot);

  /*
  Set menu slot to button
  */
  M setSlot(int slot, B button);

  /*
  Edit slots from starting slot and ending
  */
  default M applyToSlots(int start, int end, @NonNull Consumer<S> consumer) {
    for (int slot = start; slot < end; slot++) {
      if (getSlots().length < slot) {
        break;
      }
      S menuSlot = getSlots()[slot];
      consumer.accept(menuSlot);
    }

    return (M) this;
  }

  /*
  Requests an itemstack from slot
  */
  default Optional<AbstractInteliItem<?, ?>> requestItem(int slot) {
    Preconditions.checkArgument(
        slot < getSlots().length,
        "Cannot request an item at an invalid slot! (" + slot + "/" + getSlots().length + ")");

    final S slotObj = getSlots()[slot];
    final B button = (B) getSlots()[slot].getHolder().orElse(null);
    final AbstractInteliItem<?, ?>[] builder = {null};

    // Request an state if component there
    getComponent(StateRequestComponent.class)
        .map(c -> c.find((InteliSlot) slotObj))
        .ifPresent(f -> builder[0] = f.apply((InteliSlot) slotObj).clone());

    if (builder[0] == null && button != null && button.getCurrentItem().isPresent()) {
      builder[0] = new InteliItem(((Supplier<ItemStack>) button.getCurrentItem().get()).get());
    }

    // Replace with placeholders if component there
    //    getComponent(PlaceholderComponent.class)
    //        .ifPresent(c -> c.getPlaceholders().forEach(builder[0]::replace));

    return Optional.ofNullable(builder[0]);
  }

  /*
  Clone menu
  */
  M clone();
}
