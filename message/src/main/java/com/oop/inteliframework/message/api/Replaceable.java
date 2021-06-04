package com.oop.inteliframework.message.api;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.message.Replacer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface Replaceable<T> {

  T replace(Replacer replacer);

  default T replace(Consumer<Replacer> consumer) {
    Replacer replacer = new Replacer();
    consumer.accept(replacer);

    return replace(replacer);
  }

  default T replace(String key, String value) {
    return replace(replacer -> replacer.replaceLiteral(key, value));
  }

  default T replace(InteliPair<String, String>... pairs) {
    return replace(
        replacer -> {
          Map<String, String> map = new HashMap<>();
          for (InteliPair<String, String> pair : pairs) {
            map.put(pair.getKey(), pair.getValue());
          }
          replacer.replaceFromMap(map);
        });
  }
}
