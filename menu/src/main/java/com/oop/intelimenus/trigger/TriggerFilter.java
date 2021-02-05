package com.oop.intelimenus.trigger;

import com.oop.intelimenus.trigger.types.MenuTrigger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TriggerFilter<T extends MenuTrigger> {

    private final List<Predicate<T>> filters = new ArrayList<>();

    public void addFilter(Predicate<T> filter) {
        filters.add(filter);
    }

    public boolean accepts(MenuTrigger event) {
        return filters.stream().allMatch(filter -> filter.test((T) event));
    }
}
