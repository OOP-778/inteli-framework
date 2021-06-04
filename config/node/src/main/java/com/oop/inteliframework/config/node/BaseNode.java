package com.oop.inteliframework.config.node;

import com.oop.inteliframework.config.node.api.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/** The base node implementation */
@Accessors(fluent = true)
@EqualsAndHashCode
public abstract class BaseNode implements Node {

  @Getter private final List<String> comments = new LinkedList<>();

  @Override
  public void appendComments(String... comments) {
    this.comments.addAll(Arrays.asList(comments));
  }
}
