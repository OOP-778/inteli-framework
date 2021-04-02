package com.oop.inteliframework.dependency;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class StorageDependencies {
  private final List<Library> libraries = new ArrayList<>();
  private final List<String> repositories = new ArrayList<>();

  public StorageDependencies addLib(Library... libraries) {
    this.libraries.addAll(Arrays.asList(libraries));
    return this;
  }

  public StorageDependencies addRepo(String... repos) {
    this.repositories.addAll(Arrays.asList(repos));
    return this;
  }
}
