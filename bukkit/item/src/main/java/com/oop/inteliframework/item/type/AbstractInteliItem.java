package com.oop.inteliframework.item.type;

import com.oop.inteliframework.bukkit.nbt.InteliNbt;
import com.oop.inteliframework.bukkit.nbt.NBTItem;
import com.oop.inteliframework.item.api.SimpleInteliItem;
import com.oop.inteliframework.item.comp.InteliMaterial;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInteliItem<
    M extends AbstractInteliItemMeta, T extends AbstractInteliItem>
    implements SimpleInteliItem<M, AbstractInteliItem<M, T>> {

  @Getter(AccessLevel.NONE)
  private @Nullable
  final Function<T, T> cloner;

  @Getter(AccessLevel.NONE)
  private @NonNull ItemStack itemStack;

  private M meta;
  private NBTItem nbt;

  public AbstractInteliItem(
      final @NonNull ItemStack itemStack, final @Nullable Function<T, T> cloner) {
    this.itemStack = itemStack;
    this.cloner = cloner;
    this.nbt = new NBTItem(itemStack);
  }

  @Override
  public @NonNull AbstractInteliItem<M, T> clone() {
    return cloner.apply((T) this);
  }

  @Override
  public @Nullable M meta() {
    return meta == null ? _createMeta() : meta;
  }

  @Override
  public T meta(@NonNull M meta) {
    this.meta = meta;

    itemStack.setItemMeta(meta.asBukkitMeta());
    return (T) this;
  }

  @Override
  public @NonNull InteliMaterial material() {
    return InteliMaterial.matchMaterial(itemStack.getType());
  }

  @Override
  public @NonNull ItemStack asBukkitStack() {
    return InteliNbt.getTags().setItemStackTag(itemStack, nbt);
  }

  @Override
  public T applyNBT(@NonNull Consumer<NBTItem> consumer) {
    consumer.accept(nbt);
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
  public T applyMeta(@NonNull Consumer<M> supplier) {
    if (meta == null) {
      meta = _createMeta();
    }

    supplier.accept(meta);

    itemStack.setItemMeta(meta.asBukkitMeta());
    return (T) this;
  }

  @Override
  public <O> O provideWithMeta(@NonNull Function<M, O> provider) {
    if (meta == null) {
      meta = _createMeta();
    }
    return provider.apply(meta);
  }

  protected abstract M _createMeta();
}
