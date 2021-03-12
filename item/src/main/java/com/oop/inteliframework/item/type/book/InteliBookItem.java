package com.oop.inteliframework.item.type.book;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public class InteliBookItem extends AbstractInteliItem<InteliBookMeta, InteliBookItem> {
  public InteliBookItem(@NonNull ItemStack itemStack) {
    super(itemStack, s -> new InteliBookItem(s.asBukkitStack().clone()));

    if (InteliMaterial.matchMaterial(itemStack).isWritableBook())
      throw new UnsupportedOperationException("Material must be writable book!");
  }
}
