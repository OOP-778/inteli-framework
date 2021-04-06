package com.oop.inteliframework.config.file.watcher;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class WatchFired {

  private final WatchEvent.Kind kind;
  private final Path path;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    WatchFired that = (WatchFired) o;
    return that.hashCode() == hashCode();
  }

  @Override
  public int hashCode() {
    return Objects.hash(path.toString(), kind.name());
  }
}
