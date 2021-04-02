package com.oop.inteliframework.command.api.requirement;

import com.oop.inteliframework.command.api.CommandElement;

import java.util.Map;

/** Executor filters */
public interface CommandElementRequirements<T extends CommandElement<T>> {

  /** Clear all the filters */
  CommandElementRequirements<T> clear();

  /**
   * Add a filter
   *
   * @param filterName the name of the filter ex. permission filter
   * @param filter your filter
   */
  CommandElementRequirements<T> add(String filterName, CommandElementRequirement filter);

  /**
   * Remove an filter by name
   *
   * @param filterName the name of the filter
   */
  CommandElementRequirements<T> remove(String filterName);

  /**
   * Get immutable map of filters
   *
   * @return filters
   */
  Map<String, CommandElementRequirement> values();

  /**
   * Go back to element
   *
   * @return parent element
   */
  T element();
}
