package com.oop.inteliframework.item.type.item;

import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class InteliItem extends AbstractInteliItem<InteliItemMeta, InteliItem> {
  public InteliItem(final @NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliItem(s.asBukkitStack().clone()));
  }

  @Override
  protected InteliItemMeta _createMeta() {
    return new InteliItemMeta(
        !asBukkitStack().hasItemMeta()
            ? Bukkit.getItemFactory().getItemMeta(asBukkitStack().getType())
            : asBukkitStack().getItemMeta());
  }
}
