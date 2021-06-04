/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R2.packets;

import java.util.UUID;

import org.bukkit.Location;

import net.minecraft.server.v1_16_R2.DataWatcher;
import net.minecraft.server.v1_16_R2.DataWatcherObject;
import net.minecraft.server.v1_16_R2.DataWatcherRegistry;
import net.minecraft.server.v1_16_R2.PacketPlayOutNamedEntitySpawn;

/** @author Jitse Boonstra */
public class PacketPlayOutNamedEntitySpawnWrapper {

  public PacketPlayOutNamedEntitySpawn create(UUID uuid, Location location, int entityId) {
    PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn =
        new PacketPlayOutNamedEntitySpawn();

    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "a", int.class)
        .set(packetPlayOutNamedEntitySpawn, entityId);
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "b", UUID.class)
        .set(packetPlayOutNamedEntitySpawn, uuid);
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "c", double.class)
        .set(packetPlayOutNamedEntitySpawn, location.getX());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "d", double.class)
        .set(packetPlayOutNamedEntitySpawn, location.getY());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "e", double.class)
        .set(packetPlayOutNamedEntitySpawn, location.getZ());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "f", byte.class)
        .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "g", byte.class)
        .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));

    DataWatcher dataWatcher = new DataWatcher(null);
    dataWatcher.register(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

    return packetPlayOutNamedEntitySpawn;
  }
}
