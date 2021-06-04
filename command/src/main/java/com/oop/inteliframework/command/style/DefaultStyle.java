package com.oop.inteliframework.command.style;

import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.ParentableElement;
import com.oop.inteliframework.command.api.TabComplete;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.error.*;
import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oop.inteliframework.command.registry.parser.ParserHelper.checkRequirements;

public class DefaultStyle implements RegistryStyle {
  @Override
  public void handleError(
      @NonNull CommandError[] errors,
      @NonNull CommandRegistry registry,
      @NonNull CommandParseHistory history) {

    ExecutorWrapper executor = history.getExecutor();

    CommandError error = errors[0];
    if (error instanceof WrongArgumentsError) {
      executor.sendMessage(
          "Invalid Arguments for command: {}. Unknown argument for {}",
          buildUserFriendlyPath(history.getPath()),
          history.getWaitingForParse().poll());
      return;
    }

    if (error instanceof TooMuchArgumentsError) {
      executor.sendMessage(
          "Too much arguments for command: {}. Unknown arguments {}",
          buildUserFriendlyPath(history.getPath()),
          Arrays.toString(history.getWaitingForParse().toArray()));
      return;
    }

    if (error instanceof InvalidArgumentError) {
      executor.sendMessage(
          "Error while parsing argument for command: {} for argument `{}` message: {}",
          buildUserFriendlyPath(history.getPath()),
          history.getWaitingForParse().poll(),
          ((InvalidArgumentError) error).getResult().getMessage());
      return;
    }

    if (error instanceof MissingArgumentsError) {
      executor.sendMessage(
          "Missing arguments for command: {}, available arguments: {}",
          buildUserFriendlyPath(history.getPath()),
          buildAvailableArgs(history, history.getLastElement()));
      return;
    }

    sendHelpMessage(executor, history);
  }

  private String buildAvailableArgs(CommandParseHistory history, CommandElement<?> lastElement) {
    if (lastElement instanceof ParentableElement) {
      return ((ParentableElement<?>) lastElement)
          .children().values().stream()
              .flatMap(
                  c -> {
                    if (checkRequirements(lastElement, history) != null) return Stream.empty();

                    List<String> possible = new ArrayList<>();
                    if (c instanceof Command) possible.addAll(((Command) c).aliases());

                    if (c instanceof Argument) {
                      if (c.tabComplete().isPresent()) {
                        possible.addAll(((TabComplete) c.tabComplete().get()).complete(c, history));

                      } else possible.add("<" + c.labeled() + ">");

                    } else if (c instanceof NoValueArgument) {
                      if (!history.getData().hasKey(c.labeled())) {
                        history.getData().add(c.labeled(), true);
                        possible.add(c.labeled());
                      }
                    } else possible.add(c.labeled());

                    return possible.stream();
                  })
              .distinct()
              .limit(10)
              .collect(Collectors.joining(", "));
    }

    return lastElement.labeled();
  }

  public void sendHelpMessage(ExecutorWrapper executor, CommandParseHistory history) {
    executor.sendMessage(history.getLastElement().labeled());
  }

  public String buildUserFriendlyPath(Set<CommandElement> elementSet) {
    List<String> path = new LinkedList<>();
    for (CommandElement<?> commandElement : elementSet) {
      if (commandElement instanceof Argument) {
        path.add("<" + commandElement.labeled() + ">");
        continue;
      }

      path.add(commandElement.labeled());
    }

    return String.join(" ", path);
  }
}
