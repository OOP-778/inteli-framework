package com.oop.inteliframework.config.file.prerequisite;

import com.oop.inteliframework.config.file.FileController;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.function.Predicate;

@Setter
@Accessors(fluent = true, chain = true)
public class LoadFromResourcesPrerequisite {

  private final FileController controller;
  public LoadFromResourcesPrerequisite(FileController fileController) {
    this.controller = fileController;
  }

  private Predicate<String> filter;
  private Paths.CopyOption option = Paths.CopyOption.COPY_IF_NOT_EXIST;

  protected void load() {
    Paths.copyResourcesFromJar(
            filter,
            InteliPlatform.getInstance().starter().getClass(),
            new File(controller.getPath().toAbsolutePath().toString()),
            option
    );
  }
}
