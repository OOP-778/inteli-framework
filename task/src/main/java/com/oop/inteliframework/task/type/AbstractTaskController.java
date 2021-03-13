package com.oop.inteliframework.task.type;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.task.api.TaskController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTaskController<T extends AbstractTaskController>
    implements TaskController<T, InteliTask> {

  private final Map<Integer, InteliTask> tasks = new ConcurrentHashMap<>();

  @Override
  public T runTask(InteliTask taskProvider) {
    tasks.put(taskProvider.taskId(), taskProvider);
    _runTask(taskProvider);
    return (T) this;
  }

  @Override
  public T cancelTask(int taskId) {
    tasks.remove(taskId);
    _cancelTask(taskId);
    return (T) this;
  }

  @Override
  public InteliOptional<InteliTask> taskById(int taskId) {
    return InteliOptional.ofNullable(tasks.get(taskId));
  }

  @Override
  public Map<Integer, InteliTask> runningTasks() {
    return tasks;
  }

  protected abstract void _runTask(InteliTask taskProvider);

  protected abstract void _cancelTask(int taskId);
}
