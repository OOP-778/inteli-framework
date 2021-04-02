package com.oop.inteliframework.command.registry;

import com.oop.inteliframework.command.CommandData;
import com.oop.inteliframework.command.ExecutorWrapper;
import com.oop.inteliframework.command.api.ParentableElement;
import com.oop.inteliframework.command.api.TabComplete;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.command.error.CommandError;
import com.oop.inteliframework.command.registry.parser.CommandParseHistory;
import com.oop.inteliframework.command.style.RegistryStyle;
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
public class CommandRegistry {

  /** Store all the commands ignore case here */
  @Getter
  private final TreeMap<String, Command> elementTreeMap =
      new TreeMap<>(String::compareToIgnoreCase);

  @Setter @Getter @NonNull private RegistryStyle style;

  @Setter @Getter private int tabCompletionSizeLimit = 50;

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
    input = normalize(input);

    String[] s = StringUtils.split(input, ' ');
    if (s.length == 0) return false;

    Optional<Command> lookupCommand = findCommand(s[0]);
    if (!lookupCommand.isPresent()) return false;

    s = Arrays.copyOfRange(s, 1, s.length);
    if (s.length == 0) {
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

    if (parse.getLastElement() instanceof Command) {
      if (((Command) parse.getLastElement()).executer() != null)
        ((Command) parse.getLastElement()).executer().execute(executor, data);
    }

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
          .complete(history.getExecutor(), history.getLastElement(), history.getData());
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
                        possible.addAll(
                            ((TabComplete) c.tabComplete().get())
                                .complete(history.getExecutor(), c, history.getData()));

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

    String[] s = StringUtils.split(input, ' ');
    if (s.length == 0) return new ArrayList<>();

    Optional<Command> lookupCommand = findCommand(s[0]);
    if (!lookupCommand.isPresent()) return new ArrayList<>();

    input = StringUtils.replaceOnce(input, s[0], "");
    s = Arrays.copyOfRange(s, 1, s.length);

    boolean isAfterArgument = StringUtils.endsWith(input, " ");
    if (s.length == 0 && !isAfterArgument) return new ArrayList<>();
    if (s.length == 0)
      return checkSize(
          _tabComplete(
              new CommandParseHistory(
                  new CommandData(),
                  executor,
                  new LinkedList<>(),
                  new LinkedList<>(),
                  new HashSet<>(),
                  lookupCommand.get()),
              true));

    LinkedList<String> linked =
        isAfterArgument
            ? new LinkedList<>(Arrays.asList(s))
            : new LinkedList<>(Arrays.asList(Arrays.copyOfRange(s, 0, s.length - 1)));

    CommandParseHistory parse = parse(executor, lookupCommand.get(), data, linked, false);

    // We parsed last element before last argument, so we try to validate last arg based of it
    if (!parse.getResultedInto().isEmpty()) {
      style.handleError(parse.getResultedInto().toArray(new CommandError[0]), this, parse);
      return new ArrayList<>();
    }

    List<String> apply = _tabComplete(parse, !StringUtils.endsWith(input, " ") || isAfterArgument);

    if (!isAfterArgument) {
      String[] finalS = s;
      apply.removeIf(
          complete ->
              !complete
                  .toLowerCase(Locale.ROOT)
                  .contains(finalS[finalS.length - 1].toLowerCase(Locale.ROOT)));
    }

    return checkSize(apply);
  }
}
