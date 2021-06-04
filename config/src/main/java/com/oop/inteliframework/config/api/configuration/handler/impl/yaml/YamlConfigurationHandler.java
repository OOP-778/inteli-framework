package com.oop.inteliframework.config.api.configuration.handler.impl.yaml;

import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandler;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.BaseValueNode;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.node.api.iterator.NodeIterator;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.oop.inteliframework.commons.util.Helper.useAndProduce;

/** Handles YAML configurations */
public class YamlConfigurationHandler implements ConfigurationHandler {

  private static final Yaml yaml;

  static {
    DumperOptions dumperOptions = new DumperOptions();

    dumperOptions.setAllowUnicode(true);
    dumperOptions.setPrettyFlow(true);
    dumperOptions.setSplitLines(true);
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    yaml = new Yaml(dumperOptions);
  }

  public static void initializeParent(@NonNull BaseParentNode parent, Map<Object, Object> map) {
    map.forEach(
        (key, value) -> {
          if (value instanceof Map) {
            BaseParentNode child = new BaseParentNode();
            initializeParent(child, (Map<Object, Object>) value);

            parent.nodes().put(key.toString(), child);

          } else {
            BaseValueNode valueNode = new BaseValueNode(value);
            parent.nodes().put(key.toString(), valueNode);
          }
        });
  }

  @SneakyThrows
  private static String normalizeYamlDump(int spaces, String input) {
    if (StringUtils.contains(input, "- ")) {
      String spacesChars = getCharXTimes(spaces + 2, ' ');

      return "\n"
          + Arrays.stream(StringUtils.split(input, "-"))
              .map(value -> spacesChars + "-" + value)
              .collect(Collectors.joining(""));
    }
    return input;
  }

  @SneakyThrows
  private static void printHeader(BufferedWriter writer, Node node) {
    writer.write("# <--------------->\n");
    for (String comment : node.comments()) {
      writer.write("# " + comment + "\n");
    }
    writer.write("# <--------------->\n");
    writer.write("\n");
  }

  public static String getCharXTimes(int amount, char character) {
    return IntStream.range(1, amount).mapToObj(i -> character + "").collect(Collectors.joining());
  }

  @SneakyThrows
  private static void appendComments(BufferedWriter writer, int spaces, Node node) {
    List<String> comments = node.comments();
    if (comments.isEmpty()) return;

    String spacesChars = getCharXTimes(spaces, ' ');

    boolean beautify = comments.size() > 2;
    String beautifyingWith = "";
    if (beautify) {
      int longestLen = Math.max(6, comments.stream().mapToInt(String::length).max().getAsInt()) + 2;
      beautifyingWith = getCharXTimes(longestLen, '-');
      writer.write(spacesChars + "# " + beautifyingWith + "\n");
    }

    for (String comment : comments) {
      writer.write(spacesChars + "# " + comment + "\n");
    }

    if (beautify) writer.write(spacesChars + "# " + beautifyingWith + "\n");
  }

  @Override
  public ParentNode load(@NonNull InputStream stream) {
    InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
    try {
      String[] lines = new BufferedReader(reader).lines().toArray(String[]::new);
      reader.close();

      Map<String, List<String>> comments = Commentator.comments(lines);
      Map<Object, Object> data = yaml.load(String.join("\n", lines));
      BaseParentNode mainNode = new BaseParentNode();
      initializeParent(mainNode, data);

      comments.forEach(
          (path, pathComments) -> {
            mainNode.ifPresent(path, node -> node.comments().addAll(pathComments));
          });

      if (comments.containsKey("#")) {
        mainNode.comments().addAll(comments.get("#"));
      }

      // Initialize comments
      return mainNode;
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to load yaml configuration from stream", throwable);
    }
  }

  @Override
  @SneakyThrows
  public void save(@NonNull ParentNode node, @NonNull File file) {
    try (OutputStreamWriter otw =
        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      BufferedWriter writer = new BufferedWriter(otw);
      // Write header of the node
      if (node instanceof BaseParentNode && !node.comments().isEmpty()) {
        printHeader(writer, node);
      }

      Consumer<Node> emptySpace =
          (node1) -> {
            try {
              if (node1.comments().isEmpty()) return;
              writer.write("\n");
            } catch (IOException e) {
              e.printStackTrace();
            }
          };

      int space = 0;
      for (Map.Entry<String, Node> hierarchyEntry : node.map(NodeIterator.HIERARCHY).entrySet()) {
        // Adjust spaces if needed
        space =
            useAndProduce(
                StringUtils.split(hierarchyEntry.getKey(), ".").length,
                len -> {
                  return len == 1 ? 0 : len * 2;
                });

        // Append comments if has any
        appendComments(writer, space, hierarchyEntry.getValue());

        String key;
        String[] keySplit = StringUtils.split(hierarchyEntry.getKey(), ".");
        if (keySplit.length > 1) key = keySplit[keySplit.length - 1];
        else key = keySplit[0];

        // Write value if value
        if (hierarchyEntry.getValue() instanceof BaseValueNode) {
          writer.write(
              getCharXTimes(space, ' ')
                  + key
                  + ": "
                  + normalizeYamlDump(
                      space, yaml.dump(((BaseValueNode) hierarchyEntry.getValue()).value())));
          emptySpace.accept(hierarchyEntry.getValue());
          continue;
        }

        writer.write(getCharXTimes(space, ' ') + key + ":" + "\n");
        emptySpace.accept(hierarchyEntry.getValue());
      }

      writer.flush();
    }
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
