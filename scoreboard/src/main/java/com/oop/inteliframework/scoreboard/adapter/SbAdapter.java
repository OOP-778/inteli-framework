package com.oop.inteliframework.scoreboard.adapter;

import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.commons.util.SimpleReflection;
import com.oop.inteliframework.scoreboard.IScoreboard;
import com.oop.inteliframework.scoreboard.adapter.impl.*;
import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


@Getter
public abstract class SbAdapter {

    public static final SbAdapter implementation;
    protected static Constructor<?>
            SB_OBJECTIVE_PACKET_CONSTRUCTOR,
            SB_OBJECTIVE_PACKET_DISPLAY_CONSTRUCTOR,
            SB_SCORE_PACKET_CONSTRUCTOR,
            SB_TEAM_PACKET_CONSTRUCTOR;
    protected static Enum<?>
            ENUM_SB_HEALTH_INTEGER,
            ENUM_SB_ACTION_REMOVE,
            ENUM_SB_ACTION_CHANGE,
            ENUM_SB_ACTION_REMOVE_1_13,
            ENUM_SB_ACTION_CHANGE_1_13;

    private static Method MESSAGE_FROM_STRING;

    static {

        if (InteliVersion.is(8))
            implementation = new SbAdapter_1_8();
        else if (InteliVersion.is(12))
            implementation = new SbAdapter_1_12();
        else if (InteliVersion.is(13))
            implementation = new SbAdapter_1_13();
        else if (InteliVersion.is(14))
            implementation = new SbAdapter_1_14();
        else if (InteliVersion.is(15))
            implementation = new SbAdapter_1_15();
        else if (InteliVersion.is(16))
            implementation = new SbAdapter_1_16();
        else
            throw new Error("Unsupported version " + InteliVersion.getStringVersion() + " for scoreboard!");

    }

    private BiConsumer<Player, Object> packetSender;

    public SbAdapter() {
        try {
            Class<?> CRAFT_CHAT_MESSAGE_CLASS,
                    ENTITY_PLAYER_CLASS,
                    PLAYER_CONNECTION_CLASS,
                    CRAFT_PLAYER_CLASS,
                    ENUM_SB_ACTION_CLASS,
                    ENUM_SB_ACTION_CLASS_1_13,
                    ENUM_SB_DISPLAY_CLASS;
            CRAFT_CHAT_MESSAGE_CLASS = SimpleReflection.findClass("{cb}.util.CraftChatMessage");
            ENTITY_PLAYER_CLASS = SimpleReflection.findClass("{nms}.EntityPlayer");
            PLAYER_CONNECTION_CLASS = SimpleReflection.findClass("{nms}.PlayerConnection");
            CRAFT_PLAYER_CLASS = SimpleReflection.findClass("{cb}.entity.CraftPlayer");

            Method PLAYER_GET_HANDLE = SimpleReflection.getMethod(CRAFT_PLAYER_CLASS, "getHandle");
            Method CONNECTION_SEND_PACKET = SimpleReflection.getMethod(PLAYER_CONNECTION_CLASS, "sendPacket", SimpleReflection.findClass("{nms}.Packet"));
            Field PLAYER_CONNECTION = SimpleReflection.getField(ENTITY_PLAYER_CLASS, "playerConnection");

            if (InteliVersion.isBefore(8)) {
                ENUM_SB_ACTION_CLASS = SimpleReflection.findClass("{nms}.PacketPlayOutScoreboardScore$EnumScoreboardAction");

                ENUM_SB_ACTION_CHANGE = Enum.valueOf((Class<Enum>) ENUM_SB_ACTION_CLASS, "CHANGE");
                ENUM_SB_ACTION_REMOVE = Enum.valueOf((Class<Enum>) ENUM_SB_ACTION_CLASS, "REMOVE");
            }
            if (InteliVersion.isOrAfter(13)) {
                ENUM_SB_ACTION_CLASS_1_13 = SimpleReflection.findClass("{nms}.ScoreboardServer$Action");

                ENUM_SB_ACTION_CHANGE_1_13 = Enum.valueOf((Class<Enum>) ENUM_SB_ACTION_CLASS_1_13, "CHANGE");
                ENUM_SB_ACTION_REMOVE_1_13 = Enum.valueOf((Class<Enum>) ENUM_SB_ACTION_CLASS_1_13, "REMOVE");
            }

            ENUM_SB_DISPLAY_CLASS = SimpleReflection.findClass("{nms}.IScoreboardCriteria$EnumScoreboardHealthDisplay");
            ENUM_SB_HEALTH_INTEGER = Enum.valueOf((Class<Enum>) ENUM_SB_DISPLAY_CLASS, "INTEGER");

            MESSAGE_FROM_STRING = SimpleReflection.getMethod(
                    CRAFT_CHAT_MESSAGE_CLASS,
                    "fromString",
                    String.class
            );

            // Set packet sender
            packetSender = (player, packet) -> {
                try {
                    CONNECTION_SEND_PACKET.invoke(
                            PLAYER_CONNECTION.get(PLAYER_GET_HANDLE.invoke(player)),
                            packet
                    );
                    //printOutPacket(packet);
                } catch (Throwable throwable) {
                    throw new IllegalStateException("Failed to send packet to " + player.getName(), throwable);
                }
            };

            SB_OBJECTIVE_PACKET_CONSTRUCTOR = SimpleReflection.getConstructor(
                    SimpleReflection.findClass("{nms}.PacketPlayOutScoreboardObjective")
            );

            SB_OBJECTIVE_PACKET_DISPLAY_CONSTRUCTOR = SimpleReflection.getConstructor(
                    SimpleReflection.findClass("{nms}.PacketPlayOutScoreboardDisplayObjective")
            );

            SB_TEAM_PACKET_CONSTRUCTOR = SimpleReflection.getConstructor(
                    SimpleReflection.findClass("{nms}.PacketPlayOutScoreboardTeam")
            );

            SB_SCORE_PACKET_CONSTRUCTOR = SimpleReflection.getConstructor(
                    SimpleReflection.findClass("{nms}.PacketPlayOutScoreboardScore")
            );
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to construct adapter of " + getClass().getSimpleName(), throwable);
        }
    }

    protected void setCompField(Object where, String fieldName, String value) {
        try {
            Field field = SimpleReflection.getField(where.getClass(), fieldName);
            if (!InteliVersion.isBefore(13)) {
                field.set(where, Array.get(MESSAGE_FROM_STRING.invoke(null, value), 0));
            } else
                field.set(where, value);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to set component field " + where + " value: " + value, throwable);
        }
    }

    protected void setField(Object where, String fieldName, Object to) {
        try {
            Field field = SimpleReflection.getField(where.getClass(), fieldName);
            field.set(where, to);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to set field of name `" + fieldName + "` of " + where.getClass().getSimpleName() + " to " + to, throwable);
        }
    }

    public void sendObjective(IScoreboard scoreboard, ObjectiveAction objectiveAction, Player... players) {
        try {
            _sendObjective(scoreboard, objectiveAction, players);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to send objective " + objectiveAction.name() + " to " + Arrays.stream(players).map(HumanEntity::getName).collect(Collectors.joining(", ")), throwable);
        }
    }

    public void sendDisplayObjective(IScoreboard scoreboard, Player... players) {
        try {
            _sendDisplayObjective(scoreboard, players);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to send display objective to " + Arrays.stream(players).map(HumanEntity::getName).collect(Collectors.joining(", ")), throwable);
        }
    }

    public void sendScore(IScoreboard scoreboard, int line, ScoreAction scoreAction, Player... players) {
        try {
            _sendScore(scoreboard, line, scoreAction, players);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to send score line: " + line + ", action: " + scoreAction.name() + " to " + Arrays.stream(players).map(HumanEntity::getName).collect(Collectors.joining(", ")), throwable);
        }
    }

    public void sendTeam(IScoreboard scoreboard, String identifier, String[] parts, TeamAction teamAction, Player... players) {
        try {
            _sendTeam(scoreboard, identifier, parts, teamAction, players);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to send team parts: " + Arrays.toString(parts) + ", action: " + teamAction.name() + " to " + Arrays.stream(players).map(HumanEntity::getName).collect(Collectors.joining(", ")), throwable);
        }
    }

    protected abstract void _sendObjective(IScoreboard scoreboard, ObjectiveAction objectiveAction, Player... players) throws Throwable;

    protected abstract void _sendDisplayObjective(IScoreboard scoreboard, Player... players) throws Throwable;

    protected abstract void _sendScore(IScoreboard scoreboard, int line, ScoreAction scoreAction, Player... players) throws Throwable;

    protected abstract void _sendTeam(IScoreboard scoreboard, String identifier, String[] parts, TeamAction teamAction, Player... players) throws Throwable;

    public static enum ScoreAction {
        CHANGE,
        REMOVE
    }

    public static enum ObjectiveAction {
        // 0
        CREATE,

        // 1
        REMOVE,

        // 2
        UPDATE,
    }

    public static enum TeamAction {
        CREATE,
        REMOVE,
        UPDATE
    }
}
