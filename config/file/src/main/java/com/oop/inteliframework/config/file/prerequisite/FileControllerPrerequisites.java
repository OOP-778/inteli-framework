package com.oop.inteliframework.config.file.prerequisite;

import com.oop.inteliframework.config.file.FileController;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.util.function.Consumer;

public class FileControllerPrerequisites {
  private final FileController controller;

  public FileControllerPrerequisites(FileController controller) {
    this.controller = controller;
  }

  @SneakyThrows
  public void makeSureFolderExists() {
    if (!Files.isDirectory(controller.getPath())) {
      Files.createDirectory(controller.getPath());
    }
  }

  public void loadFromResources(@NonNull Consumer<LoadFromResourcesPrerequisite> consumer) {
    LoadFromResourcesPrerequisite prerequisite = new LoadFromResourcesPrerequisite(controller);
    consumer.accept(prerequisite);

    prerequisite.load();
  }
}
