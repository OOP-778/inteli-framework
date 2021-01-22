package com.oop.inteliframework.testplugin;

import com.oop.inteliframework.testplugin.util.Injector;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Field;
import java.util.List;

public class PacketCatcher extends Injector implements Listener {
    public PacketCatcher() {
        on(PacketPlayOutEntityMetadata.class, (player, packet) -> {
            printOutPacket(packet, player);
        });
    }

    @SneakyThrows
    private static void printOutPacket(Packet packet, Player player) {
        System.out.println("=== Packet (" + packet.getClass().getSimpleName() + ") Player (" + player.getName() + ")");
        System.out.println("");
        System.out.println("====== Name ==== Value");
        for (Field declaredField : packet.getClass().getDeclaredFields()) {
            declaredField.setAccessible(true);

            Object object = declaredField.get(packet);

            if (object instanceof List) {
                List<DataWatcher.WatchableObject> objects = (List<DataWatcher.WatchableObject>) object;
                for (DataWatcher.WatchableObject watchableObject : objects) {
                    System.out.println("=== WATCHABLE OBJECT ===");
                    System.out.println("b: " + watchableObject.b());
                    System.out.println("c: " + watchableObject.c());
                    System.out.println("a: " + watchableObject.a());
                }

            } else
                System.out.println("       " + declaredField.getName() + "      " + object.toString());
        }
        System.out.println("");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        inject(event.getPlayer());
    }
}
