package com.oop.inteliframework.menu.navigator.route.match;

import com.oop.inteliframework.menu.navigator.route.RouteEnterFallback;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@ToString
public class LiteralRouteMatcher implements RouteMatcher {

  @NonNull private final String match;

  @Override
  public RouteEnterFallback matches(String path) {
    if (!StringUtils.equals(path, match)) return null;

    return history -> {};
  }
}
