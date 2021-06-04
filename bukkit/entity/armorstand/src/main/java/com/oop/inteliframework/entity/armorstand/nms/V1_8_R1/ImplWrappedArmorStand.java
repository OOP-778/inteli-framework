package com.oop.inteliframework.entity.armorstand.nms.V1_8_R1;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oop.inteliframework.entity.armorstand.nms.WrappedArmorStand;
import com.oop.inteliframework.entity.commons.UpdateableObject;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Supplier;

import static com.oop.inteliframework.entity.armorstand.nms.V1_8_R1.Helper.sendPacket;

public class ImplWrappedArmorStand implements WrappedArmorStand {
  private EntityArmorStand entity;
  private Location lastLocation;
  private UpdateableObject<Location> location;
  private WrappedItem attachedItem;
  private Map<UUID, DataWatcherHelper> watcherMap = Maps.newConcurrentMap();

  private Supplier<Collection<Player>> viewersSupplier;

  public ImplWrappedArmorStand(Location location, Supplier<Collection<Player>> players) {
    this.viewersSupplier = players;
    entity =
        new EntityArmorStand(
            ((CraftWorld) location.getWorld()).getHandle(),
            location.getX(),
            location.getY(),
            location.getZ());
    this.location = new UpdateableObject<>(location);
    lastLocation = location;
  }

  private Collection<Player> convertArray(Player... players) {
    if (players.length == 0) return viewersSupplier.get();

    return Sets.newHashSet(players);
  }

  public DataWatcherHelper getDataWatcher(Player player) {
    return watcherMap.computeIfAbsent(player.getUniqueId(), key -> new DataWatcherHelper(entity));
  }

  @Override
  public void spawn(Player player) {
    List<Packet> packetList = new LinkedList<>();

    packetList.add(new PacketPlayOutSpawnEntityLiving(entity));
    if (attachedItem != null) {
      packetList.add(attachedItem.constructSpawnPacket());
      packetList.add(attachedItem.constructUpdatePacket(player));
      packetList.add(new PacketPlayOutAttachEntity(0, attachedItem.entityItem, entity));
    }

    sendPacket(player, packetList.toArray(new Packet[0]));
  }

  @Override
  public void remove(Player... players) {
    final List<Packet> packetList = new ArrayList<>();
    packetList.add(new PacketPlayOutEntityDestroy(entity.getId()));
    if (attachedItem != null) packetList.add(attachedItem.constructRemovePacket());

    for (Player player : players) {
      if (attachedItem != null) attachedItem.onRemove(player);

      watcherMap.remove(player.getUniqueId());
    }

    sendPacket(convertArray(players), packetList);
  }

  @Override
  public void setLocation(Location location) {
    this.location.set(location);
  }

  @Override
  public void setCustomName(String customName) {
    entity.setCustomName(customName);
  }

  @Override
  public void setCustomNameVisibility(boolean visibility) {
    entity.setCustomNameVisible(visibility);
  }

  @Override
  public void outputLocation() {
    if (!location.isUpdated()) return;

    List<Packet> packets = new ArrayList<>();
    Location location = this.location.get();

    double[] current = convert(location);
    double[] last = convert(lastLocation);

    double diffX = current[0] - last[0];
    double diffY = current[1] - last[1];
    double diffZ = current[2] - last[2];

    boolean move = !(Math.abs(diffX) >= 4 || Math.abs(diffY) >= 4 || Math.abs(diffZ) >= 4);

    if (move)
      packets.add(
          new PacketPlayOutRelEntityMove(
              entity.getId(), (byte) diffX, (byte) diffY, (byte) diffZ, false));
    else {
      entity.setLocation(
          location.getX(),
          location.getY(),
          location.getZ(),
          location.getYaw(),
          location.getPitch());
      packets.add(new PacketPlayOutEntityTeleport(entity));
    }

    for (Player viewer : viewersSupplier.get()) {
      for (Packet packet : packets) {
        sendPacket(viewer, packet);
      }
    }

    lastLocation = location;
  }

  private double[] convert(Location location) {
    return new double[] {
      MathHelper.floor(location.getX() * 32),
      MathHelper.floor(location.getY() * 32),
      MathHelper.floor(location.getZ() * 32)
    };
  }

  public void setupItem() {
    attachedItem = new WrappedItem(location.current());
    attachedItem.entityItem.vehicle = entity;
    entity.passenger = attachedItem.entityItem;
  }

  @Override
  public void setGravity(boolean gravity) {
    entity.setGravity(gravity);
  }

  @Override
  public void setMarker(boolean marker) {}

  @Override
  public void update(Player... players) {
    List<Packet> packetList = new ArrayList<>();
    for (Player player : players) {
      packetList.add(
          Helper.constructEntityMetaPacket(
              getDataWatcher(player).getWatchableObjects(), entity.getId()));
      if (attachedItem != null) packetList.add(attachedItem.constructUpdatePacket(player));

      sendPacket(player, packetList.toArray(new Packet[0]));
      packetList.clear();
    }
  }

  @Override
  public void setSmall(boolean small) {
    entity.setSmall(small);
  }

  @Override
  public void setVisible(boolean visible) {
    entity.setInvisible(!visible);
  }

  @Override
  public void outputItem(Player player, ItemStack itemStack) {
    if (itemStack == null) return;

    if (attachedItem == null) setupItem();

    attachedItem.setItem(itemStack, player);
    sendPacket(player, attachedItem.constructUpdatePacket(player));
  }

  @Override
  public void outputName(Player player, String text) {
    getDataWatcher(player).changeDisplayName(text);
    update(player);
  }

  @Override
  public ItemStack getItem(Player player) {
    if (attachedItem == null) return null;
    return CraftItemStack.asBukkitCopy(attachedItem.getItem(player));
  }
}
