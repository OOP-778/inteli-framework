package com.oop.inteliframework.command.registry;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.element.CommandElement;
import com.oop.inteliframework.command.element.ParentableElement;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import com.oop.inteliframework.command.element.command.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;

/** Class that handles all the commands registered */
public class CommandRegistry {

  /** Store all the commands ignore case here */
  private final TreeMap<String, Command> elementTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  public void register(Command command) {
    elementTreeMap.put(command.labeled(), command);
  }

  /** Command execution section */

  // Execute our command
  public boolean execute(ExecutorWrapper executor, String input) {
    // If command contains slash, get rid of it
    if (input.startsWith("/"))
      input = input.substring(1);

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

  /** Tab Complete Section */
  public List<String> tabComplete(ExecutorWrapper wrapper, String input) {
    String[] s = StringUtils.split(" ");
    Queue<String> waitingParsing = new LinkedList<>(Arrays.asList(s));
    if (waitingParsing.isEmpty()) return new ArrayList<>();

    
  }
}
