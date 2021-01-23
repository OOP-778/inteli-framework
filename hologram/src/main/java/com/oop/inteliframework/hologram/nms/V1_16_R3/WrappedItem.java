package com.oop.inteliframework.hologram.nms.V1_16_R3;

import com.google.common.collect.Maps;
import net.minecraft.server.v1_16_R3.EntityItem;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

import static com.oop.inteliframework.hologram.nms.V1_16_R3.Helper.constructEntityMetaPacket;

public class WrappedItem {
    protected EntityItem entityItem;
    private Map<UUID, DataWatcherHelper> watcherMap = Maps.newConcurrentMap();

    public WrappedItem(Location location) {
        entityItem = new EntityItem(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());
        entityItem.setItemStack(CraftItemStack.asNMSCopy(new ItemStack(Material.STONE)));
    }

    public Packet constructSpawnPacket() {
        return new PacketPlayOutSpawnEntity(entityItem, 2);
    }

    public DataWatcherHelper getDataWatcher(Player player) {
        return watcherMap.computeIfAbsent(player.getUniqueId(), key -> new DataWatcherHelper(entityItem));
    }

    public Packet constructUpdatePacket(Player player) {
        return constructEntityMetaPacket(getDataWatcher(player).getWatchableObjects(), entityItem.getId());
    }

    public Packet constructRemovePacket() {
        return new PacketPlayOutEntityDestroy(entityItem.getId());
    }

    public net.minecraft.server.v1_16_R3.ItemStack getItem(Player player) {
        return watcherMap.get(player.getUniqueId()).getItemStack();
    }

    public void onRemove(Player player) {
        watcherMap.remove(player.getUniqueId());
    }

    public void setItem(ItemStack item, Player player) {
        getDataWatcher(player).setItemStack(
                CraftItemStack.asNMSCopy(item)
        );
    }
}
