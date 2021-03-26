package com.oop.inteliframework.menu.interfaces;

import com.oop.inteliframework.menu.component.ComponentHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Supplier;

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
