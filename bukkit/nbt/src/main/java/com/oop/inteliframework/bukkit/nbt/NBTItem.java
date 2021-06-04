package com.oop.inteliframework.bukkit.nbt;

import lombok.NonNull;
import lombok.SneakyThrows;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class NBTItem extends CompoundTag {

  @NonNull private ItemStack itemStack;

  @SneakyThrows
  public NBTItem(@NonNull ItemStack itemStack) {
    this.itemStack = itemStack;
    final CompoundTag tag = InteliNbt.getTags().fromItemStack(itemStack);

    for (Map.Entry<String, Tag<?>> stringTagEntry : tag) {
      put(stringTagEntry.getKey(), stringTagEntry.getValue());
    }
  }

  public ItemStack getItemStack() {
    return InteliNbt.getTags().setItemStackTag(itemStack, this);
  }
}
