package com.oop.inteliframework.task.type.inteli;

import com.oop.inteliframework.task.type.AbstractTaskController;
import com.oop.inteliframework.task.type.InteliTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class InteliTaskController extends AbstractTaskController<InteliTaskController> {

  private final ScheduledExecutorService scheduler;

  public InteliTaskController(int threadsCount) {
    if (threadsCount <= 0) {
      threadsCount = 1;
    }

    scheduler =
        Executors.newScheduledThreadPool(
            threadsCount,
            run -> {
              final Thread thread =
                  new Thread(
                      run, "inteli-framework worker N" + ThreadLocalRandom.current().nextInt(100));
              thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
              return thread;
            });
  }

  @Override
  protected void _runTask(InteliTask taskProvider) {
    final Runnable runnable = taskProvider.runnable();

    if (!taskProvider.repeatable()) {
      if (taskProvider.delay() != -1)
        scheduler.schedule(runnable, taskProvider.delay(), TimeUnit.MILLISECONDS);
    } else if (taskProvider.delay() != -1) {
      scheduler.scheduleAtFixedRate(runnable, 0, taskProvider.delay(), TimeUnit.MILLISECONDS);
    } else scheduler.execute(runnable);
  }

  @Override
  protected void _cancelTask(int taskId) {
    InteliTask task = taskById(taskId).orElse(null);
    if (task != null) {
      task.cancel();
    }
  }
}
