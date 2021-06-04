/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_13_R2.packets;

import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import org.bukkit.Location;

import java.util.UUID;

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
    dataWatcher.register(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 127);

    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "h", DataWatcher.class)
        .set(packetPlayOutNamedEntitySpawn, dataWatcher);

    return packetPlayOutNamedEntitySpawn;
  }
}
