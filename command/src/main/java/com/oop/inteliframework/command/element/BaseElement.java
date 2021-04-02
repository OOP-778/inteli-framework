package com.oop.inteliframework.command.element;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.TabComplete;
import com.oop.inteliframework.command.api.requirement.CommandElementRequirement;
import com.oop.inteliframework.command.api.requirement.CommandElementRequirements;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Accessors(fluent = true)
@ToString
@EqualsAndHashCode
public abstract class BaseElement<O extends BaseElement<O>> implements CommandElement<O> {
  @Getter
  private final BaseCommandElementRequirements filters =
      new BaseCommandElementRequirements((O) this);
  @Getter private @NonNull String labeled;
  @Getter private boolean enabled = true;
  private TabComplete<O> tabComplete;

  @Override
  public O labeled(@NonNull String label) {
    this.labeled = label;
    return (O) this;
  }

  @Override
  public O enabled(boolean enabled) {
    this.enabled = enabled;
    return (O) this;
  }

  @Override
  public O tabComplete(TabComplete<O> tabComplete) {
    this.tabComplete = tabComplete;
    return (O) this;
  }

  @Override
  public Optional<TabComplete<O>> tabComplete() {
    return Optional.ofNullable(tabComplete);
  }

  @Override
  public BaseCommandElementRequirements requirements() {
    return filters;
  }

  public class BaseCommandElementRequirements implements CommandElementRequirements<O> {
    private final Map<String, CommandElementRequirement> filters =
        new TreeMap<>(String::compareToIgnoreCase);
    private final O element;

    public BaseCommandElementRequirements(O element) {
      this.element = element;
    }

    @Override
    public CommandElementRequirements<O> clear() {
      filters.clear();
      return this;
    }

    @Override
    public CommandElementRequirements<O> add(String filterName, CommandElementRequirement filter) {
      filters.put(filterName, filter);
      return this;
    }

    @Override
    public CommandElementRequirements<O> remove(String filterName) {
      filters.remove(filterName);
      return this;
    }

    @Override
    public Map<String, CommandElementRequirement> values() {
      return filters;
    }

    @Override
    public O element() {
      return element;
    }
  }
}
