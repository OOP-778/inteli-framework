package com.oop.inteliframework.config.file;

import com.oop.inteliframework.config.file.prerequisite.FileControllerPrerequisites;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class FileController<T extends FileController<T>> {
  private final Map<String, File> files = new TreeMap<>(String::compareToIgnoreCase);

  @Getter private final Path path;

  @Getter @Setter private Predicate<Path> filter;

  private Consumer<FileControllerPrerequisites> prerequisites;

  public FileController(@NonNull Path path) {
    this.path = path;
  }

  public T prerequisites(Consumer<FileControllerPrerequisites> prerequisites) {
    this.prerequisites = prerequisites;
    return (T) this;
  }

  public File getOrCreate(String name) {
    return files.computeIfAbsent(name, $ -> {
      Path filePath = path.resolve(name);
      File file = filePath.toAbsolutePath().toFile();
      if (!Files.exists(filePath.toAbsolutePath())) {
        try {
          Files.createFile(filePath);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      return file;
    });
  }

  @SneakyThrows
  public T load() {
    files.clear();
    if (prerequisites != null) prerequisites.accept(new FileControllerPrerequisites(this));

    Files.walkFileTree(
        path,
        new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path sibling, BasicFileAttributes attrs) {
            if (filter != null && !filter.test(sibling)) return FileVisitResult.CONTINUE;

            final File file = sibling.toFile();
            files.put(file.getName(), file);
            return FileVisitResult.CONTINUE;
          }
        });

    return (T) this;
  }

  public Map<String, File> files() {
    return files;
  }
}
