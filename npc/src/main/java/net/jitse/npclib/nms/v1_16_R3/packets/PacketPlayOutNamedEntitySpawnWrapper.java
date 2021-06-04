/*
 * Copyright (c) 2018 Jitse Boonstra
 */

package net.jitse.npclib.nms.v1_16_R3.packets;

import java.util.UUID;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import org.bukkit.Location;

import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.DataWatcherObject;
import net.minecraft.server.v1_16_R3.DataWatcherRegistry;
import net.minecraft.server.v1_16_R3.PacketPlayOutNamedEntitySpawn;

/** @author Jitse Boonstra */
public class PacketPlayOutNamedEntitySpawnWrapper {

  @SneakyThrows
  public PacketPlayOutNamedEntitySpawn create(UUID uuid, Location location, int entityId) {
    PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn =
        new PacketPlayOutNamedEntitySpawn();

    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "a")
        .set(packetPlayOutNamedEntitySpawn, entityId);
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "b")
        .set(packetPlayOutNamedEntitySpawn, uuid);
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "c")
        .set(packetPlayOutNamedEntitySpawn, location.getX());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "d")
        .set(packetPlayOutNamedEntitySpawn, location.getY());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "e")
        .set(packetPlayOutNamedEntitySpawn, location.getZ());
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "f")
        .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
    SimpleReflection.getField(packetPlayOutNamedEntitySpawn.getClass(), "g")
        .set(packetPlayOutNamedEntitySpawn, (byte) ((int) (location.getPitch() * 256.0F / 360.0F)));

    DataWatcher dataWatcher = new DataWatcher(null);
    dataWatcher.register(new DataWatcherObject<>(16, DataWatcherRegistry.a), (byte) 127);

    return packetPlayOutNamedEntitySpawn;
  }
}
