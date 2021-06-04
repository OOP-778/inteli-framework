package com.oop.inteliframework.packetinjector;

import com.oop.inteliframework.commons.util.SimpleReflection;
import io.netty.channel.*;
import org.bukkit.entity.Player;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import static com.oop.inteliframework.commons.util.SimpleReflection.getField;
import static java.util.Collections.emptyList;

public class Injector {

  private final String HANDLER, INJECTOR;
  private final Map<UUID, Registration> registationMap = new ConcurrentHashMap<>();
  private final Map<Type, Collection<BiPredicate<Player, Object>>> packetListeners =
      new IdentityHashMap<>();

  public Injector(String name) {
    HANDLER = "packet_handler";
    INJECTOR = name + "packet_handler";
  }

  private boolean handle(Player player, Object packet) {
    try {
      return packetListeners.getOrDefault(packet.getClass(), emptyList()).stream()
          .allMatch(listener -> listener.test(player, packet));
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    }
    return true;
  }

  public Registration inject(Player player) {
    final ChannelHandler handler =
        new ChannelDuplexHandler() {
          @Override
          public void channelRead(ChannelHandlerContext context, Object packet) throws Exception {
            if (handle(player, packet)) super.channelRead(context, packet);
          }

          @Override
          public void write(ChannelHandlerContext context, Object packet, ChannelPromise promise)
              throws Exception {
            if (handle(player, packet)) super.write(context, packet, promise);
          }
        };

    final Channel channel = PlayerConnectionHelper.getChannel(player);
    channel.pipeline().addBefore(HANDLER, INJECTOR, handler);
    Registration registration =
        () -> {
          try {
            channel.pipeline().remove(handler);
          } catch (Throwable ignored) {
          }
        };
    registationMap.put(player.getUniqueId(), registration);

    return registration;
  }

  public void unregister(Player player) {
    Optional.ofNullable(registationMap.get(player.getUniqueId())).ifPresent(Registration::close);
  }

  public void unregisterAll() {
    for (Registration value : registationMap.values()) {
      value.close();
    }

    registationMap.clear();
  }

  public <T> Registration onFiltered(Class<T> type, BiPredicate<Player, T> listener) {
    final Collection listeners = packetListeners.computeIfAbsent(type, $ -> new ArrayList<>());
    listeners.add(listener);

    return () -> listeners.remove(listener);
  }

  public <T> Registration on(Class<T> type, BiConsumer<Player, T> listener) {
    return onFiltered(
        type,
        (player, packet) -> {
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

  public static class PlayerConnectionHelper {

    private static final Class<?> CRAFT_PLAYER_CLASS;
    private static final Class<?> ENTITY_PLAYER_CLASS;
    private static final Class<?> PLAYER_CONNECTION_CLASS;
    private static final Class<?> PACKET_CLASS;
    private static final Class<?> NETWORK_MANAGER_CLASS;
    private static final Method SEND_PACKET_METHOD;
    private static final Method GET_HANDLE_METHOD;
    private static final Field PLAYER_CONNECTION_FIELD;
    private static final Field NETWORK_MANAGER_FIELD;
    private static final Field CHANNEL_FIELD;

    static {
      try {

        CRAFT_PLAYER_CLASS = SimpleReflection.findClass("{cb}.entity.CraftPlayer");
        ENTITY_PLAYER_CLASS = SimpleReflection.findClass("{nms}.EntityPlayer");
        PLAYER_CONNECTION_CLASS = SimpleReflection.findClass("{nms}.PlayerConnection");
        PACKET_CLASS = SimpleReflection.findClass("{nms}.Packet");
        NETWORK_MANAGER_CLASS = SimpleReflection.findClass("{nms}.NetworkManager");
        PLAYER_CONNECTION_FIELD = getField(ENTITY_PLAYER_CLASS, "playerConnection");
        NETWORK_MANAGER_FIELD = getField(PLAYER_CONNECTION_CLASS, "networkManager");

        SEND_PACKET_METHOD =
            SimpleReflection.getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", PACKET_CLASS);
        GET_HANDLE_METHOD = SimpleReflection.getMethod(CRAFT_PLAYER_CLASS, "getHandle");
        CHANNEL_FIELD = SimpleReflection.getField(NETWORK_MANAGER_CLASS, Channel.class);

      } catch (Exception ex) {
        throw new IllegalStateException(ex);
      }
    }

    public static Channel getChannel(org.bukkit.entity.Player player) {
      try {

        Object entityPlayer = GET_HANDLE_METHOD.invoke(player);
        Object connection = PLAYER_CONNECTION_FIELD.get(entityPlayer);
        Object networkManager = NETWORK_MANAGER_FIELD.get(connection);
        return (Channel) CHANNEL_FIELD.get(networkManager);

      } catch (Exception e) {
        e.printStackTrace();
      }

      return null;
    }

    public static void sendPacket(org.bukkit.entity.Player player, Object packet) {
      try {

        Object entityPlayer = GET_HANDLE_METHOD.invoke(player);
        Object connection = PLAYER_CONNECTION_FIELD.get(entityPlayer);

        SEND_PACKET_METHOD.invoke(connection, packet);

      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
