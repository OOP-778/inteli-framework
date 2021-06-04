/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R3.packets;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import com.mojang.authlib.GameProfile;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;

/** @author Jitse Boonstra */
public class PacketPlayOutPlayerInfoWrapper {

  private final Class<?> packetPlayOutPlayerInfoClazz =
      SimpleReflection.findClass("{nms}.PacketPlayOutPlayerInfo");
  private final Class<?> playerInfoDataClazz =
      SimpleReflection.findClass("{nms}.PacketPlayOutPlayerInfo$PlayerInfoData");
  private final Constructor playerInfoDataConstructor =
      SimpleReflection.getConstructor(
          playerInfoDataClazz,
          packetPlayOutPlayerInfoClazz,
          GameProfile.class,
          int.class,
          EnumGamemode.class,
          IChatBaseComponent.class);

  @SneakyThrows
  public PacketPlayOutPlayerInfo create(
      PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfile gameProfile, String name) {
    PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
    SimpleReflection.getField(packetPlayOutPlayerInfo.getClass(), "a")
        .set(packetPlayOutPlayerInfo, action);

    Object playerInfoData =
        playerInfoDataConstructor.newInstance(
            packetPlayOutPlayerInfo,
            gameProfile,
            1,
            EnumGamemode.NOT_SET,
            IChatBaseComponent.ChatSerializer.b(
                "{\"text\":\"[NPC] " + name + "\",\"color\":\"dark_gray\"}"));

    Field fieldAccessor = SimpleReflection.getField(packetPlayOutPlayerInfo.getClass(), "b");
    fieldAccessor.set(packetPlayOutPlayerInfo, Collections.singletonList(playerInfoData));

    return packetPlayOutPlayerInfo;
  }
}
