package com.oop.inteliframework.config.file.watcher;

import com.oop.inteliframework.plugin.InteliPlatform;
import lombok.SneakyThrows;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {

  private final Path folderPath;
  private final ConcurrentLinkedDeque<WatchFired> fired = new ConcurrentLinkedDeque<>();
  private final Map<WatchEvent.Kind, List<Consumer<Path>>> listeners = new ConcurrentHashMap<>();

  private WatchService service;
  private boolean exit;

  @SneakyThrows
  public FileWatcher() {
    this.folderPath = InteliPlatform.getInstance().starter().dataDirectory();
    if (!Files.exists(folderPath)) {
      Files.createDirectories(folderPath);
    }
  }

  @SneakyThrows
  public void watch() {
    if (exit) return;

    try {
      // We obtain the file system of the Path
      if (service == null) {
        FileSystem fileSystem = folderPath.getFileSystem();
        service = fileSystem.newWatchService();

        folderPath.register(service, ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
      }

      // Wait for the next event
      WatchKey watchKey = service.take();

      for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
        // Get the type of the event

        WatchEvent.Kind<?> kind = watchEvent.kind();
        if (kind == OVERFLOW) continue;

        Path watchEventPath = (Path) watchEvent.context();
        if (watchEventPath.toString().endsWith("~")) continue;

        this.fired.add(new WatchFired(kind, watchEventPath));
      }

      if (!watchKey.reset()) {
        exit = true;
      }

    } catch (Throwable throwable) {
      new IllegalStateException("Files watcher result into error", throwable).printStackTrace();
    }
  }

  public void listen(WatchEvent.Kind kind, Consumer<Path> listener) {
    listeners.computeIfAbsent(kind, $ -> new ArrayList<>()).add(listener);
  }

  public void fire() {
    synchronized (this.fired) {
      for (WatchFired watchFired : new HashSet<>(fired)) {
        listeners
            .getOrDefault(watchFired.getKind(), new ArrayList<>())
            .forEach(listener -> listener.accept(watchFired.getPath()));
      }
      fired.clear();
    }
  }
}
