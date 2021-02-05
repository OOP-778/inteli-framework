package com.oop.intelimenus.interfaces;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface InventoryData {

    MenuSlot[] getSlots();

    @NonNull Inventory getInventory();

    void updateTitle(String newTitle);

    void updateSlots(int... slots);

    Player getViewer();
}
