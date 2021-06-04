package com.oop.inteliframework.menu.navigator.route.match;

import com.oop.inteliframework.menu.navigator.NavigatorHistory;
import com.oop.inteliframework.menu.navigator.route.RouteEnterFallback;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ToString
@RequiredArgsConstructor
public class RegexRouteMatcher implements RouteMatcher {

  @NonNull private final Pattern pattern;

  @Setter
  @Accessors(chain = true, fluent = true)
  private BiConsumer<Matcher, NavigatorHistory> onEnter;

  @Override
  public RouteEnterFallback matches(String path) {
    Matcher matcher = pattern.matcher(path);
    boolean matches = matcher.matches();
    if (!matches) return null;

    return history -> {
      if (onEnter != null) {
        onEnter.accept(matcher, history);
      }
    };
  }
}
