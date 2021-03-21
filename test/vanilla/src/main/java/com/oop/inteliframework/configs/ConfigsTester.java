package com.oop.inteliframework.configs;

import com.oop.inteliframework.config.configuration.PlainConfig;
import com.oop.inteliframework.config.configuration.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.node.BaseParentNode;
import com.oop.inteliframework.config.property.PropertyHelper;

import java.io.File;

public class ConfigsTester {
  public static void doTest() {
    File file = new File("/run/media/oop-778/Misc/Work/inteli-framework/test/vanilla/src/main/resources/tata.yml");

//    BaseParentNode baseParentNode = PropertyHelper.handleConfigurableSerialization(
//            main,
//            new SimpleSection(),
//            false
//    );

    PlainConfig nodes = new PlainConfig(file);

    ConfigurationHandlers.findHandler(file.getName()).ifPresent(handler -> handler.save(nodes, new File("/run/media/oop-778/Misc/Work/inteli-framework/test/vanilla/src/main/resources/tata2.yml")));

    //        Node node = testSubSection.toNode();
    //
    //        // Base
    //        new Configs(com.oop.inteliframework.configs.Test.class, new File("/").toPath(), "/")
    //            // Menus
    //            .children("menus", true, menusConfigs -> {
    //                menusConfigs.loadAs(
    //                    new Configs.LoadOptions<>()
    //                        // To not use reflection, here we just pass an empty object
    //                        .provider(fileConfig -> new
    // com.oop.inteliframework.configs.MenuConfig())
    //                        // We only need to load files that ends with .yml
    //                        .fileNameFilter(name -> name.endsWith(".yml"))
    //                        // In case some menus got updated, so we gotta just import any file
    // that ends up with .yml
    //                        .importFromResources(
    //                            new InteliPair<>(true, Paths.CopyOption.COPY_IF_NOT_EXIST))
    //                );
    //            });
  }
}
