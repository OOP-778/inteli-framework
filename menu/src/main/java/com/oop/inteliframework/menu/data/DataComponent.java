package com.oop.inteliframework.menu.data;

import com.google.common.collect.Maps;
import com.oop.inteliframework.menu.component.Component;

import java.util.Map;
import java.util.Optional;

public class DataComponent implements Component<DataComponent> {

  private final Map<String, Object> data = Maps.newHashMap();

  @Override
  public DataComponent clone() {
    DataComponent component = new DataComponent();
    component.data.putAll(data);
    return component;
  }

  public void add(String key, Object object) {
    data.put(key, object);
  }

  public <T> Optional<T> get(String key) {
    return Optional.ofNullable((T) data.get(key));
  }

  public <T> Optional<T> get(String key, Class<T> clazz) {
    return get(key);
  }

  public boolean has(String key) {
    return data.containsKey(key);
  }
}
