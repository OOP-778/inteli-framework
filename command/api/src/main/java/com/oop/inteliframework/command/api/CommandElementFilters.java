package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;

import java.util.Map;
import java.util.function.BiPredicate;

/**
 * Executor filters
 */
public interface CommandElementFilters<T extends CommandElement<T>> {

    /**
     * Clear all the filters
     */
    CommandElementFilters<T> clear();

    /**
     * Add a filter
     *
     * @param filterName the name of the filter ex. permission filter
     * @param filter     your filter
     */
    CommandElementFilters<T> add(String filterName, BiPredicate<ExecutorWrapper, CommandData> filter);

    /**
     * Remove an filter by name
     *
     * @param filterName the name of the filter
     */
    CommandElementFilters<T> remove(String filterName);

    /**
     * Get immutable map of filters
     *
     * @return filters
     */
    Map<String, BiPredicate<ExecutorWrapper, CommandData>> values();

    /**
     * Go back to element
     *
     * @return parent element
     */
    T element();
}
