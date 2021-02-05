package com.oop.intelimenus.trigger;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.intelimenus.component.Component;
import com.oop.intelimenus.trigger.types.MenuTrigger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;

public class TriggerComponent implements Component<TriggerComponent> {

    @Getter
    private final Set<InteliPair<Class, Trigger>> triggers = new HashSet<>();

    public <E extends MenuTrigger> void addTrigger(Class<E> eventClass,
        Consumer<Trigger<E>> apply) {
        Trigger<E> trigger = new Trigger<>();
        trigger.addFilter(event -> eventClass.isAssignableFrom(event.getClass()));
        apply.accept(trigger);

        triggers.add(new InteliPair<>(eventClass, trigger));
    }

    @Override
    public TriggerComponent clone() {
        TriggerComponent component = new TriggerComponent();
        component.triggers.addAll(triggers);
        return component;
    }

    public <T extends MenuTrigger> List<Trigger<T>> triggers(T trigger) {
        return triggers
            .stream()
            .filter(t -> t.getKey().isAssignableFrom(trigger.getClass()))
            .filter(t -> t.getValue().accepts(trigger))
            .map(InteliPair::getValue)
            .map(t -> (Trigger<T>) t)
            .collect(Collectors.toList());
    }

    public void trigger(MenuTrigger trigger) {
        triggers
            .stream()
            .filter(t -> t.getKey().isAssignableFrom(trigger.getClass()))
            .filter(t -> t.getValue().accepts(trigger))
            .forEach(t -> t.getValue().getFinalExecutor().accept(trigger));
    }

    @Override
    public String toString() {
        return "TriggerComponent{" +
            "triggers=" + triggers.size() + " in memory" +
            '}';
    }
}
