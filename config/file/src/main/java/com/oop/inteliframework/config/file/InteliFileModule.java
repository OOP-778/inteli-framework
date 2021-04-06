package com.oop.inteliframework.config.file;

import com.oop.inteliframework.config.file.watcher.FileWatcher;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;

import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class InteliFileModule implements InteliModule {

  @Getter
  private final FileWatcher watcher = new FileWatcher();

  private final TreeMap<String, FileController> controllerTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  public InteliFileModule registerController(FileController... controllers) {
    for (FileController controller : controllers) {
      controllerTreeMap.put(controller.getPath().getFileName().toString(), controller);
    }
    return this;
  }

  public void startWatcher() {
    ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    scheduledExecutorService.scheduleAtFixedRate(
            watcher::watch,
            0,
            1,
            TimeUnit.SECONDS
    );

    scheduledExecutorService.scheduleAtFixedRate(
            watcher::fire,
            0,
            3,
            TimeUnit.SECONDS
    );
  }
}
