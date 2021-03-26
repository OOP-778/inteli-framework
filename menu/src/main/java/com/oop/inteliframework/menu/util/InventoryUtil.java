package com.oop.inteliframework.menu.util;

import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

import static com.oop.inteliframework.commons.util.SimpleReflection.findClass;

public class InventoryUtil {

  @SneakyThrows
  public static void updateTitle(Inventory inventory, Player who, String title) {
    Class<?> chatMessageClass = findClass("ChatComponentText");
    Class<?> craftInventoryClass = findClass("{cb}.inventory.CraftInventory");

    if (MCVersion.isBefore(9) && title.length() > 16) {
      title = title.substring(0, 15);
    }

    Object activeContainer = getActiveContainer(who);
    Object windowId =
        SimpleReflection.getField(activeContainer.getClass(), "windowId").get(activeContainer);

    Object chatMessage =
        SimpleReflection.getConstructor(chatMessageClass, String.class)
            .newInstance(ChatColor.translateAlternateColorCodes('&', title));

    Object packet;
    if (MCVersion.isAfter(13)) {
      Class<?> containerClass = findClass("{nms}.Containers");
      Constructor packetConst =
          SimpleReflection.getConstructor(
              findClass("{nms}.PacketPlayOutOpenWindow"),
              int.class,
              containerClass,
              findClass("{nms}.IChatBaseComponent"));

      Object containers =
          SimpleReflection.getField(activeContainer.getClass(), "e").get(activeContainer);
      packet = packetConst.newInstance(windowId, containers, chatMessage);

    } else {
      packet =
          SimpleReflection.getConstructor(
                  findClass("{nms}.PacketPlayOutOpenWindow"),
                  int.class,
                  String.class,
                  findClass("{nms}.IChatBaseComponent"))
              .newInstance(
                  windowId,
                  getMinecraftName(
                      SimpleReflection.getMethod(craftInventoryClass, "getInventory")
                          .invoke(inventory)),
                  chatMessage);
    }

    SimpleReflection.Player.sendPacket(who, packet);
  }

  @SneakyThrows
  private static String getMinecraftName(Object inventory) {
    Class<?> tileContainerClass = findClass("{nms}.ITileEntityContainer");
    if (tileContainerClass.isAssignableFrom(inventory.getClass())) {
      return (String)
          SimpleReflection.getMethod(tileContainerClass, "getContainerName").invoke(inventory);
    } else {
      return "minecraft:container";
    }
  }

  @SneakyThrows
  public static Object getActiveContainer(Player player) {
    Class<?> craftPlayerClass = findClass("{cb}.entity.CraftPlayer");
    Class<?> entityPlayerClass = findClass("{nms}.EntityPlayer");

    Object entityPlayer =
        SimpleReflection.executeMethod(
            SimpleReflection.getMethod(craftPlayerClass, "getHandle"), player);
    return SimpleReflection.getField(entityPlayerClass, "activeContainer").get(entityPlayer);
  }

  @SneakyThrows
  public static void updateItem(Player who, int slot, ItemStack itemStack) {
    Class<?> packetSetSlotClass = findClass("{nms}.PacketPlayOutSetSlot");
    Class<?> nmsItemStackClass = findClass("{nms}.ItemStack");
    Class<?> craftItemStackClass = findClass("{cb}.inventory.CraftItemStack");

    Object activeContainer = getActiveContainer(who);
    Object windowId =
        SimpleReflection.getField(activeContainer.getClass(), "windowId").get(activeContainer);

    Constructor<?> constructor =
        SimpleReflection.getConstructor(
            packetSetSlotClass, int.class, int.class, nmsItemStackClass);

    Object packet =
        SimpleReflection.initializeObject(
            constructor,
            windowId,
            slot,
            SimpleReflection.getMethod(craftItemStackClass, "asNMSCopy", ItemStack.class)
                .invoke(null, itemStack));

    SimpleReflection.Player.sendPacket(who, packet);
  }
}
