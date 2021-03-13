package com.oop.inteliframework.task.api;

import com.oop.inteliframework.commons.util.InteliOptional;
import com.oop.inteliframework.task.InteliTaskFactory;
import com.oop.inteliframework.task.type.InteliTask;
import java.util.List;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manipulation with task controllers.
 * Here you can register new controller.
 *
 * @see InteliTaskFactory
 * @see InteliTask
 * */
public interface TaskFactory<T extends TaskFactory, R extends Task> {

  /**
   * Register new task controller
   *
   * @param controller Task controller
   * */
  default <E extends TaskController> T registerNewController(
      @NonNull TaskController<E, R> controller) {
    registeredControllers().add(controller);
    return (T) this;
  }

  /**
   * Getting task controller.
   *
   * @param controllerClass Class which implements {@link TaskController}
   * */
  @Nullable
  default <E extends TaskController> InteliOptional<E> controllerByClass(
      @NonNull Class<E> controllerClass) {
    return InteliOptional.ofNullable((E)
        registeredControllers()
            .stream()
            .filter(e -> controllerClass.isAssignableFrom(e.getClass()))
            .findFirst()
            .orElse(null));
  }

  /**
   * @return All registered task controllers.
   * */
  @NonNull List<TaskController<?, R>> registeredControllers();

}
