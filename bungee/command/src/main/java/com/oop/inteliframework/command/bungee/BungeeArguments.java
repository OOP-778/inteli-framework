package com.oop.inteliframework.command.bungee;

import com.oop.inteliframework.command.element.argument.Argument;
import com.oop.inteliframework.command.element.argument.ParseResult;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.oop.inteliframework.commons.util.StringFormat.format;

public interface BungeeArguments {
  static Argument<ProxiedPlayer> playerArgument(Consumer<Argument<ProxiedPlayer>>... consumer) {
    final Argument<ProxiedPlayer> playerArgument = new Argument<>();
    playerArgument.labeled("player");
    playerArgument.parser(
        (inputs, $) -> {
          String input = inputs.poll();
          ProxiedPlayer player = ProxyServer.getInstance().getPlayer(input);
          if (player == null) return new ParseResult<>(format("Invalid player {}", input));

          return new ParseResult<>(player);
        });

    playerArgument.tabComplete(
        (element, history) ->
            ProxyServer.getInstance().getPlayers().stream()
                .map(ProxiedPlayer::getName)
                .collect(Collectors.toList()));

    if (consumer.length != 0) {
      consumer[0].accept(playerArgument);
    }

    return playerArgument;
  }

  static Argument<ServerInfo> serverArgument(Consumer<Argument<ServerInfo>>... consumer) {
    final Argument<ServerInfo> serverInfoArgument = new Argument<>();
    serverInfoArgument.labeled("server");
    serverInfoArgument.parser(
        (inputs, $) -> {
          String serverName = inputs.poll();
          ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
          if (serverInfo == null)
            return new ParseResult<>(format("Invalid server: {}", serverName));
          return new ParseResult<>(serverInfo);
        });

    serverInfoArgument.tabComplete(
        (element, history) -> {
          return ProxyServer.getInstance().getServersCopy().values().stream()
              .map(ServerInfo::getName)
              .collect(Collectors.toList());
        });

    if (consumer.length != 0) {
        consumer[0].accept(serverInfoArgument);
    }

    return serverInfoArgument;
  }
}
