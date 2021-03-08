package com.oop.inteliframework.recipe;

import com.oop.inteliframework.commons.util.InteliPair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AllArgsConstructor
@Getter
public class FoundRecipe {
    private Recipe recipe;

    // Map containing slot, (in recipe ItemStack, inv ItemStack)
    private Map<Integer, InteliPair<ItemStack, ItemStack>> inventoryData;
}
