package com.oop.inteliframework;

import com.oop.inteliframework.api.LocaleController;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;

@Getter
@Accessors(fluent = true)
public class InteliMessageModule implements InteliModule {

    private final Audience audience;
    private final LocaleController controller;

    public InteliMessageModule(@NonNull Audience audience, @NonNull LocaleController controller) {
        this.audience = audience;
        this.controller = controller;
    }
}
