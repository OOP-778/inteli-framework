package com.oop.inteliframework.command.bukkit;

import com.oop.inteliframework.command.registry.CommandRegistry;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.commons.util.SimpleReflection;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import java.util.*;
import java.util.function.Predicate;

public class BukkitCommandMap {
  private final CommandRegistry registry;
  private SimpleCommandMap commandMap;

  public BukkitCommandMap(@NonNull CommandRegistry registry) {
    this.registry = registry;
    initCommandMap();
  }

  @SneakyThrows
  private void initCommandMap() {
    this.commandMap =
        (SimpleCommandMap)
            SimpleReflection.getField(SimplePluginManager.class, "commandMap")
                .get(Bukkit.getPluginManager());
  }

  public void unregister(String label) {
    unregisterAll(cmdLabel -> cmdLabel.equalsIgnoreCase(label));
  }

  @SneakyThrows
  public void unregisterAll(Predicate<String> filter) {
    Map<String, org.bukkit.command.Command> knownCommands =
        (Map<String, org.bukkit.command.Command>)
            SimpleReflection.getField(SimpleCommandMap.class, "knownCommands").get(commandMap);

    for (String s : new HashSet<>(knownCommands.keySet())) {
      if (!filter.test(s)) continue;

      knownCommands.remove(s);
    }
  }

  public void register(Command command) {
      org.bukkit.command.Command bukkitCommand = new org.bukkit.command.Command(
              command.labeled(),
              "",
              "",
              new LinkedList<>(command.aliases())
      ) {
        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
          registry.execute(new BukkitCommandExecutor(sender), commandLabel + " " + String.join(" ", args));
          return true;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
          if (!(sender instanceof ConsoleCommandSender)) return new ArrayList<>();
          return registry.tabComplete(new BukkitCommandExecutor(sender), alias);
        }
      };

      commandMap.register(command.labeled(), bukkitCommand);
  }
}
