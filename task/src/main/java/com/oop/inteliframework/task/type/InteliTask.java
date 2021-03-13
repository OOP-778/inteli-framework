package com.oop.inteliframework.task.type;

import com.oop.inteliframework.task.api.Task;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class InteliTask implements Task<InteliTask> {

  private final int taskId = ThreadLocalRandom.current().nextInt(1000);
  @Getter(AccessLevel.NONE)
  private final AtomicInteger counter = new AtomicInteger(-1);
  @Getter(AccessLevel.NONE)
  private final AbstractTaskController taskController;
  @Setter(AccessLevel.NONE)
  private boolean cancelled = false;
  private int runTimes = -1;
  private long delay = -1;
  private @NonNull Consumer<InteliTask> body;
  private @Nullable Predicate<InteliTask> stopIf;
  private @Nullable Consumer<InteliTask> afterComplete;
  private boolean sync = false;
  private boolean repeatable = false;

  public InteliTask(AbstractTaskController taskController) {
    this.taskController = taskController;
  }

  @Override
  public InteliTask runTimes(int runTimes) {
    this.runTimes = runTimes;
    return this;
  }

  @Override
  public InteliTask run() {
    taskController.runTask(this);
    return this;
  }

  @Override
  public InteliTask cancel() {
    if (afterComplete != null) {
      afterComplete.accept(this);
    }

    this.cancelled = true;
    taskController.cancelTask(taskId);
    return this;
  }

  @Override
  public InteliTask delay(TimeUnit unit, long delay) {
    this.delay = unit.toMillis(delay);
    return this;
  }

  @Override
  public int incAndGetTimes() {
    return counter.incrementAndGet();
  }

  @Override
  public Runnable runnable() {
    return () -> {
      if (cancelled()) {
        return;
      }

      if (stopIf() != null) {
        if (stopIf().test(this)) {
          cancel();
          return;
        }
      }

      if (runTimes() > 0) {
        int currentTimes = incAndGetTimes();
        if (currentTimes == runTimes) {
          cancel();
          return;
        }
      }

      try {
        body().accept(this);

        if (!repeatable) {
          if (afterComplete != null)
            afterComplete.accept(this);
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    };
  }
}