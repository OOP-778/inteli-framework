package com.oop.inteliframework.item.type.banner;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public class InteliBannerItem extends AbstractInteliItem<InteliBannerMeta, InteliBannerItem> {
  public InteliBannerItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliBannerItem(s.asBukkitStack().clone()));

    if (!InteliMaterial.matchMaterial(itemStack).isBanner())
      throw new UnsupportedOperationException("Material must be banner!");
  }
}
