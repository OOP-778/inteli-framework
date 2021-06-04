package com.oop.inteliframework.menu.navigator.route.match;

import com.oop.inteliframework.menu.navigator.route.RouteEnterFallback;
import lombok.NonNull;

import java.util.regex.Pattern;

/** Interface used for matching routes */
public interface RouteMatcher {
  static LiteralRouteMatcher ofLiteral(@NonNull String match) {
    return new LiteralRouteMatcher(match);
  }

  static RegexRouteMatcher ofRegex(@NonNull Pattern pattern) {
    return new RegexRouteMatcher(pattern);
  }

  RouteEnterFallback matches(String path);
}
