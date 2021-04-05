package com.oop.inteliframework.config.property.property.custom;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.node.api.Node;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.property.MutableProperty;
import com.oop.inteliframework.config.property.property.Property;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.serializer.Serializer;
import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.List;

import static com.oop.inteliframework.commons.util.StringFormat.format;
import static com.oop.inteliframework.config.property.loader.Loader.loaderFrom;

public class ObjectProperty<T> implements Property<T> {
  protected Class<T> clazz;
  protected T object;

  protected ObjectProperty(Class<T> clazz, T object) {
    this.clazz = clazz;
    this.object = object;

    List<Field> clazzFields =
        InteliPlatform.getInstance()
            .safeModuleByClass(InteliPropertyModule.class)
            .getClassesCache()
            .getFields(clazz);

    // We need to check if it's configurable and find at least one property
    if (Configurable.class.isAssignableFrom(clazz)) {
      Preconditions.checkArgument(
          !clazzFields.isEmpty(),
          format("Configurable class {} doesn't contain any properties!", clazz.getSimpleName()));

    } else {
      // Find custom property handler if it's not instance of Configurable
      Preconditions.checkArgument(
          InteliPlatform.getInstance()
              .moduleByClass(InteliPropertyModule.class)
              .get()
              .handlerByClass(clazz)
              .isPresent(),
          format("Failed to find property handler for class {}!", clazz.getSimpleName()));
    }
  }

  public static <T> ObjectProperty<T> from(Class<T> clazz, T object) {
    return new ObjectProperty<>(clazz, object);
  }

  public static <T> ObjectProperty<T> from(@NonNull T object) {
    return from((Class<T>) object.getClass(), object);
  }

  @Override
  public SerializedProperty toNode() {
    if (Configurable.class.isAssignableFrom(clazz)) {
      return Serializer.serializerFor(clazz).apply(object);
    }

    PropertyHandler<T> propertyHandler =
        InteliPlatform.getInstance()
            .moduleByClass(InteliPropertyModule.class)
            .get()
            .handlerByClass((Class<T>) object.getClass())
            .get();
    return propertyHandler.toNode(object);
  }

  @Override
  public void fromNode(Node node) {
    if (Configurable.class.isAssignableFrom(clazz)) {
      this.object = loaderFrom(clazz).apply(node);
    }

    PropertyHandler<T> propertyHandler =
            InteliPlatform.getInstance()
                    .moduleByClass(InteliPropertyModule.class)
                    .get()
                    .handlerByClass((Class<T>) object.getClass())
                    .get();
    this.object = propertyHandler.fromNode(node);
  }

  @Override
  public T get() {
    return object;
  }

  @Override
  public Class<T> type() {
    return clazz;
  }

  public static class Mutable<T> extends ObjectProperty<T>
      implements MutableProperty<T, ObjectProperty<T>> {
    protected Mutable(Class<T> clazz, T object) {
      super(clazz, object);
    }

    public static <T> Mutable<T> fromEmpty(@NonNull Class<T> clazz) {
      return new Mutable<>(clazz, null);
    }

    public static <T> Mutable<T> from(@NonNull T object) {
      return new Mutable<>((Class<T>) object.getClass(), object);
    }

    @Override
    public Mutable<T> set(T object) {
      this.object = object;
      return this;
    }

    @Override
    public boolean isPresent() {
      return object != null;
    }
  }
}
