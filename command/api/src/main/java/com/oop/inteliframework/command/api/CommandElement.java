package com.oop.inteliframework.command.api;

import lombok.NonNull;

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
    CommandElementFilters<P> filters();

}

