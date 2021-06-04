package com.oop.inteliframework.dependency.common;

import com.oop.inteliframework.dependency.Library;
import com.oop.inteliframework.dependency.LibraryManager;
import com.oop.inteliframework.dependency.classloader.URLClassLoaderHelper;
import com.oop.inteliframework.dependency.logging.adapters.LogAdapter;
import com.oop.inteliframework.dependency.relocation.Relocation;
import com.oop.inteliframework.plugin.module.InteliModule;

import java.io.File;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class CommonLibraryManager extends LibraryManager implements InteliModule {
  protected final List<Library> libraries = new ArrayList<>();
  private final URLClassLoaderHelper classLoader;

  public CommonLibraryManager(LogAdapter logAdapter, URLClassLoader classLoader, File directory) {
    super(logAdapter, directory.toPath());
    this.classLoader = new URLClassLoaderHelper(classLoader);

    addMavenCentral();
    addJCenter();

    libraries.add(
        Library.builder()
            .classesPath("com.google")
            .from("com.google.guava:guava:30.1-jre")
            .build());
    libraries.add(
        (Library.builder()
            .classesPath("org.apache.commons.lang3")
            .from("org.apache.commons:commons-lang3:3.12.0")
            .build()));
  }

  @Override
  protected void addToClasspath(Path file) {
    classLoader.addToClasspath(file);
  }

  public void load() {
    for (Library library : libraries) {
      loadLibrary(library);
    }
  }

  public CommonLibraryManager appendLib(Library library) {
    this.libraries.add(library);
    return this;
  }

  public CommonLibraryManager relocate(
      Predicate<Library> libraryPredicate, Function<Library, Relocation> relocationFunction) {
    for (Library library : libraries) {
      if (!libraryPredicate.test(library)) continue;

      library.getRelocations().add(relocationFunction.apply(library));
    }

    return this;
  }
}
