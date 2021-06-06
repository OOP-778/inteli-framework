package com.oop.inteliframework.command.registry;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.CommandElement;
import com.oop.inteliframework.command.api.ParentableElement;
import com.oop.inteliframework.command.api.TabComplete;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.error.MissingArgumentsError;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import com.oop.inteliframework.command.style.DefaultStyle;
import com.oop.inteliframework.command.style.RegistryStyle;
import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.oop.inteliframework.command.registry.parser.CommandParser.parse;
import static com.oop.inteliframework.command.registry.parser.ParserHelper.checkRequirements;

/** Class that handles all the commands registered */
public class CommandRegistry implements InteliModule {

  /** Store all the commands ignore case here */
  @Getter
  private final TreeMap<String, Command> elementTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  @Setter @Getter @NonNull private RegistryStyle style = new DefaultStyle();

  @Setter @Getter private int tabCompletionSizeLimit = 50;

  public void register(Command command) {
    elementTreeMap.put(command.labeled(), command);
  }

  protected <T extends CommandElement<?>> Optional<T> _findInMap(
      Map<String, T> map, String identifier) {
    return map.values().stream()
        .map(
            command -> {
              if (command.labeled().equalsIgnoreCase(identifier)) {
                return command;
              }

              if (command instanceof Command
                  && ((Command) command).aliases().contains(identifier)) {
                return command;
              }

              return null;
            })
        .filter(Objects::nonNull)
        .findFirst();
  }

  public Optional<CommandElement<?>> findCommandElement(String identifier) {
    if (StringUtils.contains(identifier, ".")) {
      LinkedList<String> queue =
          new LinkedList<>(Arrays.asList(StringUtils.split(identifier, ".")));
      Collections.reverse(queue);

      Optional<CommandElement<?>> optionalCommand = findCommandElement(queue.poll());
      while (!queue.isEmpty()) {
        optionalCommand =
            optionalCommand.flatMap(
                element -> {
                  if (element instanceof ParentableElement) {
                    return _findInMap(((ParentableElement) element).children(), queue.poll());
                  }

                  return Optional.empty();
                });
        if (!optionalCommand.isPresent()) {
          break;
        }
      }

      return optionalCommand;
    }

    return _findInMap(elementTreeMap, identifier).map(command -> command);
  }

  /** Command execution & tab completion section */

  // Execute our command
  public boolean execute(ExecutorWrapper executor, String input) {
    // If command contains slash, get rid of it
    input = normalize(input);

    String[] s = StringUtils.split(input, ' ');
    if (s.length == 0) return false;

    Optional<Command> lookupCommand =
        findCommandElement(s[0]).map(commandElement -> (Command) commandElement);
    if (!lookupCommand.isPresent()) return false;

    s = Arrays.copyOfRange(s, 1, s.length);
    if (s.length == 0) {
      Command command = lookupCommand.get();
      if (command.executer() != null && !command.requiresChildren()) {
        command.executer().execute(executor, new CommandData());
      }

      return true;
    }

    CommandData data = new CommandData();
    CommandParseHistory parse =
        parse(executor, lookupCommand.get(), data, new LinkedList<>(Arrays.asList(s)), true);

    // Too much arguments or wrong arguments
    if (!parse.getResultedInto().isEmpty()) {
      style.handleError(parse.getResultedInto().toArray(new CommandError[0]), this, parse);
      return true;
    }

    LinkedList<CommandElement> commandElements = new LinkedList<>(parse.getPath());
    Collections.reverse(commandElements);

    Command firstCommand =
        (Command)
            commandElements.stream()
                .filter(element -> element instanceof Command)
                .findFirst()
                .orElse(null);
    if (firstCommand != null) {
      if (firstCommand.executer() != null) {
        firstCommand.executer().execute(executor, data);
        return true;
      }
    }

    parse.getResultedInto().add(new MissingArgumentsError(parse, parse.getLastElement()));
    style.handleError(parse.getResultedInto().toArray(new CommandError[0]), this, parse);
    return true;
  }

  public String normalize(String input) {
    String removedSlash = input.startsWith("/") ? input.substring(1) : input;
    if (StringUtils.contains(input, ":")) {
      String[] split = StringUtils.split(input, ":");
      if (split.length == 1) removedSlash = split[0];
      else removedSlash = split[1];
    }
    return removedSlash;
  }

  protected List<String> checkSize(List<String> completion) {
    if (completion.size() > tabCompletionSizeLimit) {
      return Arrays.asList(
          Arrays.copyOfRange(completion.toArray(new String[0]), 0, tabCompletionSizeLimit));
    }

    return completion;
  }

  protected List<String> _tabComplete(@NonNull CommandParseHistory history, boolean isAfter) {
    if (!isAfter && history.getLastElement().tabComplete().isPresent()) {
      return ((TabComplete) history.getLastElement().tabComplete().get())
          .complete(history.getLastElement(), history);
    }

    if (history.getLastElement() instanceof ParentableElement) {
      return ((ParentableElement<?>) history.getLastElement())
          .children().values().stream()
              .flatMap(
                  c -> {
                    if (checkRequirements(c, history) != null) return Stream.empty();

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
              .collect(Collectors.toList());
    } else return new ArrayList<>();
  }

  /** Tab Complete Section */
  public <P extends ParentableElement<P>> List<String> tabComplete(
      ExecutorWrapper executor, String input) {
    CommandData data = new CommandData();

    input = normalize(input);

    String[] splitArgs = StringUtils.split(input, ' ');
    if (splitArgs.length == 0) return new ArrayList<>();

    Optional<Command> lookupCommand =
        findCommandElement(splitArgs[0]).map(commandElement -> (Command) commandElement);
    if (!lookupCommand.isPresent()) return new ArrayList<>();

    input = StringUtils.replaceOnce(input, splitArgs[0], "");
    splitArgs = Arrays.copyOfRange(splitArgs, 1, splitArgs.length);

    boolean isAfterArgument = StringUtils.endsWith(input, " ");
    if (splitArgs.length == 0 && !isAfterArgument) return new ArrayList<>();
    if (splitArgs.length == 0)
      return checkSize(
          _tabComplete(
              new CommandParseHistory(
                  new CommandData(),
                  executor,
                  new LinkedList<>(),
                  new LinkedHashSet<>(),
                  new LinkedList<>(),
                  lookupCommand.get()),
              true));

    LinkedList<String> linked =
        isAfterArgument
            ? new LinkedList<>(Arrays.asList(splitArgs))
            : new LinkedList<>(
                Arrays.asList(Arrays.copyOfRange(splitArgs, 0, splitArgs.length - 1)));

    CommandParseHistory parse = parse(executor, lookupCommand.get(), data, linked, false);

    // We parsed last element before last argument, so we try to validate last arg based of it
    if (!parse.getResultedInto().isEmpty()) {
      style.handleError(parse.getResultedInto().toArray(new CommandError[0]), this, parse);
      return new ArrayList<>();
    }

    List<String> apply = _tabComplete(parse, !StringUtils.endsWith(input, " ") || isAfterArgument);

    if (!isAfterArgument) {
      String[] finalS = splitArgs;
      apply.removeIf(
          complete ->
              !complete
                  .toLowerCase(Locale.ROOT)
                  .contains(finalS[finalS.length - 1].toLowerCase(Locale.ROOT)));
    }

    return checkSize(apply);
  }
}
