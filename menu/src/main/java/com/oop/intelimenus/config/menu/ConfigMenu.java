package com.oop.intelimenus.config.menu;
import com.oop.intelimenus.button.IButton;
import com.oop.intelimenus.button.state.StateComponent;
import com.oop.intelimenus.button.state.StateRequestComponent;
import com.oop.intelimenus.component.ComponentHolder;
import com.oop.intelimenus.config.ConfigDataKeys;
import com.oop.intelimenus.data.DataComponent;
import com.oop.intelimenus.interfaces.MenuItemBuilder;
import com.oop.intelimenus.trigger.TriggerComponent;
import com.oop.intelimenus.trigger.types.ButtonClickTrigger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.NonNull;

public interface ConfigMenu<T> extends ComponentHolder<T> {

    // Register an state request on an button
    default void onStateRequest(@NonNull String identifier, @NonNull Function<IButton, MenuItemBuilder> stateProvider) {
        applyComponent(StateRequestComponent.class, src -> {
            src.register(
                slot -> {
                    if (!slot.getHolder().isPresent()) return false;

                    IButton holder = slot.getHolder().get();
                    if (!holder.getComponent(StateComponent.class).isPresent()) return false;

                    return holder.getComponent(DataComponent.class)
                        .map(dc -> dc.get(ConfigDataKeys.BUTTON_IDENTIFIER.name(), String.class)
                            .filter(identity -> identity.equalsIgnoreCase(identifier))
                            .isPresent())
                        .orElse(false);
                },
                slot -> {
                    IButton iButton = slot.getHolder().get();
                    return stateProvider.apply(iButton);
                }
            );
        });
    }

    // Listen for an specific button click by identifier
    default void onAction(String identifier, Consumer<ButtonClickTrigger> consumer) {
        onAction(
            dc -> dc.has(ConfigDataKeys.BUTTON_IDENTIFIER.name()) && dc
                .get(ConfigDataKeys.BUTTON_IDENTIFIER.name(),
                    String.class).get().equalsIgnoreCase(identifier),
            consumer
        );
    }

    // Listen for an specific button click
    default void onAction(Predicate<DataComponent> predicate,
                          Consumer<ButtonClickTrigger> consumer) {
        applyComponent(TriggerComponent.class, comp -> comp.addTrigger(
            ButtonClickTrigger.class,
            trigger -> trigger.onTrigger(
                event -> {
                    // Check if it contains data component
                    if (!event.getButton().getComponent(DataComponent.class).isPresent()) {
                        return;
                    }

                    // Check if the data component contains action KEY
                    DataComponent dataComponent = event.getButton()
                        .getComponent(DataComponent.class).get();
                    if (!predicate.test(dataComponent)) {
                        return;
                    }

                    consumer.accept(event);
                }
            )
        ));
    }
}
