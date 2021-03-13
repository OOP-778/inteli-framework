import com.oop.inteliframework.config.InteliConfigModule;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.inteliframework.config.property.PropertyHelper;

import java.io.File;
import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        File file = new File(
            "/run/media/brian/Misc/Work/inteli-framework/config/src/main/resources/");
        //PlainConfig nodes = new PlainConfig(file);

        ParentNode main = new ParentNode("Hello!", null);

        InteliConfigModule.propertyHandlerMap.put(UUID.class, new UUIDHandler());

        TestConfig config = new TestConfig();
        ParentNode smth = PropertyHelper.handleConfigurableSerialization(
                main,
                config,
                true
        );
    for (String s : smth.dump()) {
        System.out.println(s);
      //
    }

//        Node node = testSubSection.toNode();
//
//        // Base
//        new Configs(Test.class, new File("/").toPath(), "/")
//            // Menus
//            .children("menus", true, menusConfigs -> {
//                menusConfigs.loadAs(
//                    new Configs.LoadOptions<>()
//                        // To not use reflection, here we just pass an empty object
//                        .provider(fileConfig -> new MenuConfig())
//                        // We only need to load files that ends with .yml
//                        .fileNameFilter(name -> name.endsWith(".yml"))
//                        // In case some menus got updated, so we gotta just import any file that ends up with .yml
//                        .importFromResources(
//                            new InteliPair<>(true, Paths.CopyOption.COPY_IF_NOT_EXIST))
//                );
//            });
    }
}
