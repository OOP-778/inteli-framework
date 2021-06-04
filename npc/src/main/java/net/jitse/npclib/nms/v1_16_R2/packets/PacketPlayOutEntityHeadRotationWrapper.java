/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R2.packets;

import org.bukkit.Location;

import net.minecraft.server.v1_16_R2.PacketPlayOutEntityHeadRotation;

/** @author Jitse Boonstra */
public class PacketPlayOutEntityHeadRotationWrapper {

  public PacketPlayOutEntityHeadRotation create(Location location, int entityId) {
    PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation =
        new PacketPlayOutEntityHeadRotation();

    SimpleReflection.getField(packetPlayOutEntityHeadRotation.getClass(), "a", int.class)
        .set(packetPlayOutEntityHeadRotation, entityId);
    SimpleReflection.getField(packetPlayOutEntityHeadRotation.getClass(), "b", byte.class)
        .set(packetPlayOutEntityHeadRotation, (byte) ((int) location.getYaw() * 256.0F / 360.0F));

    return packetPlayOutEntityHeadRotation;
  }
}
