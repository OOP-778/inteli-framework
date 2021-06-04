package com.oop.inteliframework.bukkit.nbt.nms;

import lombok.RequiredArgsConstructor;
import net.querz.nbt.tag.*;

@RequiredArgsConstructor
public enum TagIdToClass {
  END(EndTag.class),
  BYTE(ByteTag.class),
  SHORT(ShortTag.class),
  INT(IntTag.class),
  LONG(LongTag.class),
  FLOAT(FloatTag.class),
  DOUBLE(DoubleTag.class),
  BYTE_ARRAY(ByteArrayTag.class),
  STRING(StringTag.class),
  LIST(ListTag.class),
  COMPOUND(CompoundTag.class),
  INT_ARRAY(IntArrayTag.class);

  private final Class<?> clazz;

  public static Class<?> getClassFromId(int id) {
    return values()[id].clazz;
  }
}
