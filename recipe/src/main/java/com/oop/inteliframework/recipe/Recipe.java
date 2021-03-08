package com.oop.inteliframework.recipe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Recipe {
    ItemStack getResult(Player player);
}
