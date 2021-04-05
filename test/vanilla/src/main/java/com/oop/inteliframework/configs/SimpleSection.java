package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.property.Configurable;
import com.oop.inteliframework.config.property.annotations.Comment;
import com.oop.inteliframework.config.property.annotations.Named;
import com.oop.inteliframework.config.property.annotations.NodeKey;
import com.oop.inteliframework.config.property.property.PrimitiveProperty;
import lombok.ToString;

@Comment({"Hello! I'm gae"})
@ToString
public class SimpleSection implements Configurable {

  @NodeKey()
  private final PrimitiveProperty.Mutable<String> name =
      PrimitiveProperty.Mutable.fromString("Hello!");

  @Named("item")
  private final PrimitiveProperty.Mutable<String> smth =
      PrimitiveProperty.Mutable.fromString("Hell1" + Math.random());

  public SimpleSection() {}

  public SimpleSection(String name) {
    this.name.set(name);
  }
}
