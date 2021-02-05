package com.oop.intelimenus.trigger;

import com.oop.intelimenus.trigger.types.MenuTrigger;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class Trigger<T extends MenuTrigger> extends TriggerFilter<T> {

    @Setter
    @Accessors(chain = true, fluent = true)
    private Consumer<T> onTrigger;

    @Getter
    private final Consumer<T> finalExecutor = event -> {
        if (!accepts(event)) {
            return;
        }

        Objects.requireNonNull(onTrigger, "On Trigger Cannot Be Null").accept(event);
    };
}
