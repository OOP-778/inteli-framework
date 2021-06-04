package com.oop.inteliframework.command.bukkit;

import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.oop.inteliframework.commons.util.StringFormat.format;

// Default bukkit arguments
public interface BukkitArguments {
  static Argument<Player> playerArgument(Consumer<Argument<Player>>... consumer) {
    Argument<Player> playerArgument = new Argument<>();
    playerArgument.labeled("player");
    playerArgument.parser(
        inputs -> {
          String input = inputs.poll();
          Player player = Bukkit.getPlayerExact(input);
          if (player == null) return new ParseResult<>(format("Invalid player {}", input));

          return new ParseResult<>(player);
        });
    playerArgument.tabComplete(
        (element, history) ->
            Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));

    if (consumer.length != 0) {
      consumer[0].accept(playerArgument);
    }

    return playerArgument;
  }
}
