package com.oop.inteliframework.commons.util;

import java.util.*;

public class CollectionHelper {
  public static <T extends Collection<V>, V> T addAndReturn(T to, V... what) {
    to.addAll(Arrays.asList(what));
    return to;
  }

  public static <T> Collection<T> copyAndAdd(Collection<T> to, T... what) {
    List<T> copy = new ArrayList<>(to);
    copy.addAll(Arrays.asList(what));

    return copy;
  }

  public static <T> T[] joinArrays(T[]... arrays) {
    List<T> list = new LinkedList<>();
    for (T[] array : arrays) list.addAll(Arrays.asList(array));

    return (T[]) list.toArray();
  }

  public static Map<String, String> mapFromArray(Object... objects) {
    if (objects.length % 2 != 0)
      throw new IllegalStateException(
          "Failed to convert objects to map, because the size is not even!");

    Map<String, String> map = new HashMap<>();

    int len = objects.length;
    int i = 0;

    do {
      Object key = Objects.requireNonNull(objects[i++], "Key cannot be null!");
      Object value = Objects.requireNonNull(objects[i++], "Value cannot be null!");
      map.put(key.toString(), value.toString());
    } while (i != len);

    return map;
  }
}
