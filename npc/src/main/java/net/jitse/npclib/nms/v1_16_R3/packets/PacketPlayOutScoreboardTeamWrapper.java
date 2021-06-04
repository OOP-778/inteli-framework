/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R3.packets;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;

/** @author Jitse Boonstra */
public class PacketPlayOutScoreboardTeamWrapper {

  @SneakyThrows
  public PacketPlayOutScoreboardTeam createRegisterTeam(String name) {
    PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "i")
        .set(packetPlayOutScoreboardTeam, 0);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "a")
        .set(packetPlayOutScoreboardTeam, name);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "b")
        .set(packetPlayOutScoreboardTeam, new ChatComponentText(name));
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "e")
        .set(packetPlayOutScoreboardTeam, "never");
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "f")
        .set(packetPlayOutScoreboardTeam, "never");
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "j")
        .set(packetPlayOutScoreboardTeam, 0);

    Field collectionFieldAccessor =
        SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "h");
    collectionFieldAccessor.set(packetPlayOutScoreboardTeam, Collections.singletonList(name));

    return packetPlayOutScoreboardTeam;
  }

  @SneakyThrows
  public PacketPlayOutScoreboardTeam createUnregisterTeam(String name) {
    PacketPlayOutScoreboardTeam packetPlayOutScoreboardTeam = new PacketPlayOutScoreboardTeam();

    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "i")
        .set(packetPlayOutScoreboardTeam, 1);
    SimpleReflection.getField(packetPlayOutScoreboardTeam.getClass(), "a")
        .set(packetPlayOutScoreboardTeam, name);

    return packetPlayOutScoreboardTeam;
  }
}
