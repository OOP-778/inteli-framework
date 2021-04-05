package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.api.configuration.PlainConfig;
import com.oop.inteliframework.config.api.configuration.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.property.loader.Loader;
import com.oop.inteliframework.config.property.property.SerializedProperty;
import com.oop.inteliframework.config.property.serializer.Serializer;

import java.io.File;
import java.util.function.Function;

public class ConfigsTester {
  public static void doTest() {
    File file =
        new File(
            "/run/media/oop-778/Misc/Work/inteli-framework/test/vanilla/src/main/resources/serialized.yml");

    long gayStart = System.currentTimeMillis();
    TestConfig testConfig = new TestConfig();
    PlainConfig nodes = new PlainConfig(file);

    SerializedProperty apply = ((Function<TestConfig, SerializedProperty>)Serializer.serializerForConfigurable(testConfig.getClass(), false))
            .apply(testConfig);

    nodes.merge(apply.getNode().asParent());
    nodes.dump();

    ConfigurationHandlers.findHandler(file.getName())
            .get()
            .save(nodes, file);

    System.out.println("Took gay seconds: " + (System.currentTimeMillis() - gayStart));

    TestConfig deserialized = Loader.loaderFrom(TestConfig.class)
            .apply(apply.getNode());
    System.out.println(deserialized);

  }
}
