package com.oop.inteliframework;

import com.oop.inteliframework.config.file.FileController;
import com.oop.inteliframework.config.file.InteliFileModule;
import com.oop.inteliframework.config.file.prerequisite.Paths;
import com.oop.inteliframework.plugin.InteliPlatform;

import java.io.File;
import static java.nio.file.StandardWatchEventKinds.*;

public class FilesTester {
    public static void doTest() {
        com.oop.inteliframework.config.file.prerequisite.Paths
                .copyResourcesFromJar(
                        file -> file.endsWith(".yml"),
                        new File("/run/media/oop-778/BRABARAR/Serrvers/OOP/TokenGenerator.jar"),
                        new File(System.getProperty("user.dir") + "/testFiles"),
                        com.oop.inteliframework.config.file.prerequisite.Paths.CopyOption.COPY_IF_NOT_EXIST
                );

        InteliFileModule inteliFileModule = InteliPlatform.getInstance()
                .safeModuleByClass(InteliFileModule.class);

        inteliFileModule.startWatcher();

        inteliFileModule.getWatcher()
                .listen(ENTRY_MODIFY, path -> {
                    System.out.println("modified " + path);
                });

        inteliFileModule.getWatcher()
                .listen(ENTRY_CREATE, path -> {
                    System.out.println("created " + path);
                });

        inteliFileModule.getWatcher()
                .listen(ENTRY_DELETE, path -> {
                    System.out.println("deleted " + path);
                });

        FileController<?> controller = new FileController<>(InteliPlatform.getInstance().starter().dataDirectory());
        controller.prerequisites(controllerPrerequisites -> {
           controllerPrerequisites.makeSureFolderExists();
           controllerPrerequisites.loadFromResources(resources -> {
               resources.filter(file -> file.endsWith(".yml"));
               resources.option(Paths.CopyOption.COPY_IF_NOT_EXIST);
           });
        });
    }
}
