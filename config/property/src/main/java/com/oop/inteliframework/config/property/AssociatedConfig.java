package com.oop.inteliframework.config.property;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.node.api.ParentNode;
import com.oop.inteliframework.config.property.loader.Loader;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.serializer.Serializer;
import com.oop.inteliframework.config.property.util.ReflectionUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.function.Consumer;

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

  public void reload(Consumer<Throwable> onError) {
    T previousObject = this.object;
    try {
      load();
    } catch (Throwable throwable) {
      onError.accept(throwable);
      this.object = previousObject;
    }
  }

  @Override
  public void load() {
    super.load();
    InteliPair<List<String>, Boolean> comments = ReflectionUtil.getComments(clazz);

    boolean putComments = comments().isEmpty();
    if (!comments().isEmpty() && comments.getValue()) putComments = true;

    if (putComments) {
      comments().clear();
      comments().addAll(comments.getKey());
    }

    try {
      this.object = Loader.loaderFrom(clazz).apply(this);
    } catch (Throwable throwable) {
      throw new IllegalStateException("Failed to load " + file, throwable);
    }
  }

  public void sync() {
    nodes.clear();
    SerializedProperty property = Serializer.serializerForConfigurable(clazz, false).apply(object);

    ParentNode nodes = property.getNode().asParent();
    this.nodes.putAll(((BaseParentNode) nodes).nodes());
  }

  @SneakyThrows
  public void save() {
    handler.save(this, file);
  }

  public void syncAndSave() {
    sync();
    save();
  }
}
