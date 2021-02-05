package com.oop.intelimenus.button.state;

import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.inventory.ItemStack;

public class StateComponent implements Component<StateComponent> {

    private final Map<String, MenuItemBuilder> states = new HashMap<>();

    public void addState(String id, MenuItemBuilder builder) {
        states.put(id.toLowerCase(), builder);
    }

    public void addState(String id, ItemStack itemStack) {
        addState(id, MenuItemBuilder.of(itemStack));
    }

    public boolean hasState(String id) {
        return states.containsKey(id);
    }

    public Optional<MenuItemBuilder> getState(String id) {
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
        return "StateComponent{" +
            "states=" + Arrays.toString(
            states.entrySet().stream().map(es -> es.getKey() + " : " + es.getValue().getItem())
                .toArray()) +
            '}';
    }
}
