package com.oop.inteliframework.hologram.nms.V1_16_R2;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.SimpleReflection;
import com.oop.inteliframework.hologram.HologramLine;
import com.oop.inteliframework.hologram.nms.WrappedArmorStand;
import com.oop.inteliframework.hologram.util.UpdateableObject;
import lombok.SneakyThrows;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.oop.inteliframework.hologram.nms.V1_16_R2.Helper.sendPacket;

public class ImplWrappedArmorStand implements WrappedArmorStand {
    private EntityArmorStand entity;
    private HologramLine<?, ?> line;
    private Location lastLocation;

    private UpdateableObject<Location> location;
    private WrappedItem attachedItem;

    private Map<UUID, DataWatcherHelper> watcherMap = Maps.newConcurrentMap();

    public ImplWrappedArmorStand(Location location, HologramLine<?, ?> line) {
        entity = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        entity.setInvisible(true);
        this.line = line;
        this.location = new UpdateableObject<>(location);
        lastLocation = location;
    }

    private Collection<Player> convertArray(Player... players) {
        if (players.length == 0)
            return line.getHologramView().getViewers();

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
            packetList.add(new PacketPlayOutMount(entity));
        }
        packetList.add(Helper.constructEntityMetaPacket(entity.getDataWatcher().c(), entity.getId()));

        sendPacket(player, packetList.toArray(new Packet[0]));
    }

    @Override
    public void remove(Player... players) {
        List<Packet> packetList = new ArrayList<>();
        packetList.add(new PacketPlayOutEntityDestroy(entity.getId()));
        if (attachedItem != null)
            packetList.add(attachedItem.constructRemovePacket());

        for (Player player : players) {
            if (attachedItem != null)
                attachedItem.onRemove(player);

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
        entity.setCustomName(CraftChatMessage.fromString(customName)[0]);
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
            packets.add(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(
                    entity.getId(),
                    (byte) diffX,
                    (byte) diffY,
                    (byte) diffZ,
                    false
            ));
        else {
            entity.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            packets.add(new PacketPlayOutEntityTeleport(entity));

            if (attachedItem != null) {
                attachedItem.entityItem.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
                packets.add(new PacketPlayOutEntityTeleport(attachedItem.entityItem));
            }
        }

        for (Player viewer : line.getHologramView().getViewers()) {
            for (Packet packet : packets) {
                sendPacket(viewer, packet);
            }
        }

        lastLocation = location;
    }

    private double[] convert(Location location) {
        return new double[]{
                MathHelper.floor(location.getX() * 32),
                MathHelper.floor(location.getY() * 32),
                MathHelper.floor(location.getZ() * 32)
        };
    }

    @SneakyThrows
    public void setupItem() {
        attachedItem = new WrappedItem(location.current());
        entity.passengers.add(attachedItem.entityItem);

        SimpleReflection
                .getField(Entity.class, "vehicle")
                .set(attachedItem.entityItem, entity);
    }

    @Override
    public void setGravity(boolean gravity) {
        entity.setNoGravity(gravity);
    }

    @Override
    public void setMarker(boolean marker) {
    }

    @Override
    public void update(Player... players) {
        List<Packet> packetList = new ArrayList<>();
        for (Player player : players) {
            packetList.add(Helper.constructEntityMetaPacket(getDataWatcher(player).getWatchableObjects(), entity.getId()));
            if (attachedItem != null)
                packetList.add(attachedItem.constructUpdatePacket(player));

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
        if (itemStack == null)
            return;

        if (attachedItem == null)
            setupItem();

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
