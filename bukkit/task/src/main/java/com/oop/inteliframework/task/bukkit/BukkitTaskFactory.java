package com.oop.inteliframework.task.bukkit;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.InteliTask;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@UtilityClass
public class BukkitTaskFactory {
  private static final BukkitTaskController BUKKIT_TASK_CONTROLLER =
      InteliPlatform.getInstance()
          .safeModuleByClass(InteliTaskFactory.class)
          .controllerByClass(BukkitTaskController.class)
          .get();

  public @NonNull InteliTask timer(
      final @NonNull Consumer<InteliTask> body,
      boolean sync,
      final long delay,
      final @NonNull TimeUnit unit) {
    return new InteliBukkitTask(BUKKIT_TASK_CONTROLLER)
        .sync(sync)
        .repeatable(true)
        .body(body)
        .delay(unit, delay);
  }

  public @NonNull InteliTask timer(
      final @NonNull Consumer<InteliTask> body, boolean sync, final long delay) {
    return timer(body, sync, delay, TimeUnit.MILLISECONDS);
  }

  public @NonNull InteliTask later(
      final @NonNull Consumer<InteliTask> body,
      boolean sync,
      final long delay,
      final @NonNull TimeUnit unit) {
    return new InteliBukkitTask(BUKKIT_TASK_CONTROLLER).sync(sync).body(body).delay(unit, delay);
  }

  public @NonNull InteliTask later(
      final @NonNull Consumer<InteliTask> body, boolean sync, final long delay) {
    return later(body, sync, delay, TimeUnit.MILLISECONDS);
  }

  public void enforceSync(Runnable runnable) {
    if (Bukkit.isPrimaryThread()) {
      runnable.run();
      return;
    }

    new InteliBukkitTask(BUKKIT_TASK_CONTROLLER).sync(true).body($ -> runnable.run()).run();
  }
}
