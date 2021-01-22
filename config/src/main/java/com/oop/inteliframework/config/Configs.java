package com.oop.inteliframework.config;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.configuration.AssociatedConfig;
import com.oop.inteliframework.config.configuration.PlainConfig;

import java.util.Optional;
import java.util.TreeMap;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public class Configs {
    private final TreeMap<String, PlainConfig> plainConfigs = new TreeMap<>(String::compareToIgnoreCase);
    private final TreeMap<String, AssociatedConfig> associatedConfigs = new TreeMap<>(String::compareToIgnoreCase);

    public <T> Optional<T> find(Class<T> type, String name) {
        AssociatedConfig associatedConfig = associatedConfigs.get(name);
        if (associatedConfig == null) return Optional.empty();

        Preconditions.checkArgument(
                type.isAssignableFrom(associatedConfig.getHolder().getClass()),
                format("The given type is not the same as found type. Found: {}, required: {}", associatedConfig.getHolder().getClass().getSimpleName(), type.getSimpleName())
        );

        return Optional.of((T) associatedConfig.getHolder());
    }
}
