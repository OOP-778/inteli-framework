package com.oop.inteliframework.command.registry.parser;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.ParentableElement;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.error.InvalidArgumentError;
import com.oop.inteliframework.command.error.TooMuchArgumentsError;
import com.oop.inteliframework.command.error.WrongArgumentsError;
import lombok.NonNull;

import java.util.*;

import static com.oop.inteliframework.command.registry.parser.ParserHelper.checkRequirements;

public class CommandParser {
  private static final Map<Class<?>, ElementParser> parserMap = new HashMap<>();

  static {
    registerParser(
        Command.class,
        (element, parent, peek, history) -> {
          List<String> possibleLabels = new ArrayList<>();
          possibleLabels.add(element.labeled());
          possibleLabels.addAll(element.aliases());

          // Try to match possible labels with the label
          if (possibleLabels.stream().anyMatch(peek::equalsIgnoreCase)) {
            history.getWaitingForParse().poll();
            CommandError commandError = checkRequirements(element, history);
            if (commandError != null) {
              history.getResultedInto().add(commandError);
              return null;
            }
            return element;
          }

          return null;
        });

    registerParser(
        Argument.class,
        (element, parent, peek, history) -> {
          CommandError commandError = checkRequirements(element, history);
          if (commandError != null) {
            history.getResultedInto().add(commandError);
            return null;
          }

          ParseResult<?> parseResult =
              ((Argument<?>) element).parser().parse(history.getWaitingForParse());

          if (parseResult.getMessage() != null) {
            if (((Argument<?>) element).optional()) return null;

            history.getResultedInto().add(new InvalidArgumentError(element, history, parseResult));
            return null;
          }

          Object parsedObject = parseResult.getObject();
          history.getData().add(element.labeled(), parsedObject);

          return element;
        });

    registerParser(
        NoValueArgument.class,
        (element, parent, peek, history) -> {
          if (!peek.equalsIgnoreCase(element.labeled())) return null;

          CommandError commandError = checkRequirements(element, history);
          if (commandError != null) {
            history.getResultedInto().add(commandError);
            return null;
          }

          if (history.getData().hasKey(element.labeled())) return null;

          history.getWaitingForParse().poll();
          history.getData().add(element.labeled(), true);
          return parent;
        });
  }

  public static <T extends CommandElement> void registerParser(
      Class<T> clazz, ElementParser<T> parser) {
    parserMap.put(clazz, parser);
  }

  public static CommandParseHistory parse(
      @NonNull ExecutorWrapper executor,
      @NonNull CommandElement currentElement,
      @NonNull CommandData commandData,
      @NonNull Queue<String> waitingParsing,
      boolean returnOnErrors) {
    CommandParseHistory history =
        new CommandParseHistory(
            commandData,
            executor,
            waitingParsing,
            new LinkedList<>(),
            new LinkedHashSet<>(Collections.singletonList(currentElement)),
            currentElement);

    _parse(history, returnOnErrors);
    return history;
  }

  private static void _parse(CommandParseHistory history, boolean returnOnErrors) {
    if (history.getWaitingForParse().isEmpty()) {
      return;
    }

    if (history.getLastElement() instanceof ParentableElement) {
      if (((ParentableElement) history.getLastElement()).children().isEmpty()) {
        history.getResultedInto().add(new TooMuchArgumentsError(history, history.getLastElement()));
        return;
      }

      String label = history.getWaitingForParse().peek();
      for (CommandElement<?> element :
          ((ParentableElement<?>) history.getLastElement()).children().values()) {

        ElementParser elementParser =
            parserMap.keySet().stream()
                .filter(clazz -> clazz.isAssignableFrom(element.getClass()))
                .findFirst()
                .map(parserMap::get)
                .orElseThrow(
                    () ->
                        new IllegalStateException(
                            "Failed to find element parser for " + element.getClass()));

        CommandElement<?> parse =
            elementParser.parse(element, history.getLastElement(), label, history);

        if (parse != null) {
          history.setLastElement(parse);
          history.getPath().add(parse);
          _parse(history, returnOnErrors);
          return;

        } else if (!history.getResultedInto().isEmpty() && returnOnErrors) {
          return;
        } else history.getResultedInto().clear();
      }
    }

    if (!history.getWaitingForParse().isEmpty()) {
      history.getResultedInto().add(new WrongArgumentsError(history, history.getLastElement()));
    }
  }
}
