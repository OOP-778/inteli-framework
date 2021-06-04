/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R3.packets;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import org.bukkit.Location;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityHeadRotation;

/** @author Jitse Boonstra */
public class PacketPlayOutEntityHeadRotationWrapper {

  @SneakyThrows
  public PacketPlayOutEntityHeadRotation create(Location location, int entityId) {
    PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation =
        new PacketPlayOutEntityHeadRotation();

    SimpleReflection.getField(packetPlayOutEntityHeadRotation.getClass(), "a")
        .set(packetPlayOutEntityHeadRotation, entityId);
    SimpleReflection.getField(packetPlayOutEntityHeadRotation.getClass(), "b")
        .set(packetPlayOutEntityHeadRotation, (byte) ((int) location.getYaw() * 256.0F / 360.0F));

    return packetPlayOutEntityHeadRotation;
  }
}
