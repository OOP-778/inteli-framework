package com.oop.inteliframework.command.error;

import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.Getter;

@Getter
public class MessageCommandError extends CommandError {

    private String message;

    public MessageCommandError(CommandParseHistory history, CommandElement elementCaused, String message) {
        super(history, elementCaused);
        this.message = message;
    }
}
