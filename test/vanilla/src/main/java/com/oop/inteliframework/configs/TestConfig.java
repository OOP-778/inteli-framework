package com.oop.inteliframework.configs;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.property.MapProperty;
import lombok.ToString;

import java.util.TreeMap;

@Comment(
    value = {"Test Comment"},
    override = true)
@ToString
public class TestConfig implements Configurable {

  @Named("testMap")
  private final MapProperty<String, SimpleSection, TreeMap<String, SimpleSection>> testMap = MapProperty.from(
          new TreeMap<>(), String.class, SimpleSection.class, new InteliPair<>("get", new SimpleSection())
  );
}
