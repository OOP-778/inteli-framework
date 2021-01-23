package com.oop.inteliframework.config.handler.impl;

import com.oop.inteliframework.config.handler.ConfigurationHandler;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeValuable;
import com.oop.inteliframework.config.node.ParentableNode;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.oop.inteliframework.commons.util.StringFormat.format;

/**
 * Handles YAML configurations
 */
public class YamlConfigurationHandler implements ConfigurationHandler {
    private final Yaml yaml = new Yaml();

    @Override
    public Node load(@NonNull InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try {
            String[] lines = new BufferedReader(reader)
                    .lines()
                    .toArray(String[]::new);
            reader.close();

            Map<String, List<String>> comments = Commentator.comments(lines);
            comments.forEach((path, co) -> {
                System.out.println("path: " + path + ", comments: " + String.join(",", co));
            });

            Map<Object, Object> data = yaml
                    .load(String.join("\n", lines));
            ParentableNode mainNode = new ParentableNode(".", null);
            initializeParent(mainNode, data);

            // Initialize comments
            return mainNode;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to load yaml configuration from stream", throwable);
        }
    }

    public static void initializeParent(@NonNull ParentableNode parent, Map<Object, Object> map) {
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                ParentableNode child = new ParentableNode(key.toString(), parent);
                initializeParent(child, (Map<Object, Object>) value);

                parent
                        .nodes()
                        .put(key.toString(), child);

            } else {
                NodeValuable nodeValuable = new NodeValuable(key.toString(), parent, value);
                parent
                        .nodes()
                        .put(nodeValuable.key(), nodeValuable);
            }
        });
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
