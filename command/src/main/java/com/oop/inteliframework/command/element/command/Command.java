package com.oop.inteliframework.command.element.command;

import com.oop.inteliframework.command.element.ParentableElement;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.*;

/**
 * The command class used to handle the command / subcommands
 */
@Getter
@Accessors(fluent = true)
public class Command extends ParentableElement<Command> {
    private CommandExecution executer;

    private Set<String> aliases = new HashSet<>();

    public Command onExecute(CommandExecution exec) {
        this.executer = exec;
        return this;
    }

    public Command addAlias(String ...alias) {
        aliases.addAll(Arrays.asList(alias));
        return this;
    }
}
