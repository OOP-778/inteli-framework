package com.oop.inteliframework.menu.button.state;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.item.type.AbstractInteliItem;
import com.oop.inteliframework.menu.component.Component;
import com.oop.inteliframework.menu.slot.InteliSlot;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class StateRequestComponent implements Component<StateRequestComponent> {

  @Getter
  private final Set<
          InteliPair<Predicate<InteliSlot>, Function<InteliSlot, AbstractInteliItem<?, ?>>>>
      requests = new HashSet<>();

  public void register(
      Predicate<InteliSlot> filter, Function<InteliSlot, AbstractInteliItem<?, ?>> function) {
    requests.add(new InteliPair<>(filter, function));
  }

  public void register(int slot, Function<InteliSlot, AbstractInteliItem<?, ?>> function) {
    register(s -> s.getIndex() == slot, function);
  }

  @Override
  public StateRequestComponent clone() {
    StateRequestComponent component = new StateRequestComponent();
    component.requests.addAll(requests);
    return component;
  }

  public Function<InteliSlot, AbstractInteliItem<?, ?>> find(InteliSlot slot) {
    for (InteliPair<Predicate<InteliSlot>, Function<InteliSlot, AbstractInteliItem<?, ?>>> request :
        requests) {
      if (!request.getKey().test(slot)) {
        continue;
      }
      return request.getValue();
    }
    return null;
  }

  @Override
  public String toString() {
    return "StateRequestComponent{" + "requests=size{" + requests.size() + "}" + '}';
  }
}
