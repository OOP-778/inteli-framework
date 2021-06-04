/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R2.packets;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.server.v1_16_R2.ChatComponentText;
import net.minecraft.server.v1_16_R2.IChatBaseComponent;
import net.minecraft.server.v1_16_R2.PacketPlayOutScoreboardTeam;

/** @author Jitse Boonstra */
public class PacketPlayOutScoreboardTeamWrapper {

  public PacketPlayOutScoreboardTeam createRegisterTeam(String name) {
    PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "i", int.class)
        .set(packetPlayOutScoreboardTeam, 0);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "a", String.class)
        .set(packetPlayOutScoreboardTeam, name);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "b", IChatBaseComponent.class)
        .set(packetPlayOutScoreboardTeam, new ChatComponentText(name));
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "e", String.class)
        .set(packetPlayOutScoreboardTeam, "never");
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "f", String.class)
        .set(packetPlayOutScoreboardTeam, "never");
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "j", int.class)
        .set(packetPlayOutScoreboardTeam, 0);
    SimpleReflection.FieldAccessor<Collection> collectionFieldAccessor =
        SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "h", Collection.class);
    collectionFieldAccessor.set(packetPlayOutScoreboardTeam, Collections.singletonList(name));

    return packetPlayOutScoreboardTeam;
  }

  public PacketPlayOutScoreboardTeam createUnregisterTeam(String name) {
    PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "i", int.class)
        .set(packetPlayOutScoreboardTeam, 1);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "a", String.class)
        .set(packetPlayOutScoreboardTeam, name);

    return packetPlayOutScoreboardTeam;
  }
}
