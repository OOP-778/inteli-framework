package com.oop.inteliframework.bukkit.entity.tracker;

import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.task.api.Task;
import com.oop.inteliframework.task.api.TaskController;
import com.oop.inteliframework.task.type.InteliTask;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class InteliPlayerTracker implements InteliModule {
  private final Map<String, TrackerSystem> trackerSystemMap =
      new TreeMap<>(String::compareToIgnoreCase);

  @Getter private TaskController<?, Task<?>> taskController;

  public InteliPlayerTracker(TaskController taskController) {
    this.taskController = taskController;
    new InteliTask(taskController)
        .body(
            $ -> {
              for (TrackerSystem value : trackerSystemMap.values()) {
                value.update();
              }
            })
        .repeatable(true)
        .delay(TimeUnit.SECONDS, 1)
        .run();
  }

  public void registerTracker(TrackerSystem system) {
    trackerSystemMap.put(system.getName(), system);
  }

  public Optional<TrackerSystem> get(String name) {
    return Optional.ofNullable(trackerSystemMap.get(name));
  }

  public TrackerSystem getOrCreate(String name, Supplier<TrackerSystem> supplier) {
    return trackerSystemMap.computeIfAbsent(name, $ -> supplier.get());
  }
}
