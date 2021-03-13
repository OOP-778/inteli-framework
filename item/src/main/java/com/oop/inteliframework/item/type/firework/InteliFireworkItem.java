package com.oop.inteliframework.item.type.firework;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class InteliFireworkItem extends AbstractInteliItem<InteliFireworkMeta, InteliFireworkItem> {
  public InteliFireworkItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliFireworkItem(s.asBukkitStack().clone()));

    if (InteliMaterial.matchMaterial(itemStack) != InteliMaterial.FIREWORK_ROCKET)
      throw new UnsupportedOperationException("Material must be banner!");
  }

  @Override
  protected InteliFireworkMeta _createMeta() {
    return new InteliFireworkMeta(
        (FireworkMeta)
            (asBukkitStack().hasItemMeta()
                ? Bukkit.getItemFactory().getItemMeta(asBukkitStack().getType())
                : asBukkitStack().getItemMeta()));
  }
}
