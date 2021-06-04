package com.oop.inteliframework.menu.navigator.route;

import com.oop.inteliframework.menu.navigator.NavigatorHistory;

@FunctionalInterface
public interface RouteEnterFallback {
  void onEnter(NavigatorHistory history);
}
