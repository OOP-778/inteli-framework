package com.oop.inteliframework.config.file;

import com.oop.inteliframework.config.file.prerequisite.FileControllerPrerequisites;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FileController<T extends FileController<T>> {
  public final Map<String, File> files = new TreeMap<>(String::compareToIgnoreCase);

  @Getter
  private final Path path;

  @Getter
  @Setter
  private Predicate<Path> filter;

  private Consumer<FileControllerPrerequisites> prerequisites;

  public FileController(@NonNull Path path) {
    this.path = path;
  }

  public T prerequisites(Consumer<FileControllerPrerequisites> prerequisites) {
    this.prerequisites = prerequisites;
    return (T) this;
  }

  public T load() {
    files.clear();
    if (prerequisites != null)
      prerequisites.accept(new FileControllerPrerequisites(this));

    for (Path child : path) {
      if (filter != null && !filter.test(child)) continue;

      final File file = path.resolve(child).toFile();
      files.put(file.getName(), file);
    }

    return (T) this;
  }

  public Map<String, File> files() {
    return files;
  }
}
