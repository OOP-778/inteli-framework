package com.oop.inteliframework.config.handler.impl;

import com.oop.inteliframework.config.handler.ConfigurationHandler;
import com.oop.inteliframework.config.node.Node;
import lombok.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handles YAML configurations
 */
public class YamlConfigurationHandler implements ConfigurationHandler {

    @Override
    public Node load(@NonNull InputStream stream) {
        return null;
    }

    @Override
    public void save(@NonNull Node node, @NonNull OutputStream stream) {

    }

    @Override
    public boolean accepts(String filename) {
        return filename.endsWith(".yml");
    }

    @Override
    public String name() {
        return "yaml";
    }
}
