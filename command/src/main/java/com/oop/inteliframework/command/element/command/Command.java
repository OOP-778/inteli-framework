package com.oop.inteliframework.command.element.command;

import com.oop.inteliframework.command.api.CommandExecution;
import com.oop.inteliframework.command.api.ParentableElement;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/** The command class used to handle the command / subcommands */
@Getter
@Accessors(fluent = true)
@ToString
public class Command extends ParentableElement<Command> {
  private final Set<String> aliases = new HashSet<>();
  private CommandExecution executer;

  public Command onExecute(CommandExecution exec) {
    this.executer = exec;
    return this;
  }

  public Command addAlias(String... alias) {
    aliases.addAll(Arrays.asList(alias));
    return this;
  }
}
