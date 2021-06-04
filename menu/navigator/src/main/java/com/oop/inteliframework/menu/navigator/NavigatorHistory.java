package com.oop.inteliframework.menu.navigator;

import com.oop.inteliframework.menu.navigator.props.GlobalNavigatorProps;
import com.oop.inteliframework.menu.navigator.props.LocalNavigatorProps;
import com.oop.inteliframework.menu.navigator.route.NavigatorRoute;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class NavigatorHistory {

  private NavigatorComponent navigator;
  private NavigatorRoute currentPath;
  private LocalNavigatorProps localProps;
  private NavigatorRoute previous;
  @NonNull private GlobalNavigatorProps globalProps;
}
