package com.oop.inteliframework.menu.navigator.props;

import com.oop.inteliframework.menu.navigator.NavigatorComponent;

public class LocalNavigatorProps extends NavigatorProps {
  @Override
  public void onPathExit(NavigatorComponent navigatorComponent) {
    navigatorComponent.history().getLocalProps().properties.clear();
  }

  @Override
  public void onPathEnter(NavigatorComponent navigatorComponent) {
    navigatorComponent.history().getLocalProps().properties.clear();
  }
}
