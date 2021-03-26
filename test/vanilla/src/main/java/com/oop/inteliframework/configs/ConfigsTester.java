package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandler;
import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.property.InteliPropertyModule;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.util.Serializer;
import com.oop.inteliframework.plugin.InteliPlatform;

import java.io.File;

public class ConfigsTester {
  public static void doTest() {
    File file =
        new File(
            "/run/media/oop-778/Misc/Work/inteli-framework/test/vanilla/src/main/resources/serialized.yml");

    TestConfig testConfig = new TestConfig();
    PlainConfig nodes = new PlainConfig(file);

    SerializedProperty apply = Serializer.serializerFor(testConfig)
            .apply(testConfig);

    nodes.merge(apply.getNode().asParent());

    nodes.dump();

    ConfigurationHandlers.findHandler(file.getName())
            .get()
            .save(nodes, file);
  }
}
