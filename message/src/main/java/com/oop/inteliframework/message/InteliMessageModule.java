package com.oop.inteliframework.message;

import com.oop.inteliframework.plugin.module.InteliModule;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import net.kyori.adventure.platform.AudienceProvider;

@Getter
@Accessors(fluent = true)
public class InteliMessageModule implements InteliModule {
  private final AudienceProvider audiences;

  public InteliMessageModule(@NonNull AudienceProvider audiences) {
    this.audiences = audiences;
  }
}
