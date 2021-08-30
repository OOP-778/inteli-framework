package com.oop.inteliframework.bukkit.nbt.nms;

import com.oop.inteliframework.adapters.VersionedAdapter;
import lombok.NonNull;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.inventory.ItemStack;

public interface NBTTags extends VersionedAdapter {
  // Convert itemStack to an CompoundTag
  CompoundTag fromItemStack(ItemStack itemStack);

  // Convert CompoundTag to itemStack
  ItemStack fromCompound(@NonNull CompoundTag tag);

  // Set Items compoundTag
  ItemStack setItemStackTag(ItemStack itemStack, CompoundTag compoundTag);
}
