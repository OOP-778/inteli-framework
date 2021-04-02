package com.oop.inteliframework.testplugin.command;

import com.oop.inteliframework.command.bukkit.BukkitCommandExecutor;
import com.oop.inteliframework.command.bukkit.BukkitCommandRegistry;
import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.NoValueArgument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import com.oop.inteliframework.command.element.command.Command;
import com.oop.inteliframework.testplugin.TestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.stream.Collectors;

public class CommandsTesting implements Listener {
  public CommandsTesting(TestPlugin plugin) {
    Bukkit.getPluginManager().registerEvents(this, plugin);

    BukkitCommandRegistry commandRegistry = new BukkitCommandRegistry();
    commandRegistry.register(
        new Command()
            .labeled("checkuser")
            .onExecute(
                ((executor, commandData) -> {
                  executor.as(BukkitCommandExecutor.class).commandSender.sendMessage("Hello!");
                }))
            .addChild(
                new PlayersArg()
                    .addChild(
                        new Command()
                            .labeled("oopGay?")
                            .onExecute(
                                ((executor, commandData) -> {
                                  executor
                                      .as(BukkitCommandExecutor.class)
                                      .commandSender
                                      .sendMessage("Heylo!");
                                }))
                            .addChild(new NoValueArgument().labeled("--silent")))
                    .addChild(
                        new Command()
                            .labeled("gayNoYes?")
                            .onExecute(
                                ((executor, commandData) -> {
                                  executor
                                      .as(BukkitCommandExecutor.class)
                                      .commandSender
                                      .sendMessage("Heylo!");
                                }))
                            .addChild(new NoValueArgument().labeled("--silent"))))
    );
  }

  public class PlayersArg extends Argument<Player> {
    public PlayersArg() {
      labeled("player");
      tabComplete(
          ((executor, element, commandData) ->
              Bukkit.getOnlinePlayers().stream()
                  .map(HumanEntity::getName)
                  .collect(Collectors.toList())));
      parser(
          (argsQueue -> {
            Player player = Bukkit.getPlayerExact(argsQueue.poll());
            if (player == null) return new ParseResult<>("Invalid Player");

            return new ParseResult<>(player);
          }));
    }
  }
}
