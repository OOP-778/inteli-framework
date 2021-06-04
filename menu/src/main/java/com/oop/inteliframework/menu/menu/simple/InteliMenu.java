package com.oop.inteliframework.menu.menu.simple;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.menu.actionable.MenuAction;
import com.oop.inteliframework.menu.attribute.AttributeComponent;
import com.oop.inteliframework.menu.attribute.Attributes;
import com.oop.inteliframework.menu.button.IButton;
import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.interfaces.Menu;
import com.oop.inteliframework.menu.placholder.PlaceholderComponent;
import com.oop.inteliframework.menu.slot.InteliSlot;
import com.oop.inteliframework.menu.trigger.Trigger;
import com.oop.inteliframework.menu.trigger.TriggerComponent;
import com.oop.inteliframework.menu.trigger.types.ButtonClickTrigger;
import com.oop.inteliframework.menu.trigger.types.MenuCloseTrigger;
import com.oop.inteliframework.menu.trigger.types.MenuOpenTrigger;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.bukkit.BukkitTaskController;
import com.oop.inteliframework.task.bukkit.InteliBukkitTask;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class InteliMenu implements Menu<InteliMenu, InteliSlot, IButton> {

  private final Player viewer;
  @Getter private final Map<Class, Component> componentMap = new HashMap<>();
  @Getter private InteliSlot[] slots;
  @Getter @Setter private MenuAction currentAction = MenuAction.NONE;

  @Getter
  @Setter(AccessLevel.PROTECTED)
  private InventoryData inventoryData;

  @Getter private int rows;

  @Getter @Setter private Function<InteliMenu, String> titleSupplier;

  private Menu parent;
  private Menu moving;

  private ScheduledFuture<?> animationTask;

  private Supplier<Inventory> inventorySupplier;

  public InteliMenu(Player viewer, int rows, String title) {
    this.viewer = viewer;
    this.rows = rows;
    this.titleSupplier = menu -> title;

    setSize(rows * 9);
  }

  public InteliMenu(Player player) {
    this.viewer = player;
  }

  @Override
  public Optional<Player> getViewer() {
    return Optional.ofNullable(viewer);
  }

  @Override
  public void onClick(InventoryClickEvent event) {
    InteliSlot slot = getInventoryData().getSlots()[event.getSlot()];

    // Check if slot is locked
    boolean locked =
        slot.getComponent(AttributeComponent.class)
            .map(c -> c.hasAttribute(Attributes.LOCKED))
            .orElse(false);
    if (locked) {
      event.setCancelled(true);
      return;
    }

    if (slot.getComponent(AttributeComponent.class)
        .map(c -> c.hasAttribute(Attributes.CAN_BE_PICKED_UP))
        .orElse(false)) {
      handlePickup(event, slot);
      return;
    }

    IButton holder = slot.getHolder().orElse(null);
    if (holder != null) {
      // Check if button is locked
      if (holder
          .getComponent(AttributeComponent.class)
          .map(c -> c.hasAttribute(Attributes.LOCKED))
          .orElse(false)) {
        event.setCancelled(true);
        return;
      }

      // Check if slot holder can be picked up
      if (holder
          .getComponent(AttributeComponent.class)
          .map(c -> c.hasAttribute(Attributes.CAN_BE_PICKED_UP))
          .orElse(false)) {
        handlePickup(event, slot);
        return;
      }

      // Check for triggers
      holder
          .getComponent(TriggerComponent.class)
          .ifPresent(
              triggerComponent -> {
                ButtonClickTrigger trigger =
                    new ButtonClickTrigger(
                        this,
                        holder,
                        slot.getIndex(),
                        event.getClick(),
                        event.getAction(),
                        (Player) event.getWhoClicked());
                List<Trigger<ButtonClickTrigger>> triggers = triggerComponent.triggers(trigger);
                if (triggers.isEmpty()) {
                  event.setCancelled(true);
                  return;
                }

                triggers.forEach(t -> t.getFinalExecutor().accept(trigger));

                if (trigger.isCancelled()) {
                  event.setCancelled(true);
                }
              })
          .elseNot(() -> event.setCancelled(true));
    }
  }

  public void handlePickup(InventoryClickEvent event, InteliSlot slot) {
    // TODO: Implement pickuping of items
  }

  @Override
  public void onClose(InventoryCloseEvent event) {
    getComponent(TriggerComponent.class)
        .ifPresent(
            component -> {
              MenuCloseTrigger trigger = new MenuCloseTrigger(this, getViewer().get());
              for (Trigger<MenuCloseTrigger> menuCloseTriggerTrigger :
                  component.triggers(trigger)) {
                menuCloseTriggerTrigger.getFinalExecutor().accept(trigger);
              }
            });

    if (animationTask != null && !animationTask.isCancelled()) {
      animationTask.cancel(true);
    }
  }

  @Override
  public void onDrag(InventoryDragEvent event) {}

  @Override
  public void onOpen(InventoryOpenEvent event) {
    if (animationTask != null && !animationTask.isCancelled()) {
      animationTask.cancel(true);
    }

    getComponent(TriggerComponent.class)
        .ifPresent(
            component -> {
              MenuOpenTrigger trigger = new MenuOpenTrigger(this, getViewer().get());
              for (Trigger<MenuOpenTrigger> menuCloseTriggerTrigger : component.triggers(trigger)) {
                menuCloseTriggerTrigger.getFinalExecutor().accept(trigger);
              }
            });

    //    animationTask =
    //        InteliMenuComponent.getInteliMenus()
    //            .getScheduler()
    //            .scheduleAtFixedRate(
    //                () -> {
    //
    // getComponent(AnimationComponent.class).ifPresent(AnimationComponent::execute);
    //                  for (InteliSlot slot :
    //                      getInventoryData()
    //                          .findSlots(
    //                              slot ->
    //                                  slot.getHolder().isPresent()
    //                                      && slot.getHolder()
    //                                          .get()
    //                                          .getComponent(AnimationComponent.class)
    //                                          .isPresent())) {
    //
    // slot.getHolder().get().getComponent(AnimationComponent.class).get().execute();
    //                  }
    //                },
    //                2,
    //                2,
    //                TimeUnit.MILLISECONDS);
  }

  @Override
  public void onBottomClick(InventoryClickEvent event) {}

  @Override
  public void setMoving(Menu where) {
    this.moving = where;
  }

  @Override
  public Optional<Menu> getCurrentMoving() {
    return Optional.ofNullable(moving);
  }

  @Override
  public Optional<Menu> getParent() {
    return Optional.ofNullable(parent);
  }

  @Override
  public void setParent(Menu parent) {
    this.parent = parent;
  }

  @Override
  public Inventory getInventory(boolean rebuild) {
    if (inventoryData == null) {
      return buildMenu();
    }
    return !rebuild ? inventoryData.getInventory() : buildMenu();
  }

  @Override
  public InteliMenu setSlot(@NonNull InteliSlot slot) {
    slots[slot.getIndex()] = slot;
    return this;
  }

  @Override
  public InteliMenu setSlot(int slot, IButton button) {
    InteliSlot inteliSlot = slots[slot];
    inteliSlot.setHolder(button);
    return this;
  }

  @Override
  public InteliMenu clone() {
    return null;
  }

  @Override
  public Inventory buildMenu() {
    String title = preSetTitle(titleSupplier.apply(this));
    Inventory inventory =
        Bukkit.createInventory(this, rows * 9, ChatColor.translateAlternateColorCodes('&', title));
    inventoryData = new InventoryData(this, rows * 9, inventory);
    inventoryData.setTitle(title);

    for (InteliSlot slot : slots) {
      if (slot.getIndex() >= inventory.getSize()) {
        continue;
      }
      requestItem(slot.getIndex())
          .ifPresent(
              item -> {
                preSetItem(item);

                inventory.setItem(slot.getIndex(), item.asBukkitStack());
                InteliSlot slotClone = slot.clone();

                slotClone
                    .getHolder()
                    .ifPresent(
                        holder -> {
                          holder.setCurrentItem(item::asBukkitStack);
                          holder.setCurrentMenu(this);
                          holder.setParent(slotClone);
                        });

                inventoryData.getSlots()[slot.getIndex()] = slotClone;
              });
    }

    return inventory;
  }

  @Override
  public void open(Menu object, Runnable callback) {
    Preconditions.checkArgument(
        getViewer().isPresent(), "Failed to open a menu, cause there's no assigned player!");

    if (Bukkit.isPrimaryThread()) {
      new InteliBukkitTask(
              InteliPlatform.getInstance()
                  .safeModuleByClass(InteliTaskFactory.class)
                  .controllerByClass(BukkitTaskController.class)
                  .get())
          .body($ -> open(object, callback))
          .run();
      return;
    }

    Inventory inventory =
        object.getInventory(
            getComponent(AttributeComponent.class)
                .map(comp -> comp.hasAttribute(Attributes.REBUILD_ON_OPEN))
                .isPresent());
    Player player = viewer.getPlayer();
    if (player == null) {
      return;
    }

    new InteliBukkitTask(
            InteliPlatform.getInstance()
                .safeModuleByClass(InteliTaskFactory.class)
                .controllerByClass(BukkitTaskController.class)
                .get())
        .sync(true)
        .body(
            $ -> {
              player.openInventory(inventory);

              if (callback != null) {
                callback.run();
              }
            })
        .run();
  }

  protected void setSize(int size) {
    Preconditions.checkArgument(size % 9 == 0, "Invalid Inventory size: " + size);

    rows = size / 9;
    slots = new InteliSlot[size];
    for (int i = 0; i < slots.length; i++) {
      slots[i] = new InteliSlot(i);
    }
  }

  protected void preSetItem(AbstractInteliItem<?, ?> builder) {}

  protected String preSetTitle(String title) {
    String[] newTitle = new String[] {title};
    getComponent(PlaceholderComponent.class)
        .ifPresent(pc -> pc.getPlaceholders().forEach(f -> newTitle[0] = f.apply(newTitle[0])));
    return newTitle[0];
  }

  @Override
  public String toString() {
    return "IMenu{"
        + "slots="
        + Arrays.toString(slots)
        + ", viewer="
        + viewer
        + ", currentAction="
        + currentAction
        + ", inventoryData="
        + inventoryData
        + ", componentMap="
        + Arrays.toString(componentMap.values().stream().map(Objects::toString).toArray())
        + ", rows="
        + rows
        + ", parent="
        + parent
        + ", moving="
        + moving
        + '}';
  }

  public boolean isCurrentlyOpen() {
    return inventoryData != null
        && getViewer()
            .get()
            .getOpenInventory()
            .getTopInventory()
            .equals(inventoryData.getInventory());
  }
}
