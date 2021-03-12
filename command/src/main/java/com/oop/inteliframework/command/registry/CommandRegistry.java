package com.oop.inteliframework.command.registry;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.element.CommandElement;
import com.oop.inteliframework.command.element.ParentableElement;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.commons.util.CollectionHelper;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Class that handles all the commands registered */
public class CommandRegistry {

  /** Store all the commands ignore case here */
  @Getter
  private final TreeMap<String, Command> elementTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  private static int countNonRepeatingSpaces(String input) {
    int spaces = 0;
    boolean lastWasSpace = false;
    for (char character : input.toCharArray()) {
      if (character == ' ') {
        if (lastWasSpace) continue;

        lastWasSpace = true;
        spaces++;
      } else lastWasSpace = false;
    }

    return spaces;
  }

  public void register(Command command) {
    elementTreeMap.put(command.labeled(), command);
  }

  public Optional<Command> findCommand(String identifier) {
    return elementTreeMap.values().stream()
        .filter(
            command ->
                command.labeled().equalsIgnoreCase(identifier)
                    || command.aliases().contains(identifier))
        .findFirst();
  }

  /** Command execution & tab completion section */

  // Execute our command
  public boolean execute(ExecutorWrapper executor, String input) {
    // If command contains slash, get rid of it
    input = removeSlash(input);

    Queue<String> waitingParsing = new LinkedList<>(Arrays.asList(StringUtils.split(input, ' ')));
    waitingParsing.removeIf(string -> string.trim().isEmpty());

    if (waitingParsing.isEmpty()) return false;

    String label = waitingParsing.poll();
    for (Command value : elementTreeMap.values()) {
      List<String> possibleLabels = new ArrayList<>();
      possibleLabels.add(value.labeled());
      possibleLabels.addAll(value.aliases());

      // Try to match possible labels with the label
      if (possibleLabels.stream().anyMatch(label::equalsIgnoreCase)) {
        execute(executor, waitingParsing, value, new CommandData());
        return true;
      }
    }

    return false;
  }

  protected void execute(ExecutorWrapper executorWrapper, Command command, CommandData data) {
    if (command.executer() != null) command.executer().execute(executorWrapper, data);
  }

  protected void execute(
      ExecutorWrapper executor,
      Queue<String> waitingParsing,
      CommandElement<?> currentElement,
      CommandData commandData) {
    if (waitingParsing.isEmpty()) {
      if (currentElement == null) return;

      if (currentElement instanceof Command) {
        execute(executor, (Command) currentElement, commandData);
        return;
      }

      // TODO: Help Message
      return;
    }

    // Handle filters
    Function<CommandElement<?>, Boolean> filtersCheck =
        (element) -> {
          Map<String, BiPredicate<ExecutorWrapper, CommandData>> values =
              element.filters().values();
          // TODO: No access handling
          return values.isEmpty()
              || values.values().stream().allMatch(filter -> filter.test(executor, commandData));
        };

    if (currentElement instanceof ParentableElement) {
      String label = waitingParsing.peek();
      for (CommandElement<?> element :
          ((ParentableElement<?>) currentElement).children().values()) {
        if (element instanceof Command) {
          List<String> possibleLabels = new ArrayList<>();
          possibleLabels.add(element.labeled());
          possibleLabels.addAll(((Command) element).aliases());

          // Try to match possible labels with the label
          if (possibleLabels.stream().anyMatch(label::equalsIgnoreCase)) {
            waitingParsing.poll();

            if (!filtersCheck.apply(element)) return;

            execute(executor, waitingParsing, element, commandData);
            return;
          }
        }

        if (element instanceof Argument) {
          ParseResult<?> parseResult = ((Argument<?>) element).parser().parse(waitingParsing);

          if (!filtersCheck.apply(element)) return;

          if (parseResult.getMessage() != null) {
            if (((Argument<?>) element).optional()) continue;

            // TODO: Incorrect argument handling
            return;
          }

          Object parsedObject = parseResult.getObject();
          commandData.add(element.labeled(), parsedObject);

          execute(executor, waitingParsing, element, commandData);
          return;
        }

        if (element instanceof NoValueArgument) {
          waitingParsing.poll();

          if (label.equalsIgnoreCase(element.labeled())) {
            if (!filtersCheck.apply(element)) return;

            commandData.add(element.labeled(), true);
            execute(executor, waitingParsing, currentElement, commandData);
            return;
          }
        }
      }
    }

    // TODO: Help handling
  }

  public String removeSlash(String input) {
    return input.startsWith("/") ? input.substring(1) : input;
  }

  /** Tab Complete Section */
  public <P extends ParentableElement<P>> List<String> tabComplete(ExecutorWrapper executor, String input) {
    input = removeSlash(input);

    // For commands that goes with plugin:etc
    if (StringUtils.contains(input, ":")) input = StringUtils.split(input, ":")[1];

    String[] s = StringUtils.split(input, ' ');
    if (s.length == 0) return new ArrayList<>();

    Optional<Command> lookupCommand = findCommand(s[0]);
    if (!lookupCommand.isPresent()) return new ArrayList<>();

    s = Arrays.copyOfRange(s, 1, s.length);
    CommandData commandData = new CommandData();

    int spacesCount = countNonRepeatingSpaces(input);

    // Handle filters
    Function<CommandElement<?>, Boolean> filtersCheck =
            (element) -> {
              Map<String, BiPredicate<ExecutorWrapper, CommandData>> values =
                      element.filters().values();
              // TODO: No access handling
              return values.isEmpty()
                      || values.values().stream().allMatch(filter -> filter.test(executor, commandData));
            };

    // If we're one space away from last argument
    System.out.println("args size: " + s.length + ", spaces: " + spacesCount);
    Queue<String> parseQueue;
//    if (spacesCount == s.length)
      parseQueue = new LinkedList<>(Arrays.asList(s));
//    else
//      parseQueue = new LinkedList<>(Arrays.asList(Arrays.copyOfRange(s, 0, s.length-1)));

    if (parseQueue.isEmpty()) {
      return lookupCommand.get().children().values()
              .stream()
              .flatMap(c -> {
                List<String> possible = new ArrayList<>();
                if (c instanceof Command)
                  possible.addAll(((Command) c).aliases());

                if (c instanceof Argument) {
                  possible.add("<" + c.labeled() + ">");

                } else
                  possible.add(c.labeled());

                return possible.stream();
              })
              .distinct()
              .collect(Collectors.toList());
    }

    CommandElement lastElementParsed = parse(executor, lookupCommand.get(), commandData, parseQueue);
    if (lastElementParsed == null) {
      return new ArrayList<>();
    }

    List<String> possibleTabCompletions = new ArrayList<>();
    if (lastElementParsed instanceof ParentableElement) {
      for (CommandElement<?> value :
              ((ParentableElement<?>) lastElementParsed).children().values()) {
        if (!filtersCheck.apply(value)) continue;

        if (value instanceof Command)
          possibleTabCompletions.addAll(((Command) value).aliases());

        else if (value instanceof NoValueArgument) {
          if (!commandData.hasKey(value.labeled()))
            possibleTabCompletions.add(value.labeled());

        } else
          possibleTabCompletions.add(value.labeled());
      }
    }

    if (spacesCount == s.length) {
      String[] finalS = s;
      possibleTabCompletions.removeIf(it -> !it.contains(finalS[finalS.length-1]));
    }

    System.out.println(Arrays.toString(commandData.keys().toArray()));
    return possibleTabCompletions;
  }

  private CommandElement parse(
      @NonNull ExecutorWrapper executor,
      @NonNull CommandElement currentElement,
      @NonNull CommandData commandData,
      @NonNull Queue<String> waitingParsing) {
    if (waitingParsing.isEmpty()) {
      return currentElement;
    }

    // Handle filters
    Function<CommandElement<?>, Boolean> filtersCheck =
        (element) -> {
          Map<String, BiPredicate<ExecutorWrapper, CommandData>> values =
              element.filters().values();
          // TODO: No access handling
          return values.isEmpty()
              || values.values().stream().allMatch(filter -> filter.test(executor, commandData));
        };

    if (currentElement instanceof ParentableElement) {
      String label = waitingParsing.peek();
      for (CommandElement<?> element :
          ((ParentableElement<?>) currentElement).children().values()) {

        if (element instanceof Command) {
          List<String> possibleLabels = new ArrayList<>();
          possibleLabels.add(element.labeled());
          possibleLabels.addAll(((Command) element).aliases());

          // Try to match possible labels with the label
          if (possibleLabels.stream().anyMatch(label::equalsIgnoreCase)) {
            waitingParsing.poll();

            if (!filtersCheck.apply(element)) continue;
            return parse(executor, element, commandData, waitingParsing);
          }
        }

        if (element instanceof Argument) {
          ParseResult<?> parseResult = ((Argument<?>) element).parser().parse(waitingParsing);
          if (!filtersCheck.apply(element)) continue;

          if (parseResult.getMessage() != null) {
            if (((Argument<?>) element).optional()) continue;

            // TODO: Incorrect argument handling
            continue;
          }

          Object parsedObject = parseResult.getObject();
          commandData.add(element.labeled(), parsedObject);

          return parse(executor, element, commandData, waitingParsing);
        }

        if (element instanceof NoValueArgument) {
          waitingParsing.poll();

          if (label.equalsIgnoreCase(element.labeled())) {
            if (!filtersCheck.apply(element)) continue;

            commandData.add(element.labeled(), true);
            return parse(executor, currentElement, commandData, waitingParsing);
          }
        }
      }
    }

    return null;
  }
}
