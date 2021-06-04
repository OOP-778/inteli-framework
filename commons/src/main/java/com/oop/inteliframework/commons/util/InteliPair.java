package com.oop.inteliframework.commons.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InteliPair<K, V> {

  private K key;
  private V value;
}
