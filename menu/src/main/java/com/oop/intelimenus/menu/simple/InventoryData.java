package com.oop.intelimenus.menu.simple;

import com.oop.intelimenus.attribute.AttributeComponent;
import com.oop.intelimenus.attribute.Attributes;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import com.oop.intelimenus.slot.InteliSlot;
import com.oop.intelimenus.util.InventoryUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryData implements com.oop.intelimenus.interfaces.InventoryData {

    @Getter
    private InteliSlot[] slots;

    @Getter
    private Inventory inventory;

    @Getter
    @Setter
    private String title;

    @Getter
    private final InteliMenu menu;

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
            System.out.println("setting new title for menu without open: " + newTitle);
            inventory = copy(newTitle);
        } else {
            InventoryUtil.updateTitle(inventory, menu.getViewer().orElse(null), newTitle);
        }
    }

    @Override
    public void updateSlots(int... slots) {
        if (inventory == null) {
            return;
        }

        boolean hasOpened = menu.isCurrentlyOpen();
        for (int slot : slots) {
            ItemStack itemStack;
            Optional<MenuItemBuilder> optional = menu.requestItem(slot);
            if (!optional.isPresent()) {
                itemStack = new ItemStack(Material.AIR);
            } else {
                menu.preSetItem(optional.get());
                itemStack = optional.get().getItem();
            }

            if (hasOpened) {
                InventoryUtil.updateItem(menu.getViewer().orElse(null), slot, itemStack);
            } else {
                inventory.setItem(slot, itemStack);
            }
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
            if (!slot.getHolder().isPresent() || slot.getHolder().get()
                .getComponent(AttributeComponent.class)
                .map(ac -> ac.hasAttribute(Attributes.PLACEHOLDER)).orElse(false)) {
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
        return "IInventoryData{" +
            "slots=" + Arrays.toString(Arrays.stream(slots).map(InteliSlot::toString).toArray()) +
            '}';
    }

    private Inventory copy(String title) {
        Inventory copy = Bukkit.createInventory(menu, inventory.getSize(),
            ChatColor.translateAlternateColorCodes('&', title));
        for (int i = 0; i < inventory.getSize(); i++) {
            copy.setItem(i, inventory.getContents()[i]);
        }
        return copy;
    }

    public void updateItem(int index, ItemStack itemStack) {
        InventoryUtil.updateItem(menu.getViewer().orElse(null), index, itemStack);
    }
}
