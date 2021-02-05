package com.oop.inteliframework.config.configuration.handler.impl.yaml;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.config.configuration.handler.ConfigurationHandler;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ValueNode;
import com.oop.inteliframework.config.node.ParentNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

/**
 * Handles YAML configurations
 */
public class YamlConfigurationHandler implements ConfigurationHandler {

    private final Yaml yaml = new Yaml();

    public static void initializeParent(@NonNull ParentNode parent, Map<Object, Object> map) {
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                ParentNode child = new ParentNode(key.toString(), parent);
                initializeParent(child, (Map<Object, Object>) value);

                parent
                    .nodes()
                    .put(key.toString(), child);

            } else {
                ValueNode valueNode = new ValueNode(key.toString(), parent, value);
                parent
                    .nodes()
                    .put(valueNode.key(), valueNode);
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
            ParentNode mainNode = new ParentNode("", null);
            initializeParent(mainNode, data);

            comments.forEach((path, pathComments) -> {
                InteliOptional<Node> nodeAt = mainNode.findAt(path);
                nodeAt.ifPresent(node -> node.comments().addAll(pathComments));
            });

            if (comments.containsKey("#")) {
                mainNode.comments().addAll(comments.get("#"));
            }

            // Initialize comments
            return mainNode;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to load yaml configuration from stream",
                throwable);
        }
    }

    @Override
    public void save(@NonNull Node node, @NonNull File file) {

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
