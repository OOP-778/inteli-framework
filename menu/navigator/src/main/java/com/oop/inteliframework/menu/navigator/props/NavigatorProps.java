package com.oop.inteliframework.menu.navigator.props;

import com.oop.inteliframework.menu.navigator.NavigatorComponent;
import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public abstract class NavigatorProps {

  protected final Map<String, Object> properties = new TreeMap<>();

  public abstract void onPathExit(NavigatorComponent navigatorComponent);

  public abstract void onPathEnter(NavigatorComponent navigatorComponent);
}
