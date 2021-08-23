package com.oop.inteliframework.bukkit.nbt.nms.V1_16_R2;

import static com.oop.inteliframework.commons.util.StringFormat.format;

import com.oop.inteliframework.bukkit.nbt.nms.NBTTags;
import com.oop.inteliframework.bukkit.nbt.nms.TagIdToClass;
import java.util.Map;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R2.NBTBase;
import net.minecraft.server.v1_16_R2.NBTTagByte;
import net.minecraft.server.v1_16_R2.NBTTagByteArray;
import net.minecraft.server.v1_16_R2.NBTTagCompound;
import net.minecraft.server.v1_16_R2.NBTTagDouble;
import net.minecraft.server.v1_16_R2.NBTTagFloat;
import net.minecraft.server.v1_16_R2.NBTTagInt;
import net.minecraft.server.v1_16_R2.NBTTagIntArray;
import net.minecraft.server.v1_16_R2.NBTTagList;
import net.minecraft.server.v1_16_R2.NBTTagLong;
import net.minecraft.server.v1_16_R2.NBTTagShort;
import net.minecraft.server.v1_16_R2.NBTTagString;
import net.querz.nbt.tag.ByteArrayTag;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.DoubleTag;
import net.querz.nbt.tag.FloatTag;
import net.querz.nbt.tag.IntArrayTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.LongArrayTag;
import net.querz.nbt.tag.LongTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import net.querz.nbt.tag.Tag;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class ImplNBTTags implements NBTTags {

  @Override
  public CompoundTag fromItemStack(ItemStack itemStack) {
    final net.minecraft.server.v1_16_R2.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
    if (nmsCopy.getTag() == null) {
      return new CompoundTag();
    }

    return convertCompound(nmsCopy.getTag());
  }

  public Tag<?> convertTag(NBTBase nbt) {
    if (nbt instanceof NBTTagCompound) {
      return convertCompound((NBTTagCompound) nbt);
    }
    if (nbt instanceof NBTTagList) {
      return convertListTag((NBTTagList) nbt);
    }

    return convertSimpleTag(nbt);
  }

  @SneakyThrows
  public CompoundTag convertCompound(NBTTagCompound nms) {
    CompoundTag to = new CompoundTag();

    for (String nbtKey : nms.getKeys()) {
      final NBTBase tag = nms.get(nbtKey);
      to.put(nbtKey, convertTag(tag));
    }

    return to;
  }

  public ListTag convertListTag(NBTTagList tag) {
    ListTag converted = ListTag.createUnchecked(TagIdToClass.getClassFromId(tag.d_()));
    if (tag.isEmpty()) {
      return converted;
    }

    for (int i = 0; i < tag.size(); i++) {
      converted.add(convertTag(tag.get(i)));
    }

    return converted;
  }

  public Tag<?> convertSimpleTag(NBTBase tag) {
    if (tag instanceof NBTTagString) {
      return new StringTag(tag.asString());
    }

    if (tag instanceof NBTTagInt) {
      return new IntTag(((NBTTagInt) tag).asInt());
    }

    if (tag instanceof NBTTagLong) {
      return new LongTag(((NBTTagLong) tag).asLong());
    }

    if (tag instanceof NBTTagShort) {
      return new ShortTag(((NBTTagShort) tag).asShort());
    }

    if (tag instanceof NBTTagDouble) {
      return new DoubleTag(((NBTTagDouble) tag).asDouble());
    }

    if (tag instanceof NBTTagFloat) {
      return new FloatTag(((NBTTagFloat) tag).asFloat());
    }

    if (tag instanceof NBTTagByteArray) {
      return new ByteArrayTag(((NBTTagByteArray) tag).getBytes());
    }

    if (tag instanceof NBTTagIntArray) {
      return new IntArrayTag(((NBTTagIntArray) tag).getInts());
    }

    if (tag instanceof NBTTagByte) {
      return new ByteTag(((NBTTagByte) tag).asByte());
    }

    throw new IllegalStateException("Unknown NBT tag by " + tag.getClass());
  }

  @Override
  public ItemStack setItemStackTag(ItemStack itemStack, CompoundTag compoundTag) {
    final net.minecraft.server.v1_16_R2.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
    nmsItem.setTag(convertCompoundToNMS(compoundTag));

    return CraftItemStack.asBukkitCopy(nmsItem);
  }

  public NBTBase convertTagToNMS(Tag<?> tag) {
    if (tag instanceof CompoundTag) {
      return convertCompoundToNMS((CompoundTag) tag);
    }

    if (tag instanceof ListTag) {
      return convertListToNMS((ListTag<?>) tag);
    }

    if (tag instanceof StringTag) {
      return NBTTagString.a(((StringTag) tag).getValue());
    }

    if (tag instanceof DoubleTag) {
      return NBTTagDouble.a(((DoubleTag) tag).asDouble());
    }

    if (tag instanceof ShortTag) {
      return NBTTagShort.a(((ShortTag) tag).asShort());
    }

    if (tag instanceof LongTag) {
      return NBTTagLong.a(((LongTag) tag).asLong());
    }

    if (tag instanceof FloatTag) {
      return NBTTagFloat.a(((FloatTag) tag).asFloat());
    }

    if (tag instanceof ByteTag) {
      return NBTTagByte.a(((ByteTag) tag).asByte());
    }

    if (tag instanceof LongArrayTag) {
      throw new IllegalStateException("LongArrayTag is unsupported!");
    }

    if (tag instanceof IntArrayTag) {
      return new NBTTagIntArray(((IntArrayTag) tag).getValue());
    }

    throw new IllegalStateException(format("Unknown NBT Tag for {}", tag.getClass()));
  }

  public NBTTagList convertListToNMS(ListTag<?> tag) {
    final NBTTagList nbtTagList = new NBTTagList();
    for (Tag<?> inside : tag) {
      nbtTagList.add(convertTagToNMS(inside));
    }

    return nbtTagList;
  }

  public NBTTagCompound convertCompoundToNMS(CompoundTag tag) {
    NBTTagCompound compound = new NBTTagCompound();
    for (Map.Entry<String, Tag<?>> stringTagEntry : tag) {
      compound.set(stringTagEntry.getKey(), convertTagToNMS(stringTagEntry.getValue()));
    }

    return compound;
  }
}
