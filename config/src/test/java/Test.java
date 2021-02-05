import com.oop.inteliframework.config.util.Paths;
import com.oop.inteliframework.config.util.Paths.CopyOption;
import java.io.File;

public class Test {
    public static void main(String[] args) {
        File file = new File(
            "/run/media/brian/Misc/Work/inteli-framework/config/src/main/resources/");
        //PlainConfig nodes = new PlainConfig(file);

        Paths
            .copyResourcesFromJar(
                "",
                f -> f.contains("menus"),
                Test.class,
                new File("/run/media/brian/Misc/Work/inteli-framework/config"),
                CopyOption.REPLACE_IF_EXIST
            );

        TestSubSection testSubSection = new TestSubSection();
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
