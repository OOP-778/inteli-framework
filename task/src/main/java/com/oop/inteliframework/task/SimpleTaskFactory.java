package com.oop.inteliframework.task;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.task.type.InteliTask;
import com.oop.inteliframework.task.type.inteli.InteliTaskController;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SimpleTaskFactory {

  private final InteliTaskController classicTaskController =
      InteliPlatform
          .getInstance()
          .moduleByClass(InteliTaskFactory.class)
          .get()
          .controllerByClass(InteliTaskController.class)
          .get();

  public @NonNull InteliTask timer(final @NonNull Consumer<InteliTask> body, final long delay,
      final @NonNull
          TimeUnit unit) {
    return new InteliTask(classicTaskController)
        .body(body)
        .delay(unit, delay);
  }

  public @NonNull InteliTask timer(final @NonNull Consumer<InteliTask> body, final long delay) {
    return timer(body, delay, TimeUnit.MILLISECONDS);
  }

  public @NonNull InteliTask schedule(final @NonNull Consumer<InteliTask> body, final long delay,
      final @NonNull
          TimeUnit unit) {
    return new InteliTask(classicTaskController)
        .repeatable(true)
        .body(body)
        .delay(unit, delay);
  }

}
