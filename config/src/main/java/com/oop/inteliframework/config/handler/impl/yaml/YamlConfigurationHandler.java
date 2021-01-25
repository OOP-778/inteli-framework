package com.oop.inteliframework.config.handler.impl.yaml;

import com.oop.inteliframework.config.handler.ConfigurationHandler;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.NodeValuable;
import com.oop.inteliframework.config.node.ParentableNode;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Handles YAML configurations
 */
public class YamlConfigurationHandler implements ConfigurationHandler {
    private final Yaml yaml = new Yaml();

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
    public Node load(@NonNull InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
        try {
            String[] lines = new BufferedReader(reader)
                    .lines()
                    .toArray(String[]::new);
            reader.close();

            Map<String, List<String>> comments = Commentator.comments(lines);
            Map<Object, Object> data = yaml
                    .load(String.join("\n", lines));
            ParentableNode mainNode = new ParentableNode("", null);
            initializeParent(mainNode, data);

            comments.forEach((path, pathComments) -> {
                Optional<Node> nodeAt = mainNode.findNodeAt(path);
                nodeAt.ifPresent(node -> node.comments().addAll(pathComments));
            });

            if (comments.containsKey("#"))
                mainNode.comments().addAll(comments.get("#"));

            // Initialize comments
            return mainNode;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to load yaml configuration from stream", throwable);
        }
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
