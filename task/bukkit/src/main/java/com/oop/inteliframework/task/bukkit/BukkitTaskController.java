package com.oop.inteliframework.task.bukkit;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.task.api.TaskController;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitTaskController implements TaskController<BukkitTaskController, InteliBukkitTask> {
    private final Map<Long, InteliBukkitTask> tasks = new ConcurrentHashMap<>();
    private JavaPlugin starter = (JavaPlugin) InteliPlatform.getInstance().starter();

    @Override
    public BukkitTaskController runTask(InteliBukkitTask taskProvider) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BukkitTask bukkitTask;

        if (taskProvider.sync()) {
            if (taskProvider.repeatable()) {
               bukkitTask = scheduler.runTaskTimer(
                        starter,
                        taskProvider.runnable(),
                        taskProvider.delay(),
                        taskProvider.delay()
                );
            } else if (taskProvider.delay() != -1) {
                bukkitTask = scheduler.runTaskLater(
                        starter,
                        taskProvider.runnable(),
                        taskProvider.delay()
                );
            } else
                bukkitTask = scheduler.runTask(starter, taskProvider.runnable());
        } else {
            if (taskProvider.repeatable()) {
                bukkitTask = scheduler.runTaskTimerAsynchronously(
                        starter,
                        taskProvider.runnable(),
                        taskProvider.delay(),
                        taskProvider.delay()
                );
            } else if (taskProvider.delay() != -1) {
                bukkitTask = scheduler.runTaskLaterAsynchronously(
                        starter,
                        taskProvider.runnable(),
                        taskProvider.delay()
                );
            } else
                bukkitTask = scheduler.runTaskAsynchronously(starter, taskProvider.runnable());
        }

        taskProvider.taskId(bukkitTask.getTaskId());
        tasks.put(taskProvider.taskId(), taskProvider);
        return this;
    }

    @Override
    public BukkitTaskController cancelTask(long taskId) {
        tasks.remove(taskId);
        Bukkit.getScheduler().cancelTask((int) taskId);
        return this;
    }

    @Override
    public InteliOptional<InteliBukkitTask> taskById(long taskId) {
        return InteliOptional.ofNullable(tasks.get(taskId));
    }

    @Override
    @NonNull
    public Map<Long, InteliBukkitTask> runningTasks() {
        return tasks;
    }
}