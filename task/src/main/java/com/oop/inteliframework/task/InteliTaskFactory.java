package com.oop.inteliframework.task;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.api.module.InteliModule;
import com.oop.inteliframework.task.api.TaskController;
import com.oop.inteliframework.task.api.TaskFactory;
import com.oop.inteliframework.task.type.InteliTask;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true, chain = true)
public class InteliTaskFactory implements
    InteliModule, TaskFactory<InteliTaskFactory, InteliTask> {
  private final @NonNull List<TaskController<?, InteliTask>> registeredControllers = new ArrayList<>();

  @Override
  public @Nullable InteliPlatform platform() {
    return InteliPlatform.getInstance();
  }
}
