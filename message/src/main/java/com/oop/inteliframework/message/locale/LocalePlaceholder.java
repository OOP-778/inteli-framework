package com.oop.inteliframework.message.locale;

import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Builder
public class LocalePlaceholder {

    private String key;
    private @Nullable String description;

    public LocalePlaceholder(@NotNull String key, @Nullable String description) {
        this.key = key;
        this.description = description;
    }
}
