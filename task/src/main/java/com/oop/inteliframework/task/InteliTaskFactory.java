package com.oop.inteliframework.task;

import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.task.api.TaskController;
import com.oop.inteliframework.task.api.TaskFactory;
import com.oop.inteliframework.task.type.InteliTask;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true, chain = true)
public class InteliTaskFactory implements InteliModule, TaskFactory<InteliTaskFactory> {

  private final @NonNull List<TaskController> registeredControllers = new ArrayList<>();

  public InteliTaskFactory() {
    platform()
        .hookDisable(
            () -> {
              removeAll();
            });
  }

  @Override
  public @NonNull List<TaskController> registeredControllers() {
    return registeredControllers;
  }

  @Override
  public <T1 extends TaskController> void removeController(Class<T1> clazz) {
    registeredControllers.removeIf(
        controller -> {
          boolean remove = controller.getClass() == clazz;
          if (remove) {
            controller.shutdown();
          }

          return remove;
        });
  }

  public void removeAll() {
    for (TaskController<?, InteliTask> registeredController : registeredControllers) {
      registeredController.shutdown();
    }

    registeredControllers.clear();
  }
}
