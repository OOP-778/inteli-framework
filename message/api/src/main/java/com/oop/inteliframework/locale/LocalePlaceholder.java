package com.oop.inteliframework.locale;

import lombok.Builder;
import lombok.Getter;
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
