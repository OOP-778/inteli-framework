package com.oop.inteliframework.task.api;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.task.type.AbstractTaskController;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Here you need to implement run and cancel task logic
 *
 * @see AbstractTaskController
 * @see InteliTaskController
 */
public interface TaskController<T extends TaskController, R extends Task> {

  /** @param taskProvider Create and run new task */
  T runTask(final @NonNull R taskProvider);

  /** @param taskId Task id */
  T cancelTask(final long taskId);

  /**
   * @param taskId Task id
   * @return Task as {@link InteliOptional}
   */
  InteliOptional<R> taskById(final long taskId);

  /** @return All running tasks with id's */
  @NonNull
  Map<Long, R> runningTasks();

  /** Shutdown Task Controller */
  void shutdown();

  /** Prepare and return a task */
  R prepareTask(Consumer<R> taskConsumer);
}
