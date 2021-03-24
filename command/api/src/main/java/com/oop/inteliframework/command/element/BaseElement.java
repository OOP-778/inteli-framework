package com.oop.inteliframework.command.element;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.TabComplete;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiPredicate;

@Getter
@Accessors(fluent = true)
public abstract class BaseElement<O extends BaseElement<O>> implements CommandElement<O> {
  private @NonNull String labeled;

  private boolean enabled = true;

  private TabComplete<O> tabComplete;

  private final CommandElementFilters filters = new CommandElementFilters((O) this);

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
  public CommandElementFilters filters() {
    return filters;
  }

  public class CommandElementFilters implements com.oop.inteliframework.command.api.CommandElementFilters<O> {

    private final Map<String, BiPredicate<ExecutorWrapper, CommandData>> filters = new TreeMap<>(String::compareToIgnoreCase);
    private O element;

    public CommandElementFilters(O element) {
      this.element = element;
    }

    @Override
    public com.oop.inteliframework.command.api.CommandElementFilters<O> clear() {
      filters.clear();
      return this;
    }

    @Override
    public com.oop.inteliframework.command.api.CommandElementFilters<O> add(String filterName, BiPredicate<ExecutorWrapper, CommandData> filter) {
      filters.put(filterName, filter);
      return this;
    }

    @Override
    public com.oop.inteliframework.command.api.CommandElementFilters<O> remove(String filterName) {
      filters.remove(filterName);
      return this;
    }

    @Override
    public Map<String, BiPredicate<ExecutorWrapper, CommandData>> values() {
      return filters;
    }

    @Override
    public O element() {
      return element;
    }
  }
}
