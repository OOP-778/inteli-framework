package com.oop.inteliframework.scoreboard.adapter.impl;

import com.oop.inteliframework.commons.util.StringFormat;
import com.oop.inteliframework.scoreboard.IScoreboard;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SbAdapter_1_12 extends SbAdapter {
    @Override
    protected void _sendObjective(IScoreboard scoreboard, ObjectiveAction objectiveAction, Player... players) throws Throwable {
        Object packet = SB_OBJECTIVE_PACKET_CONSTRUCTOR.newInstance();
        setField(packet, "a", scoreboard.getId());
        setField(packet, "d", objectiveAction.ordinal());

        for (Player player : players) {
            if (objectiveAction != ObjectiveAction.REMOVE) {
                setCompField(packet, "b", StringFormat.colored(scoreboard.getTitleSupplier().apply(player)));
                if (ENUM_SB_HEALTH_INTEGER != null)
                    setField(packet, "c", ENUM_SB_HEALTH_INTEGER);
            }

            getPacketSender().accept(player, packet);
        }
    }

    @Override
    protected void _sendDisplayObjective(IScoreboard scoreboard, Player... players) throws Throwable {
        Object packet = SB_OBJECTIVE_PACKET_DISPLAY_CONSTRUCTOR.newInstance();
        setField(packet, "b", scoreboard.getId());
        setField(packet, "a", 1);

        for (Player player : players)
            getPacketSender().accept(player, packet);
    }

    @Override
    protected void _sendScore(IScoreboard scoreboard, int line, ScoreAction scoreAction, Player... players) throws Throwable {
        Object packet = SB_SCORE_PACKET_CONSTRUCTOR.newInstance();
        setField(packet, "c", (15 - line));
        setField(packet, "b", scoreboard.getId());

        setField(packet, "d", scoreAction == ScoreAction.REMOVE ? ENUM_SB_ACTION_REMOVE : ENUM_SB_ACTION_CHANGE);
        setField(packet, "a", scoreboard.getLineIdentifiers()[line - 1]);

        for (Player player : players)
            getPacketSender().accept(player, packet);
    }

    @Override
    protected void _sendTeam(IScoreboard scoreboard, String identifier, String[] parts, TeamAction teamAction, Player... players) throws Throwable {
        Object packet = SB_TEAM_PACKET_CONSTRUCTOR.newInstance();
        setField(packet, "a", identifier);
        setField(packet, "i", teamAction.ordinal());

        if (teamAction == TeamAction.CREATE || teamAction == TeamAction.UPDATE) {
            if (teamAction == TeamAction.CREATE)
                setField(packet, "h", Collections.singleton(identifier));

            if (parts[0] != null)
                setField(packet, "c", parts[0]);

            if (parts[1] != null)
                setField(packet, "d", parts[1]);
        }

        for (Player player : players)
            getPacketSender().accept(player, packet);
    }
}
