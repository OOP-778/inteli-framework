package com.oop.inteliframework.task;

import com.oop.inteliframework.plugin.module.InteliModule;
import com.oop.inteliframework.task.api.TaskController;
import com.oop.inteliframework.task.api.TaskFactory;
import com.oop.inteliframework.task.type.InteliTask;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true, chain = true)
public class InteliTaskFactory implements InteliModule, TaskFactory<InteliTaskFactory, InteliTask> {
  private final @NonNull List<TaskController<?, InteliTask>> registeredControllers =
      new ArrayList<>();
}
