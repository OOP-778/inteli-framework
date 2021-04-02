package com.oop.inteliframework.command.api;

import com.oop.inteliframework.command.api.requirement.CommandElementRequirements;
import lombok.NonNull;

import java.util.Optional;

/**
 * Command Element common methods for all elements
 *
 * @param <P> The type implementing this element
 */
public interface CommandElement<P extends CommandElement<P>> {
  /** The name of the component */
  P labeled(@NonNull String label);

  /** Get label of the element */
  String labeled();

  /**
   * Set if the element is enabled or not
   *
   * @param enabled if the command should be enabled
   */
  P enabled(boolean enabled);

  /** Called on tab complete on this element */
  P tabComplete(TabComplete<P> tabComplete);

  /** Get tab complete */
  Optional<TabComplete<P>> tabComplete();

  /** Get instance of the requirements */
  CommandElementRequirements<P> requirements();
}
