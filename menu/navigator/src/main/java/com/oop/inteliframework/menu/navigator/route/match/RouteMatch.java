package com.oop.inteliframework.menu.navigator.route.match;

import com.oop.inteliframework.menu.navigator.route.NavigatorRoute;
import com.oop.inteliframework.menu.navigator.route.RouteEnterFallback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class RouteMatch {

  private final NavigatorRoute route;
  @Nullable private final RouteEnterFallback fallback;
}
