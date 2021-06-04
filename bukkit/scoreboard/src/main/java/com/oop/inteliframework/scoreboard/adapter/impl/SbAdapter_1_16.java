package com.oop.inteliframework.scoreboard.adapter.impl;

import com.oop.inteliframework.commons.util.StringFormat;
import com.oop.inteliframework.scoreboard.InteliScoreboard;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SbAdapter_1_16 extends SbAdapter {

  @Override
  protected void _sendObjective(
      InteliScoreboard scoreboard, ObjectiveAction objectiveAction, Player... players)
      throws Throwable {
    Object packet = SB_OBJECTIVE_PACKET_CONSTRUCTOR.newInstance();
    setField(packet, "a", scoreboard.getId());
    setField(packet, "d", objectiveAction.ordinal());

    for (Player player : players) {
      if (objectiveAction != ObjectiveAction.REMOVE) {
        setCompField(
            packet, "b", StringFormat.colored(scoreboard.getTitleSupplier().apply(player)));
        if (ENUM_SB_HEALTH_INTEGER != null) setField(packet, "c", ENUM_SB_HEALTH_INTEGER);
      }

      getPacketSender().accept(player, packet);
    }
  }

  @Override
  protected void _sendDisplayObjective(InteliScoreboard scoreboard, Player... players)
      throws Throwable {
    Object packet = SB_OBJECTIVE_PACKET_DISPLAY_CONSTRUCTOR.newInstance();
    setField(packet, "b", scoreboard.getId());
    setField(packet, "a", 1);

    for (Player player : players) getPacketSender().accept(player, packet);
  }

  @Override
  protected void _sendScore(
      InteliScoreboard scoreboard, int line, ScoreAction scoreAction, Player... players)
      throws Throwable {
    Object packet = SB_SCORE_PACKET_CONSTRUCTOR.newInstance();
    setField(packet, "c", (15 - line));
    setField(packet, "b", scoreboard.getId());

    setField(
        packet,
        "d",
        scoreAction == ScoreAction.REMOVE
            ? ENUM_SB_ACTION_REMOVE_1_13
            : ENUM_SB_ACTION_CHANGE_1_13);
    setField(packet, "a", StringFormat.colored(InteliScoreboard.lineIdentifiers[line]));

    for (Player player : players) getPacketSender().accept(player, packet);
  }

  @Override
  protected void _sendTeam(
      InteliScoreboard scoreboard,
      String identifier,
      String[] parts,
      TeamAction teamAction,
      Player... players)
      throws Throwable {
    Object packet = SB_TEAM_PACKET_CONSTRUCTOR.newInstance();
    setField(packet, "a", identifier);
    setField(packet, "i", teamAction.ordinal());

    if (teamAction == TeamAction.CREATE || teamAction == TeamAction.UPDATE) {
      if (teamAction == TeamAction.CREATE) setField(packet, "h", Collections.singleton(identifier));

      setCompField(packet, "c", StringFormat.colored(parts[0]));
    }

    for (Player player : players) getPacketSender().accept(player, packet);
  }
}
