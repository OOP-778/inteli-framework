package com.oop.inteliframework.config.api.configuration;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandler;
import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.Node;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Accessors(fluent = true)
public class PlainConfig extends BaseParentNode {

  @Setter @Getter protected ConfigurationHandler handler;

  @Getter protected File file;

  @SneakyThrows
  public PlainConfig(@NonNull File file) {
    this.file = file;

    if (!file.exists()) {
      file.createNewFile();
    }

    handler =
        ConfigurationHandlers.findHandler(file.getName())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Failed to find configuration handler for filename: " + file.getName()));
  }

  public PlainConfig(String filename, @NonNull InputStream stream) {
    handler =
        ConfigurationHandlers.findHandler(filename)
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Failed to find configuration handler for filename: " + filename));

    try {
      Node loaded = handler.load(stream);
      Preconditions.checkArgument(
          loaded instanceof BaseParentNode, "Loaded node must be parentable!");
      merge((BaseParentNode) loaded);
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          format("Failed to load file with name: {} with handler: {}", filename, handler.name()),
          throwable);
    }
  }

  @SneakyThrows
  protected static boolean isEmpty(File file) {
    // If file is fully empty
    if (file.length() == 0) return true;

    // If file contains just spaces or tabs
    List<String> lines = Files.readAllLines(file.toPath());
    return lines.stream().allMatch(line -> line.trim().isEmpty());
  }

  public void load() {
    try {
      nodes.clear();
      if (isEmpty(file)) return;

      Node loaded = handler.load(new FileInputStream(file));
      Preconditions.checkArgument(
          loaded instanceof BaseParentNode, "Loaded node must be parentable!");
      merge((BaseParentNode) loaded);
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          format(
              "Failed to load file with name: {} with handler: {}", file.getName(), handler.name()),
          throwable);
    }
  }
}
