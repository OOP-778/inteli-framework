package com.oop.inteliframework.menu.interfaces;

import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface Stateable<T extends Stateable> {

  Map<String, AbstractInteliItem> getStates();

  default T registerDefaultState(@NonNull ItemStack itemStack) {
    registerState("default", itemStack);
    return (T) this;
  }

  default T registerState(@NonNull String id, @NonNull ItemStack itemStack) {
    getStates().remove(id.toLowerCase());
    getStates().put(id.toLowerCase(), new InteliItem(itemStack));
    return (T) this;
  }

  default Optional<AbstractInteliItem<?, ?>> getDefaultState() {
    return getState("default");
  }

  default Optional<AbstractInteliItem<?, ?>> getState(String id) {
    return Optional.ofNullable(getStates().get(id.toLowerCase()));
  }
}
