package com.oop.inteliframework.bukkit.nbt.nms;

import com.oop.inteliframework.adapters.VersionedAdapter;
import net.querz.nbt.tag.CompoundTag;
import org.bukkit.inventory.ItemStack;

public interface NBTTags extends VersionedAdapter {
  // Convert itemStack to an CompoundTag
  CompoundTag fromItemStack(ItemStack itemStack);

  // Set Items compoundTag
  ItemStack setItemStackTag(ItemStack itemStack, CompoundTag compoundTag);
}
