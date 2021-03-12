package com.oop.inteliframework.item.type.potion;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public class InteliPotionItem extends AbstractInteliItem<InteliPotionMeta, InteliPotionItem> {
  public InteliPotionItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliPotionItem(s.asBukkitStack().clone()));

    if (!InteliMaterial.matchMaterial(itemStack).isPotion())
      throw new UnsupportedOperationException("Material must be potion!");
  }
}
