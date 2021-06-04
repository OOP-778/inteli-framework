package com.oop.inteliframework.config.property.util;

import com.oop.inteliframework.commons.util.InteliPair;

import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {
  public static InteliPair<List<String>, Boolean> getComments(Class clazz) {
    return new InteliPair<>(new ArrayList<>(), true);
  }
}
