package com.oop.inteliframework.config.handler;

import lombok.NonNull;

import java.util.Optional;
import java.util.TreeMap;

public class ConfigurationHandlers {
    private static final TreeMap<String, ConfigurationHandler> registeredHandlers = new TreeMap<>(String::compareToIgnoreCase);

    public static void registerHandler(@NonNull ConfigurationHandler configurationHandler) {
        registeredHandlers.put(configurationHandler.name(), configurationHandler);
    }

    public static Optional<ConfigurationHandler> findHandler(@NonNull String filename) {
        for (ConfigurationHandler value : registeredHandlers.values()) {
            if (value.accepts(filename))
                return Optional.of(value);
        }

        return Optional.empty();
    }
}
