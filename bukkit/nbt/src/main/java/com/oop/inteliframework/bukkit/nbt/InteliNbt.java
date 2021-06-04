package com.oop.inteliframework.bukkit.nbt;

import com.oop.inteliframework.adapters.VersionedAdapters;
import com.oop.inteliframework.bukkit.nbt.nms.NBTTags;
import com.oop.inteliframework.commons.util.InteliVersion;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class InteliNbt {

  private static final NBTTags tags;

  static {
    try {
      tags =
          VersionedAdapters.loadClass(
                  NBTTags.class.getPackage().getName(), "ImplNBTTags", NBTTags.class)
              .get()
              .newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(
          format(
              "Unsupported version of {}. NBTTags not found!", InteliVersion.getStringVersion()));
    }
  }

  public static NBTTags getTags() {
    return tags;
  }
}
