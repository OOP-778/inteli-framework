package com.oop.inteliframework.config.node.api.iterator;

/** Used to list nodes */
public enum NodeIterator {

  /** Only list nodes that implement ParentNode */
  PARENT,

  /** Only list nodes that implement ValueNode */
  VALUE,

  /** List all available nodes */
  ALL,

  /** List all available nodes with children */
  HIERARCHY
}
