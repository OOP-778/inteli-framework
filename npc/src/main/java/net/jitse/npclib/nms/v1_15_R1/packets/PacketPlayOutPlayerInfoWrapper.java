/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_15_R1.packets;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.EnumGamemode;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;

import java.util.Collections;
import java.util.List;

/** @author Jitse Boonstra */
public class PacketPlayOutPlayerInfoWrapper {

  private final Class<?> packetPlayOutPlayerInfoClazz =
      SimpleReflection.getMinecraftClass("PacketPlayOutPlayerInfo");
  private final Class<?> playerInfoDataClazz =
      SimpleReflection.getMinecraftClass("PacketPlayOutPlayerInfo$PlayerInfoData");
  private final SimpleReflection.ConstructorInvoker playerInfoDataConstructor =
      SimpleReflection.getConstructor(
          playerInfoDataClazz,
          packetPlayOutPlayerInfoClazz,
          GameProfile.class,
          int.class,
          EnumGamemode.class,
          IChatBaseComponent.class);

  public PacketPlayOutPlayerInfo create(
      PacketPlayOutPlayerInfo.EnumPlayerInfoAction action, GameProfile gameProfile, String name) {
    PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo();
    SimpleReflection.getField(
            packetPlayOutPlayerInfo.getClass(),
            "a",
            PacketPlayOutPlayerInfo.EnumPlayerInfoAction.class)
        .set(packetPlayOutPlayerInfo, action);

    Object playerInfoData =
        playerInfoDataConstructor.invoke(
            packetPlayOutPlayerInfo,
            gameProfile,
            1,
            EnumGamemode.NOT_SET,
            IChatBaseComponent.ChatSerializer.b(
                "{\"text\":\"[NPC] " + name + "\",\"color\":\"dark_gray\"}"));

    SimpleReflection.FieldAccessor<List> fieldAccessor =
        SimpleReflection.getField(packetPlayOutPlayerInfo.getClass(), "b", List.class);
    fieldAccessor.set(packetPlayOutPlayerInfo, Collections.singletonList(playerInfoData));

    return packetPlayOutPlayerInfo;
  }
}
