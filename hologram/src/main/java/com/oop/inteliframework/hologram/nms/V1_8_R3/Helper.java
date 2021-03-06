package com.oop.inteliframework.hologram.nms.V1_8_R3;

import com.oop.inteliframework.commons.util.SimpleReflection;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Helper {
    public static void sendPacket(Player player, Packet... packets) {
        if (!player.isOnline()) return;

        for (Packet packet : packets) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static void sendPacket(Collection<Player> players, Collection<Packet> packets) {
        for (Player player : players) {
            if (!player.isOnline()) return;

            for (Packet packet : packets) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    public static Packet constructEntityMetaPacket(List<DataWatcher.WatchableObject> objectList, int id) {
        try {
            Packet packet = new PacketPlayOutEntityMetadata();
            SimpleReflection
                    .getField(packet.getClass(), List.class)
                    .set(packet, objectList);

            SimpleReflection
                    .getField(packet.getClass(), int.class)
                    .set(packet, id);
            
            return packet;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
