package com.oop.inteliframework.plugin.module;

import com.oop.inteliframework.plugin.InteliPlatform;
import com.oop.inteliframework.plugin.logger.InteliLogger;

public interface InteliModule {
  default InteliPlatform platform() {
    return InteliPlatform.getInstance();
  }

  default InteliLogger logger() {
    return platform().logger();
  }
}
