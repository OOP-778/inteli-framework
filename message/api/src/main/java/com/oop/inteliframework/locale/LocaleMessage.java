package com.oop.inteliframework.locale;

import com.oop.inteliframework.api.InteliMessage;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Getter
public class LocaleMessage {
    private @Nullable InteliMessage message;
    private final List<LocalePlaceholder> placeholders = new LinkedList<>();

    public LocaleMessage withPlaceholder(@NonNull String key, @Nullable String description) {
        this.placeholders.add(new LocalePlaceholder(key, description));
        return this;
    }

    public LocaleMessage withMessage(InteliMessage message) {
        this.message = message;
        return this;
    }
}
