package com.oop.inteliframework.entity.armorstand.nms;

import com.oop.inteliframework.adapters.VersionedAdapter;
import com.oop.inteliframework.adapters.VersionedAdapters;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public interface WrappedArmorStand extends VersionedAdapter {
  BiFunction<Location, Supplier<Collection<Player>>, WrappedArmorStand> supplier =
      requestFunction();

  static BiFunction<Location, Supplier<Collection<Player>>, WrappedArmorStand> requestFunction() {
    Optional<Class<WrappedArmorStand>> wrappedArmorStandClass =
        VersionedAdapters.loadClass(
            WrappedArmorStand.class.getPackage().getName(),
            "ImplWrappedArmorStand",
            WrappedArmorStand.class);
    if (!wrappedArmorStandClass.isPresent())
      throw new IllegalStateException(
          format("Unsupported Version ({}), no class found.", VersionedAdapters.version));

    Constructor<WrappedArmorStand> constructor;
    try {
      Class<WrappedArmorStand> clazz = wrappedArmorStandClass.get();
      constructor = clazz.getDeclaredConstructor(Location.class, Supplier.class);
      constructor.setAccessible(true);
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          format("Unsupported Version ({}), no constructor found.", VersionedAdapters.version));
    }

    return (location, line) -> {
      try {
        return constructor.newInstance(location, line);
      } catch (Throwable e) {
        e.printStackTrace();
      }
      return null;
    };
  }

  void setGravity(boolean gravity);

  void setMarker(boolean marker);

  void update(Player... players);

  void spawn(Player player);

  void remove(Player... players);

  void setLocation(Location location);

  void setCustomName(String customName);

  void setCustomNameVisibility(boolean visibility);

  void outputLocation();

  void setSmall(boolean small);

  void setVisible(boolean visible);

  ItemStack getItem(Player player);

  void outputItem(Player player, ItemStack itemStack);

  void outputName(Player player, String text);

  void setupItem();
}
