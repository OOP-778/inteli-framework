package com.oop.inteliframework.menu.navigator.route;

import com.oop.inteliframework.menu.navigator.props.LocalNavigatorProps;

public abstract class NavigatorRoute {

  // The state of the current open
  private boolean currentlyOpened = false;

  // Local navigator properties
  private LocalNavigatorProps localProps;
}
