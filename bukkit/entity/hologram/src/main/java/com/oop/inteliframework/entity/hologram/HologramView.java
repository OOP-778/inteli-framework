package com.oop.inteliframework.entity.hologram;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.oop.inteliframework.commons.util.ConcurrentObject;
import com.oop.inteliframework.commons.util.InsertableList;
import com.oop.inteliframework.commons.util.InteliClock;
import com.oop.inteliframework.entity.commons.UpdateableObject;
import com.oop.inteliframework.entity.hologram.line.HologramText;
import com.oop.inteliframework.entity.hologram.rule.HologramRule;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class HologramView {
  private final InteliClock refreshClock = new InteliClock(40);

  private final UpdateableObject<Float> spacing = new UpdateableObject<>(0.26f);

  @Getter private final UpdateableObject<Location> baseLocation = new UpdateableObject<>(null);

  @Getter(AccessLevel.PACKAGE)
  private final Set<HologramRule> rules = Sets.newConcurrentHashSet();

  private final ConcurrentObject<LinkedList<HologramLine<?, ?>>> lines =
      new ConcurrentObject<>(new LinkedList<>());
  private final Set<Player> viewers = Sets.newConcurrentHashSet();
  private final Map<HologramLine<?, ?>, Integer> oldIndexes = new HashMap<>();

  @Getter(value = AccessLevel.PACKAGE)
  private Location lastLocation;

  @Getter private long refreshRate = -1;
  @Getter private Hologram hologram;

  public Optional<HologramLine<?, ?>> getLine(int index) {
    Preconditions.checkArgument(
        lines.use(List::size) > index,
        "ArrayIndexOutBounds " + index + "/" + (lines.use(List::size) - 1));
    return Optional.ofNullable(lines.use(list -> list.get(index)));
  }

  public HologramLine<?, ?> addLine(String text) {
    return addLine(() -> text);
  }

  public HologramLine<?, ?> addLine(HologramLine line) {
    lines.use(l -> l.add(line));
    return line;
  }

  public HologramLine<?, ?> addLine(Supplier<String> textSupplier) {
    HologramLine<?, ?> line = new HologramText(textSupplier);
    lines.use(l -> l.add(line));
    return line;
  }

  public HologramLine<?, ?> setLine(int index, HologramLine<?, ?> line) {
    Preconditions.checkArgument(
        lines.use(List::size) > index,
        "ArrayIndexOutBounds " + index + "/" + (lines.use(List::size) - 1));
    lines.use(l -> l.set(index, line));
    return line;
  }

  public HologramLine<?, ?> setLine(int index, String text) {
    return setLine(index, new HologramText(text));
  }

  public HologramLine<?, ?> insertLine(
      int index, HologramLine<?, ?> line, InsertableList.InsertMethod method) {
    Preconditions.checkArgument(
        lines.use(List::size) > index,
        "ArrayIndexOutBounds " + index + "/" + (lines.use(List::size) - 1));
    try {
      this.lines.preUse();
      LinkedList<HologramLine<?, ?>> lines = this.lines.getObject();
      switch (method) {
        case BEFORE:
          lines.add(index - 1, line);
          break;
        case AFTER:
          lines.add(index + 1, line);
          break;
        case REPLACE:
          lines.set(index, line);
          break;
      }
      this.lines.postUse();
    } catch (Throwable throwable) {
      lines.postUse();
      throwable.printStackTrace();
    }
    return line;
  }

  public HologramLine<?, ?> insertLine(int index, HologramLine<?, ?> line) {
    return insertLine(index, line, InsertableList.InsertMethod.BEFORE);
  }

  public boolean isViewer(Player player) {
    return viewers.contains(player);
  }

  void updateViewers(Collection<Player> worldPlayers) {
    Set<Player> oldViewers = Sets.newHashSet(viewers);

    viewers.clear();
    for (Player player : new ArrayList<>(worldPlayers)) {
      if (!player.isOnline()) {
        worldPlayers.remove(player);
        continue;
      }

      if (rules.isEmpty() || rules.stream().allMatch(rule -> rule.canSee(this, player))) {
        viewers.add(player);
        worldPlayers.remove(player);
      }
    }

    lines.modify(
        list -> {
          Stream.of(oldViewers, viewers)
              .flatMap(Collection::stream)
              .forEach(
                  player -> {
                    for (HologramLine<?, ?> hologramLine : list) {
                      if (viewers.contains(player) && !oldViewers.contains(player)) {
                        hologramLine.handleAdd(player);
                        continue;
                      }

                      if (!viewers.contains(player)) {
                        hologramLine.handleRemove(player);
                      }
                    }
                  });
        });
  }

  void update() {
    if (!refreshClock.tick()) return;
    if (lastLocation != null && baseLocation.isUpdated()) clearCache();

    // Update the placement of lines
    lines.preUse();

    LinkedList<HologramLine<?, ?>> lines = this.lines.getObject();
    for (HologramLine<?, ?> line : lines) {
      Integer i = oldIndexes.get(line);
      int i2 = lines.indexOf(line);
      if (i == null || i2 != i || baseLocation.isUpdated()) {
        readjustLocations(lines);
        break;
      }
    }

    // Update the lines
    for (HologramLine<?, ?> line : lines) {
      if (line.getHologramView() == null) line.setHologramView(this);

      line.preUpdate();
      line.update();
      line.postUpdate();
    }

    this.lines.postUse();
  }

  private void clearCache() {
    lines.modify(lines -> lines.forEach(HologramLine::clearData));
  }

  public void readjustLocations(LinkedList<HologramLine<?, ?>> lines) {
    oldIndexes.clear();
    lastLocation = baseLocation.get().clone();
    for (int i = 0; i < lines.size(); i++) {
      HologramLine hologramLine = lines.get(i);
      hologramLine.setLocation(lastLocation);
      lastLocation = lastLocation.clone().add(0, -spacing.get(), 0);

      oldIndexes.put(hologramLine, i);
    }
  }

  public void despawn() {
    lines.modify(l -> l.forEach(HologramLine::remove));
  }

  public void remove() {
    despawn();
    lines.modify(LinkedList::clear);
  }

  public void animate() {
    // lines.modify(lines -> lines.stream().filter(line -> line instanceof
    // HologramText).forEach(line -> ((HologramText) line).animate()));
  }

  protected void setLocation(Location location) {
    this.baseLocation.set(location);
  }

  public void addRule(HologramRule... rule) {
    rules.addAll(Arrays.asList(rule));
  }

  public void addLines(HologramLine<?, ?>... line) {
    lines.use(lines -> lines.addAll(Arrays.asList(line)));
  }

  public Set<Player> getViewers() {
    return Collections.unmodifiableSet(viewers);
  }

  protected void setHologram(Hologram hologram) {
    this.hologram = hologram;
  }

  public void setRefreshRate(long time, TimeUnit unit) {
    setRefreshRate(unit.toMillis(time));
  }

  public void setRefreshRate(long milis) {
    refreshClock.reset();
    refreshClock.setRunAt(milis);
    this.refreshRate = milis;
  }
}
