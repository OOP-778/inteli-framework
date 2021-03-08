package com.oop.inteliframework.scoreboard;

import com.google.common.base.Function;
import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.ConcurrentObject;
import com.oop.inteliframework.commons.util.InsertableList;
import com.oop.inteliframework.commons.util.InteliVersion;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter.ObjectiveAction;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter.ScoreAction;
import com.oop.inteliframework.scoreboard.adapter.SbAdapter.TeamAction;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
public class InteliScoreboard {
    private static final SbAdapter adapter = SbAdapter.implementation;
    private final String id;

    @Setter
    @NonNull
    private Function<Player, String> titleSupplier;

    private final ConcurrentObject<InsertableList<LineEntry>> linesObject = new ConcurrentObject<>(new InsertableList<>());

    public static final String[] lineIdentifiers = IntStream
            .range('A', 'Z' + 1)
            .mapToObj(a -> ChatColor.COLOR_CHAR + (((char) a) + ""))
            .toArray(String[]::new);

    // Lines cache
    private final Map<UUID, ScoreboardCache> userCache = new ConcurrentHashMap<>();

    private final Set<Player> viewers = Sets.newConcurrentHashSet();

    public InteliScoreboard(String name) {
        if (name.length() > 10)
            name = name.substring(0, 10);
        this.id = name + ThreadLocalRandom.current().ints(0, 200)
                .limit(2)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
    }

    public static String[] splitIntoParts(String input) {
        if (InteliVersion.isOrAfter(13))
            return new String[] {input};

        String[] arr = new String[]{"", "", ""};

        int counter = 0;
        int part = 0;

        Character lastColor = null;
        Character lastDecorator = null;

        boolean shouldAddColors = false;
        boolean add;

        final List<Character> colors = Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        final List<Character> decorators = Arrays.asList('k', 'l', 'm', 'n', 'o', 'r');

        for (int i = 0; i < input.length(); i++) {
            add = true;

            if ((input.charAt(i) == '&' || input.charAt(i) == ChatColor.COLOR_CHAR) && colors.contains((input.charAt(i + 1)))) {
                lastColor = input.charAt(i + 1);
                lastDecorator = null;
                i++;
                if (counter + 2 > 16) {
                    part++;
                    counter = 0;
                    if (part > 2) throw new Error("String too big");
                }
                shouldAddColors = true;
                add = false;
            }

            if ((input.charAt(i) == '&' || input.charAt(i) == ChatColor.COLOR_CHAR) && decorators.contains((input.charAt(i + 1)))) {
                lastDecorator = input.charAt(i + 1);
                i++;
                if (counter + 2 > 16) {
                    part++;
                    counter = 0;
                    if (part > 2) throw new Error("String too big");
                }
                shouldAddColors = true;
                add = false;
            }

            if (counter == 16) {
                part++;
                counter = 0;
                if (part > 2) throw new Error("String too big");
                shouldAddColors = true;
            }

            if (!add) continue;

            if (shouldAddColors) {
                if (lastColor != null) {
                    arr[part] = arr[part].concat((ChatColor.COLOR_CHAR + "") + lastColor);
                    counter += 2;
                }

                if (lastDecorator != null) {
                    arr[part] = arr[part].concat((ChatColor.COLOR_CHAR + "") + lastDecorator);
                    counter += 2;
                }
                shouldAddColors = false;
            }

            arr[part] = arr[part].concat(String.valueOf(input.charAt(i)));
            counter++;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].isEmpty())
                arr[i] = null;
        }

        return arr;
    }

    public void add(Player player) {
        if (viewers.contains(player)) return;

        adapter.sendObjective(this, SbAdapter.ObjectiveAction.CREATE, player);
        adapter.sendDisplayObjective(this, player);

        update(player);
        viewers.add(player);
    }

    public void remove() {
        for (Player viewer : viewers)
            remove(viewer);
    }

    public void remove(Player player) {
        if (!viewers.contains(player)) return;
        viewers.remove(player);

        adapter.sendObjective(this, SbAdapter.ObjectiveAction.REMOVE, player);
        linesObject.modify(lines -> {
            ScoreboardCache playerCache = userCache.computeIfAbsent(player.getUniqueId(), k -> new ScoreboardCache());
            for (int i = 0; i < lines.size(); i++) {
                if (!player.isOnline()) return;

                String oldLine = playerCache.getLines().get(i);
                if (oldLine != null) {
                    adapter.sendTeam(this, lineIdentifiers[i], new String[0], TeamAction.REMOVE, player);
                    adapter.sendScore(this, i, ScoreAction.REMOVE, player);
                }
            }

            userCache.remove(player.getUniqueId());
        });
    }

    public void update(Player... players) {
        for (Player player : players) {
            ScoreboardCache playerCache = userCache.computeIfAbsent(player.getUniqueId(), k -> new ScoreboardCache());

            // Try to update title
            String title = titleSupplier.apply(player);
            if (playerCache.getTitle() == null || !title.equalsIgnoreCase(playerCache.getTitle())) {
                playerCache.setTitle(title);
                adapter.sendObjective(this, SbAdapter.ObjectiveAction.UPDATE, player);
            }

            linesObject.modify(lines -> {
                for (int i = 0; i < lines.size(); i++) {
                    if (!player.isOnline()) return;

                    LineEntry lineEntry = lines.get(i);
                    String line = lineEntry.supply(player);
                    String oldLine = playerCache.getLines().get(i);

                    // If the line wasn't present but now is, or if it has changed
                    if (oldLine == null || !line.equalsIgnoreCase(oldLine)) {
                        // Split text into multiple parts at the size of 16
                        // Make sure to keep colors
                        String[] parts = splitIntoParts(line);

                        String finalLine = line;
                        playerCache.getLines().merge(i, line, (n, n2) -> finalLine);
                        if (oldLine != null)
                            adapter.sendTeam(this, lineIdentifiers[i], parts, SbAdapter.TeamAction.UPDATE, player);
                        else
                            adapter.sendTeam(this, lineIdentifiers[i], parts, SbAdapter.TeamAction.CREATE, player);

                        adapter.sendScore(this, i, SbAdapter.ScoreAction.CHANGE, player);
                        playerCache.getLines().replace(i, line);
                    }
                }
            });
        }
    }

    public void updateAll() {
        for (Player viewer : viewers)
            update(viewer);
    }
}
