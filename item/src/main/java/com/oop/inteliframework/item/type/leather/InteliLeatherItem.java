package com.oop.inteliframework.item.type.leather;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class InteliLeatherItem extends AbstractInteliItem<InteliLeatherMeta, InteliLeatherItem> {
  public InteliLeatherItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliLeatherItem(s.asBukkitStack().clone()));

    if (!InteliMaterial.matchMaterial(itemStack).isLeatherArmor())
      throw new UnsupportedOperationException("Material must be part of leather armor!");
  }

  @Override
  protected InteliLeatherMeta _createMeta() {
    return new InteliLeatherMeta(
        (LeatherArmorMeta)
            (asBukkitStack().hasItemMeta()
                ? Bukkit.getItemFactory().getItemMeta(asBukkitStack().getType())
                : asBukkitStack().getItemMeta()));
  }
}
