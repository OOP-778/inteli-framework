package com.oop.inteliframework.menu.navigator.route;

import com.oop.inteliframework.menu.navigator.NavigatorComponent;
import com.oop.inteliframework.menu.navigator.route.match.RouteMatch;
import com.oop.inteliframework.menu.navigator.route.match.RouteMatcher;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/** A class for holding all routes */
@Accessors(chain = true, fluent = true)
public class Routes {

  private final NavigatorComponent navigator;
  private final Map<RouteMatcher, NavigatorRoute> routeMap = new HashMap<>();

  @Setter @NonNull private BiFunction<String, NavigatorComponent, RouteMatch> fallBackPath;

  public Routes(NavigatorComponent navigatorComponent) {
    this.navigator = navigatorComponent;
  }

  public Optional<RouteMatch> matchRoute(String path) {
    for (Map.Entry<RouteMatcher, NavigatorRoute> routeEntry : routeMap.entrySet()) {
      RouteEnterFallback matches = routeEntry.getKey().matches(path);
      if (matches == null) continue;

      return Optional.of(new RouteMatch(routeEntry.getValue(), matches));
    }

    return Optional.empty();
  }

  public RouteMatch getOrFallBack(String path) {
    return matchRoute(path).orElse(fallBackPath.apply(path, navigator));
  }

  public Routes route(RouteMatcher routeMatcher, NavigatorRoute route) {
    routeMap.put(routeMatcher, route);
    return this;
  }
}
