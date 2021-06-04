package com.oop.inteliframework.menu.button.state;

import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.item.type.item.InteliItem;
import com.oop.inteliframework.menu.component.Component;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class StateComponent implements Component<StateComponent> {

  private final Map<String, AbstractInteliItem<?, ?>> states = new HashMap<>();

  public void addState(String id, AbstractInteliItem<?, ?> item) {
    states.put(id.toLowerCase(), item);
  }

  public void addState(String id, @NonNull ItemStack itemStack) {
    addState(id, new InteliItem(itemStack));
  }

  public boolean hasState(String id) {
    return states.containsKey(id);
  }

  public Optional<AbstractInteliItem<?, ?>> getState(String id) {
    return Optional.ofNullable(states.get(id.toLowerCase()));
  }

  public Collection<String> getStates() {
    return states.keySet();
  }

  @Override
  public StateComponent clone() {
    StateComponent component = new StateComponent();
    states.forEach((key, state) -> component.states.put(key, state.clone()));
    return component;
  }

  @Override
  public String toString() {
    return "StateComponent{"
        + "states="
        + Arrays.toString(
            states.entrySet().stream()
                .map(es -> es.getKey() + " : " + es.getValue().asBukkitStack())
                .toArray())
        + '}';
  }
}
