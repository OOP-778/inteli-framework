package com.oop.inteliframework.command.element;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import lombok.NonNull;

import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Command Element common methods for all elements
 * @param <P> The type implementing this element
 */
public interface CommandElement<P extends CommandElement<P>> {
    /**
     * The name of the component
     */
    P labeled(@NonNull String label);

    /**
     * Get label of the element
     */
    String labeled();

    /**
     * Set if the element is enabled or not
     * @param enabled if the command should be enabled
     */
    P enabled(boolean enabled);

    /**
     * Called on tab complete on this element
     */
    P tabComplete(TabComplete<P> tabComplete);

    /**
     * Get instance of the this element executor filters
     */
    Filters<P> filters();

    /**
     * Executor filters
     */
    interface Filters<T extends CommandElement<T>> {

        /**
         * Clear all the filters
         */
        Filters<T> clear();

        /**
         * Add a filter
         * @param filterName the name of the filter ex. permission filter
         * @param filter your filter
         */
        Filters<T> add(String filterName, BiPredicate<ExecutorWrapper, CommandData> filter);

        /**
         * Remove an filter by name
         * @param filterName the name of the filter
         */
        Filters<T> remove(String filterName);

        /**
         * Get immutable map of filters
         * @return filters
         */
        Map<String, BiPredicate<ExecutorWrapper, CommandData>> values();

        /**
         * Go back to element
         * @return parent element
         */
        T element();
    }
}

