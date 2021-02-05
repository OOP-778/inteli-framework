package com.oop.intelimenus.button.state;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import com.oop.intelimenus.slot.InteliSlot;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Getter;

public class StateRequestComponent implements Component<StateRequestComponent> {

    @Getter
    private final Set<InteliPair<Predicate<InteliSlot>, Function<InteliSlot, MenuItemBuilder>>> requests = new HashSet<>();

    public void register(Predicate<InteliSlot> filter,
        Function<InteliSlot, MenuItemBuilder> function) {
        requests.add(new InteliPair<>(filter, function));
    }

    public void register(int slot, Function<InteliSlot, MenuItemBuilder> function) {
        register(s -> s.getIndex() == slot, function);
    }

    @Override
    public StateRequestComponent clone() {
        StateRequestComponent component = new StateRequestComponent();
        component.requests.addAll(requests);
        return component;
    }

    public Function<InteliSlot, MenuItemBuilder> find(InteliSlot slot) {
        for (InteliPair<Predicate<InteliSlot>, Function<InteliSlot, MenuItemBuilder>> request : requests) {
            if (!request.getKey().test(slot)) {
                continue;
            }
            return request.getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return "StateRequestComponent{" +
            "requests=size{" + requests.size() + "}" +
            '}';
    }
}
