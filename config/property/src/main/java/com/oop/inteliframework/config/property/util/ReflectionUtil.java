package com.oop.inteliframework.config.property.util;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.property.annotations.Comment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {
  public static InteliPair<List<String>, Boolean> getComments(Class clazz) {
    Comment commentAnnotation = (Comment) clazz.getAnnotation(Comment.class);
    if (commentAnnotation == null) {
      return new InteliPair<>(new ArrayList<>(), false);
    }

    return new InteliPair<>(Arrays.asList(commentAnnotation.value()), commentAnnotation.override());
  }
}
