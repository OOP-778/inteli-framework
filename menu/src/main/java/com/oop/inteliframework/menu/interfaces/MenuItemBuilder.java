package com.oop.inteliframework.menu.interfaces;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.oop.inteliframework.menu.InteliMenus.getInteliMenus;

public interface MenuItemBuilder<T extends MenuItemBuilder> extends Cloneable {

  static MenuItemBuilder of(@NonNull ItemStack item) {
    return getInteliMenus().getMenuItemBuilderFunction().apply(item.clone());
  }

  /*
  Returns item
  */
  ItemStack getItem();

  /*
  Replace item lore or display name with object
  */
  T replace(String what, Object to);

  /*
  Replace item lore or display name with function
  Usually used for placeholders supports
  */
  T replace(Function<String, String> parser);

  /*
  Returns lore of the item
  If not present, will return empty list
  */
  List<String> getLore();

  /*
  Set lore of the item
  */
  T lore(List<String> newLore);

  /*
  Append single line to lore
  */
  T appendLore(String line);

  /*
  Append coll of lines to lore
  */
  T appendLore(Collection<String> lines);

  /*
  Set display name of the item
  */
  T displayName(String newDisplayName);

  T clone();
}
