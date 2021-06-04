package com.oop.inteliframework.commons.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InteliTriPair<T, S, C> {
  private T first;
  private S second;
  private C third;
}
