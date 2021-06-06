package com.oop.inteliframework.bukkit.nbt.nms.V1_8_R3;

import com.oop.inteliframework.bukkit.nbt.nms.NBTTags;
import com.oop.inteliframework.bukkit.nbt.nms.TagIdToClass;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.*;
import net.querz.nbt.tag.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class ImplNBTTags implements NBTTags {
  @Override
  public CompoundTag fromItemStack(ItemStack itemStack) {
    final net.minecraft.server.v1_8_R3.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
    if (nmsCopy.getTag() == null) return new CompoundTag();

    System.out.println(nmsCopy.getTag());
    return convertCompound(nmsCopy.getTag());
  }

  public Tag<?> convertTag(NBTBase nbt) {
    if (nbt instanceof NBTTagCompound) return convertCompound((NBTTagCompound) nbt);
    if (nbt instanceof NBTTagList) return convertListTag((NBTTagList) nbt);

    return convertSimpleTag(nbt);
  }

  @SneakyThrows
  public CompoundTag convertCompound(NBTTagCompound nms) {
    CompoundTag to = new CompoundTag();

    for (String nbtKey : nms.c()) {
      final NBTBase tag = nms.get(nbtKey);
      to.put(nbtKey, convertTag(tag));
    }

    return to;
  }

  public ListTag convertListTag(NBTTagList tag) {
    ListTag converted = ListTag.createUnchecked(TagIdToClass.getClassFromId(tag.f()));
    if (tag.isEmpty()) return converted;

    for (int i = 0; i < tag.size(); i++) {
      converted.add(convertTag(tag.g(i)));
    }

    return converted;
  }

  public Tag<?> convertSimpleTag(NBTBase tag) {
    if (tag instanceof NBTTagString) {
      return new StringTag(((NBTTagString) tag).a_());
    }

    if (tag instanceof NBTTagInt) {
      return new IntTag(((NBTTagInt) tag).d());
    }

    if (tag instanceof NBTTagLong) {
      return new LongTag(((NBTTagLong) tag).c());
    }

    if (tag instanceof NBTTagShort) {
      return new ShortTag(((NBTTagShort) tag).e());
    }

    if (tag instanceof NBTTagDouble) {
      return new DoubleTag(((NBTTagDouble) tag).g());
    }

    if (tag instanceof NBTTagFloat) {
      return new FloatTag(((NBTTagFloat) tag).h());
    }

    if (tag instanceof NBTTagByteArray) {
      return new ByteArrayTag(((NBTTagByteArray) tag).c());
    }

    if (tag instanceof NBTTagIntArray) {
      return new IntArrayTag(((NBTTagIntArray) tag).c());
    }

    if (tag instanceof NBTTagByte) return new ByteTag(((NBTTagByte) tag).f());

    throw new IllegalStateException("Unknown NBT tag by " + tag.getClass());
  }

  @Override
  public ItemStack setItemStackTag(ItemStack itemStack, CompoundTag compoundTag) {
    final net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
    nmsItem.setTag(convertCompoundToNMS(compoundTag));

    return CraftItemStack.asBukkitCopy(nmsItem);
  }

  public NBTBase convertTagToNMS(Tag<?> tag) {
    if (tag instanceof CompoundTag) return convertCompoundToNMS((CompoundTag) tag);

    if (tag instanceof ListTag) {
      return convertListToNMS((ListTag<?>) tag);
    }

    if (tag instanceof StringTag) {
      return new NBTTagString(((StringTag) tag).getValue());
    }

    if (tag instanceof DoubleTag) {
      return new NBTTagDouble(((DoubleTag) tag).asDouble());
    }

    if (tag instanceof ShortTag) {
      return new NBTTagShort(((ShortTag) tag).asShort());
    }

    if (tag instanceof LongTag) {
      return new NBTTagLong(((LongTag) tag).asLong());
    }

    if (tag instanceof FloatTag) {
      return new NBTTagFloat(((FloatTag) tag).asFloat());
    }

    if (tag instanceof ByteTag) {
      return new NBTTagByte(((ByteTag) tag).asByte());
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
