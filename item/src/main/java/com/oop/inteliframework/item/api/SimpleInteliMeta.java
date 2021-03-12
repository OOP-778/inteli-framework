package com.oop.inteliframework.item.api;

import com.oop.inteliframework.item.api.holder.EnchantHolder;
import com.oop.inteliframework.item.api.holder.FlagHolder;
import com.oop.inteliframework.item.type.AbstractInteliItemMeta;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.NonNull;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Simple item meta builder for items.
 *
 * <p>Look at {@link AbstractInteliItemMeta} (Simple implementation of this interface)
 *
 * @param <M> Item meta (Use bukkit types, example {@link SkullMeta})
 * @param <T> Meta provider
 */
public interface SimpleInteliMeta<M extends ItemMeta, T extends SimpleInteliMeta, L extends SimpleInteliLore>
    extends Nameable<T>, EnchantHolder<T>, FlagHolder<T> {

  /**
   * Handle lore applying (With features)
   *
   * @param loreProvider Handle lore provider
   * */
  T lore(L loreProvider);

  /**
   * Handle default lore applying
   *
   * @param lines Lore lines
   * @throws NullPointerException If lines is null
   * */
  T lore(final @NonNull String... lines);

  /** @param enable Enable simple glowing for item */
  T glowing(final boolean enable);

  /**
   * @return As bukkit meta
   * @throws NullPointerException If meta is null
   */
  @NonNull
  M asBukkitMeta();

  /**
   * @return Return lore provider
   * */
  @Nullable
  L lore();

  /**
   * @param supplier Lore supplier
   * @throws NullPointerException If supplier is null
   */
  T loreSupplier(final @NonNull Consumer<L> supplier);

  /**
   * Create a copy of meta
   *
   * @return Cloned meta
   */
  @NonNull
  T clone();
}
