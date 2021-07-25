package com.oop.inteliframework.command.bungee.requirement.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.Getter;

@Getter
public class CommandPermissionNotMetError extends CommandError {

    private final String permission;

    public CommandPermissionNotMetError(CommandParseHistory history, CommandElement elementCaused, String permission) {
        super(history, elementCaused);
        this.permission = permission;
    }
}
