package com.oop.inteliframework.testplugin.util;

import io.netty.channel.*;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import static java.util.Collections.emptyList;

public class Injector {
    private static final String HANDLER = "packet_handler", INJECTOR = "custom_packet_handler";
    private final Map<Type, Collection<BiPredicate<Player, Packet>>> packetListeners = new IdentityHashMap<>();

    private boolean handle(Player player, Packet packet) {
        return packetListeners.getOrDefault(packet.getClass(), emptyList()).stream().allMatch(listener ->
                listener.test(player, packet)
        );
    }

    public Registration inject(Player player) {
        final ChannelHandler handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
                if (handle(player, (Packet) packet)) super.channelRead(context, packet);
            }

            @Override
            public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise) throws Exception {
                if (handle(player, (Packet) packet)) super.write(context, packet, promise);
            }
        };

        final Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.pipeline().addBefore(HANDLER, INJECTOR, handler);
        return () -> channel.pipeline().remove(handler);
    }

    public void send(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public <Type extends Packet> Registration on(Class<Type> type, BiPredicate<Player, Type> listener) {
        final Collection listeners = packetListeners.computeIfAbsent(type, $ -> new ArrayList<>());
        listeners.add(listener);

        return () -> listeners.remove(listener);
    }

    public <Type extends Packet> Registration on(Class<Type> type, BiConsumer<Player, Type> listener) {
        return on(type, (player, packet) -> {
            listener.accept(player, packet);
            return true;
        });
    }

    public interface Registration extends Closeable {
        @Override
        void close();

        default void unregister() {
            close();
        }
    }
}