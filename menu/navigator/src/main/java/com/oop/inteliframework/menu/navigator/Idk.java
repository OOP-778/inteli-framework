package com.oop.inteliframework.menu.navigator;

import com.oop.inteliframework.menu.navigator.route.match.RouteMatcher;

import java.util.regex.Pattern;

public class Idk {
  public static void main(String[] args) {
    NavigatorComponent component = new NavigatorComponent(new NavigatorHistory());
    component
        .routes()
        .fallBackPath(
            (path, navigator) -> {
              navigator.viewer().sendMessage("Unknown menu path, falling back to /");
              return navigator
                  .routes()
                  .matchRoute("/")
                  .orElseThrow(() -> new IllegalStateException("There's no default route!"));
            })
        .route(RouteMatcher.ofLiteral("/"), null)
        .route(
            RouteMatcher.ofRegex(Pattern.compile("enchants.([a-z]+)"))
                .onEnter(
                    (matcher, history) -> {
                      history.getLocalProps().getProperties().put("enchant", matcher.group(1));
                    }),
            null);
  }
}
