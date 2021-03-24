package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.property.CollectionProperty;

import java.util.ArrayList;
import java.util.List;

@Comment(
    value = {"Test Comment"},
    override = true)
public class TestConfig implements Configurable {

  @Named("testList")
  private final CollectionProperty<SimpleSection, List<SimpleSection>> testList =
      CollectionProperty.from(
          new ArrayList<>(),
          SimpleSection.class,
          new SimpleSection(),
          new SimpleSection("gay"),
          new SimpleSection("wfafafw"));
}
