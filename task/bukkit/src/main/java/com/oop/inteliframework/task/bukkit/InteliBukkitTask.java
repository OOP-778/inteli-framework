package com.oop.inteliframework.task.bukkit;

import com.oop.inteliframework.task.type.InteliTask;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
public class InteliBukkitTask extends InteliTask {

  @Setter @Getter private boolean sync = false;

  public InteliBukkitTask(BukkitTaskController taskController) {
    super(taskController);
  }
}
