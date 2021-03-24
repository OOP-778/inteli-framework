package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.util.ReflectionUtil;
import com.oop.inteliframework.config.property.util.Serializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Getter
public class AssociatedConfig<T extends Configurable> extends PlainConfig {

  protected Constructor<T> constructor;
  protected Class<T> clazz;
  private T object;

  public AssociatedConfig(@NonNull File file, Class<T> clazz) {
    super(file);
    this.clazz = clazz;

    load();
  }

  protected T newObject() {
    try {
      if (constructor == null) {
        this.constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
      }

      return constructor.newInstance();
    } catch (Throwable throwable) {
      throw new IllegalStateException(
          format("Failed to find empty constructor for {} class", clazz));
    }
  }

  @Override
  public void load() {
    super.load();

    T object = newObject();
    InteliPair<List<String>, Boolean> comments = ReflectionUtil.getComments(clazz);

    boolean putComments = comments().isEmpty();
    if (!comments().isEmpty() && comments.getValue()) putComments = true;

    if (putComments) {
      comments().clear();
      comments().addAll(comments.getKey());
    }

    object.onPreload(this);
    // TODO: Loading...
  }

  public void sync() {
    nodes.clear();
    SerializedProperty property = Serializer.serializerForConfigurable(
            clazz,
            false
    ).apply(object);

    ParentNode nodes = property.getNode().asParent();
    this.nodes.putAll(((BaseParentNode) nodes).nodes());
  }

  @SneakyThrows
  public void save() {
    handler.save(this, file);
  }
}
