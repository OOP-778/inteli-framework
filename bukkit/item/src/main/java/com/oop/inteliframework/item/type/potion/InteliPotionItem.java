package com.oop.inteliframework.item.type.potion;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class InteliPotionItem extends AbstractInteliItem<InteliPotionMeta, InteliPotionItem> {
  public InteliPotionItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliPotionItem(s.asBukkitStack().clone()));

    if (!InteliMaterial.matchMaterial(itemStack).isPotion())
      throw new UnsupportedOperationException("Material must be potion!");
  }

  @Override
  protected InteliPotionMeta _createMeta() {
    return new InteliPotionMeta(
        (PotionMeta)
            (asBukkitStack().hasItemMeta()
                ? Bukkit.getItemFactory().getItemMeta(asBukkitStack().getType())
                : asBukkitStack().getItemMeta()));
  }
}
