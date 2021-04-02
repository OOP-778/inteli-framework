package com.oop.inteliframework.item.type;

import com.oop.inteliframework.item.api.SimpleInteliMeta;
import com.oop.inteliframework.item.comp.InteliEnchantment;
import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.oop.inteliframework.commons.util.StringFormat.colored;
import static com.oop.inteliframework.commons.util.StringFormat.colorizeCollection;

public abstract class AbstractInteliItemMeta<M extends ItemMeta, T extends AbstractInteliItemMeta>
    implements SimpleInteliMeta<M, T, InteliLore> {
  private final @NonNull Function<T, T> cloner;
  private final @NonNull M meta;
  private InteliLore lore;

  public AbstractInteliItemMeta(final @NonNull M meta, final @NonNull Function<T, T> cloner) {
    this.meta = meta;
    this.cloner = cloner;

    this.lore = new InteliLore(meta.hasLore() ? meta.getLore() : new ArrayList<>());
  }

  @Override
  public T clone() {
    return cloner.apply((T) this);
  }

  @Override
  public T name(@NonNull String name) {
    meta.setDisplayName(colored(name));
    return (T) this;
  }

  @Override
  public T nameSupplier(@NonNull Supplier<String> supplier) {
    name(supplier.get());
    return (T) this;
  }

  @Override
  public @Nullable String name() {
    return meta.getDisplayName();
  }

  @Override
  public T lore(@NonNull String... lines) {
    lore.lore(colorizeCollection(Arrays.asList(lines)));
    return (T) this;
  }

  @Override
  public @NonNull M asBukkitMeta() {
    meta.setLore(lore.lore());
    return meta;
  }

  @Override
  public T enchant(@NonNull InteliEnchantment enchant) {
    enchant(enchant, 1);
    return (T) this;
  }

  @Override
  public T enchant(@NonNull InteliEnchantment enchant, int level) {
    enchant(enchant, level, true);
    return (T) this;
  }

  @Override
  public T enchant(@NonNull InteliEnchantment enchant, int level, boolean restrictEnchants) {
    meta.addEnchant(enchant.parseEnchantment(), level, restrictEnchants);
    return (T) this;
  }

  @Override
  public T removeEnchant(@NonNull InteliEnchantment enchant) {
    meta.removeEnchant(enchant.parseEnchantment());
    return (T) this;
  }

  @Override
  public @NonNull Map<Enchantment, Integer> enchants() {
    return meta.getEnchants();
  }

  @Override
  public T flag(@NonNull ItemFlag flag) {
    flags(flag);
    return (T) this;
  }

  @Override
  public T flags(@NonNull ItemFlag... flags) {
    meta.addItemFlags(flags);
    return (T) this;
  }

  @Override
  public T removeFlag(@NonNull ItemFlag flag) {
    meta.removeItemFlags(flag);
    return (T) this;
  }

  @Override
  public @NonNull Set<ItemFlag> flags() {
    return meta.getItemFlags();
  }

  @Override
  public T glowing(boolean enable) {
    enchant(InteliEnchantment.DAMAGE_ALL, 1).flag(ItemFlag.HIDE_ENCHANTS);
    return (T) this;
  }

  @Override
  public T applyLore(@NonNull Consumer<InteliLore> supplier) {
    supplier.accept(lore);
    return (T) this;
  }

  @Override
  public T lore(InteliLore loreProvider) {
    this.lore = loreProvider;
    return (T) this;
  }

  @Override
  public InteliLore lore() {
    return lore;
  }
}
