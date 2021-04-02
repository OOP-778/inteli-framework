package com.oop.inteliframework.config.api.configuration.handler;

import com.oop.inteliframework.config.api.configuration.handler.impl.yaml.YamlConfigurationHandler;
import java.util.Optional;
import java.util.TreeMap;
import lombok.NonNull;

public class ConfigurationHandlers {

    private static final TreeMap<String, ConfigurationHandler> registeredHandlers = new TreeMap<>(
        String::compareToIgnoreCase);

    static {
        registerHandler(new YamlConfigurationHandler());
    }

    public static void registerHandler(@NonNull ConfigurationHandler configurationHandler) {
        registeredHandlers.put(configurationHandler.name(), configurationHandler);
    }

    public static Optional<ConfigurationHandler> findHandler(@NonNull String filename) {
        for (ConfigurationHandler value : registeredHandlers.values()) {
            if (value.accepts(filename)) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }
}
