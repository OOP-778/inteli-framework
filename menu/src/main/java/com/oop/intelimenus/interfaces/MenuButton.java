package com.oop.intelimenus.interfaces;

import com.oop.intelimenus.component.ComponentHolder;
import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.inventory.ItemStack;

public interface MenuButton<T extends MenuButton<T>> extends Cloneable, ComponentHolder<T> {

    /*
    Get current item that's displayed inside inventory
    Will return empty if not set
    */
    Optional<Supplier<ItemStack>> getCurrentItem();

    /*
    Cloning of button
    */
    T clone();
}
