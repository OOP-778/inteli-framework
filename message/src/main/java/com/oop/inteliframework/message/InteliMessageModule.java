package com.oop.inteliframework.message;

import com.oop.inteliframework.message.api.LocaleController;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;

@Getter
@Accessors(fluent = true)
public class InteliMessageModule implements InteliModule {

    private final Audience audience;
    private final LocaleController controller;

    @Setter
    private MiniMessage componentCreator;

    public InteliMessageModule(@NonNull Audience audience, @NonNull LocaleController controller, MiniMessage componentCreator) {
        this.audience = audience;
        this.controller = controller;
        this.componentCreator = componentCreator;
    }

}
