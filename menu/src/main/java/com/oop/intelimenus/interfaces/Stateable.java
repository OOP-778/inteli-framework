package com.oop.intelimenus.interfaces;

import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public interface Stateable<T extends Stateable> {

    Map<String, MenuItemBuilder> getStates();

    default T registerDefaultState(ItemStack itemStack) {
        registerState("default", itemStack);
        return (T) this;
    }

    default T registerState(String id, ItemStack itemStack) {
        getStates().remove(id.toLowerCase());
        getStates().put(id.toLowerCase(), MenuItemBuilder.of(itemStack));
        return (T) this;
    }

    default Optional<MenuItemBuilder> getDefaultState() {
        return getState("default");
    }

    default Optional<MenuItemBuilder> getState(String id) {
        return Optional.ofNullable(getStates().get(id.toLowerCase()));
    }
}
