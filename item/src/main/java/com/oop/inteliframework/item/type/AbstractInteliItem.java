package com.oop.inteliframework.item.type;

import com.oop.inteliframework.item.api.SimpleInteliItem;
import com.oop.inteliframework.item.comp.InteliMaterial;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class AbstractInteliItem<
        M extends AbstractInteliItemMeta, T extends AbstractInteliItem>
    implements SimpleInteliItem<M, AbstractInteliItem<M, T>> {
  @Getter(AccessLevel.NONE)
  private @NonNull final ItemStack itemStack;

  @Getter(AccessLevel.NONE)
  private @Nullable final Function<T, T> cloner;

  private @NonNull NBTItem nbtItem;
  private @NonNull M meta;

  public AbstractInteliItem(
      final @NonNull ItemStack itemStack, final @Nullable Function<T, T> cloner) {
    this.itemStack = itemStack;
    this.cloner = cloner;
    nbtItem = new NBTItem(itemStack);
  }

  @Override
  public @NonNull AbstractInteliItem<M, T> clone() {
    return cloner.apply((T) this);
  }

  @Override
  public @Nullable M meta() {
    return meta;
  }

  @Override
  public T meta(@NonNull M meta) {
    this.meta = meta;

    itemStack.setItemMeta(meta.getMeta());
    return (T) this;
  }

  @Override
  public @NonNull InteliMaterial material() {
    return InteliMaterial.matchMaterial(itemStack.getType());
  }

  @Override
  public @NonNull ItemStack asBukkitStack() {
    return itemStack;
  }

  @Override
  public T nbtSupplier(@NonNull Consumer<NBTItem> supplier) {
    supplier.accept(nbtItem);
    return (T) this;
  }

  @Override
  public T nbt(@NonNull NBTItem item) {
    nbtItem = item;
    return (T) this;
  }

  @Override
  public T durability(int durability) {
    itemStack.setDurability((short) durability);
    return (T) this;
  }

  @Override
  public T count(int count) {
    itemStack.setAmount(count);
    return (T) this;
  }

  @Override
  public T metaSupplier(@NonNull Consumer<M> supplier) {
    supplier.accept(meta);
    return (T) this;
  }

  @Override
  public @NonNull NBTItem nbt() {
    return nbtItem;
  }
}
