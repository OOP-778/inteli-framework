package com.oop.inteliframework.item.api;

import com.oop.inteliframework.item.comp.InteliMaterial;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.function.Consumer;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Simple builder for items.
 *
 * <p>Look at {@link AbstractInteliItem} (Simple implementation of this interface)
 *
 * @param <M> Item meta provider
 * @param <T> Item provider
 */
public interface SimpleInteliItem<M extends SimpleInteliMeta, T extends SimpleInteliItem> {

  /** @return Item meta as {@link SimpleInteliMeta} */
  @Nullable
  M meta();

  /**
   * @param meta Item new meta (Must be {@link SimpleInteliMeta})
   * @throws NullPointerException If meta is null
   */
  T meta(final @NonNull M meta);

  /**
   * @return Material as {@link InteliMaterial}
   * @throws NullPointerException If material is null
   */
  @NonNull
  InteliMaterial material();

  /**
   * @return {@link SimpleInteliItem} as bukkit item stack
   * @throws NullPointerException If stack is null
   */
  @NonNull
  ItemStack asBukkitStack();

  /**
   * @param supplier Will supply changes to current nbt item
   * @throws NullPointerException If supplier is null
   */
  T nbtSupplier(final @NonNull Consumer<NBTItem> supplier);

  /**
   * Will reset nbt item
   *
   * @param item New nbt item ({@link NBTItem}) (WARNING! It'll reset all applied data!)
   * @throws NullPointerException If item is null
   */
  T nbt(final @NonNull NBTItem item);

  /**
   * Sets item durability
   *
   * @param durability Required durability
   */
  T durability(int durability);

  /**
   * Sets item count (64 is maximum!)
   *
   * @param count Required item count
   */
  T count(int count);

  /**
   * Allow to manipulate with already created meta
   *
   * @param supplier Will supply meta changes
   */
  T metaSupplier(final @NonNull Consumer<M> supplier);

  /**
   * @return {@link NBTItem} as NBTAPI item
   * @throws NullPointerException If nbt-item is null
   */
  @NonNull
  NBTItem nbt();

  /**
   * Create a copy of item
   *
   * @return Cloned item
   */
  @NonNull
  T clone();
}
