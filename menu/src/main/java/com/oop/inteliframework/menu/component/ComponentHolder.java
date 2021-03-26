package com.oop.inteliframework.menu.component;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.commons.util.InteliOptional;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ComponentHolder<T> {

  Map<Class, Component> getComponentMap();

  default Supplier<Map<Class, Component>> mapSupplier() {
    return HashMap::new;
  }

  default <C extends Component<C>> InteliOptional<C> getComponent(Class<C> clazz) {
    return InteliOptional.ofNullable((C) getComponentMap().get(clazz));
  }

  default <C extends Component> T applyComponent(Class<C> clazz, Consumer<C> consumer) {
    Preconditions.checkArgument(
        !clazz.isInterface(),
        "Required implementation yet got interface. " + clazz.getSimpleName());
    consumer.accept(
        (C)
            getComponentMap()
                .computeIfAbsent(
                    clazz,
                    c -> {
                      try {
                        Constructor<C> constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);

                        C object = constructor.newInstance();
                        object.onAdd(this);
                        return object;
                      } catch (Exception exception) {
                        if (exception instanceof NoSuchMethodException) {
                          throw new IllegalStateException(
                              "Failed to get component of class: "
                                  + clazz
                                  + ", cause it does not have empty constructor!");
                        } else {
                          throw new IllegalStateException(
                              "Failed to get component of class " + clazz + ".", exception);
                        }
                      }
                    }));
    return (T) this;
  }

  default Map<Class, Component> cloneComponents() {
    Map<Class, Component> map = mapSupplier().get();
    getComponentMap().forEach((key, value) -> map.put(key, value.clone()));
    return map;
  }
}
