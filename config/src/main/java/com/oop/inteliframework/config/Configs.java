package com.oop.inteliframework.config;

import static com.oop.inteliframework.commons.util.CollectionHelper.addAndReturn;

import com.oop.inteliframework.commons.util.InteliPair;
import com.oop.inteliframework.config.configuration.PlainConfig;
import com.oop.inteliframework.config.node.ParentNode;
import com.oop.inteliframework.config.util.Paths;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

public class Configs {

    private final TreeMap<String, PlainConfig> plainConfigs = new TreeMap<>(
        String::compareToIgnoreCase);
    private final TreeMap<String, Object> associatedConfigs = new TreeMap<>(
        String::compareToIgnoreCase);
    private final TreeMap<String, Configs> children = new TreeMap<>(String::compareToIgnoreCase);

    private final Class<?> owner;
    private final Configs parent;
    private final Path baseDirectory;
    private final String directoryName;

    public Configs(Class<?> owner, Path baseDirectory, String directoryName, Configs parent) {
        this.owner = owner;
        this.baseDirectory = baseDirectory;
        this.parent = parent;
        this.directoryName = directoryName;
    }

    public Configs(Class<?> owner, Path baseDirectory, String directoryName) {
        this(owner, baseDirectory, directoryName, null);
    }

//    public <T> Optional<T> find(Class<T> type, String name) {
//        AssociatedConfig associatedConfig = associatedConfigs.get(name);
//        if (associatedConfig == null) {
//            return Optional.empty();
//        }
//
//        Preconditions.checkArgument(
//            type.isAssignableFrom(associatedConfig.getHolder().getClass()),
//            format("The given type is not the same as found type. Found: {}, required: {}",
//                associatedConfig.getHolder().getClass().getSimpleName(), type.getSimpleName())
//        );
//
//        return Optional.of((T) associatedConfig.getHolder());
//    }

    public Configs children(Class<?> owner, String path, boolean importFolderIfNotPresent,
        Consumer<Configs> childrenConsumer) {
        if (StringUtils.contains(path, ".")) {
            throw new IllegalStateException("Multi Path not supported yet");
        }

        Path childrenPath = baseDirectory.resolve(path);

        // If the path doesn't exist, import the whole folder
        if (!Files.exists(childrenPath) && importFolderIfNotPresent) {
            Paths
                .copyFolderFromJar(path, baseDirectory.toFile(), Paths.CopyOption.COPY_IF_NOT_EXIST,
                    owner);
        }

        Configs configs = children
            .computeIfAbsent(path, p -> new Configs(owner, baseDirectory.resolve(path), path));
        childrenConsumer.accept(configs);
        return this;
    }

    public Configs children(String path, boolean importFolderIfNotPresent,
        Consumer<Configs> consumer) {
        return children(owner, path, importFolderIfNotPresent, consumer);
    }

    public Configs children(String path, Consumer<Configs> consumer) {
        return children(owner, path, false, consumer);
    }

    public <T> List<T> loadAll(LoadOptions<T>... loadOptions) {
        for (LoadOptions<T> loadOption : loadOptions) {
            // Before we load anything, let's make sure we import
            if (loadOption.importFromResources().getKey()) {
                Paths.copyResourcesFromJar(
                    directoryName,
                    fileName -> fileName.startsWith(directoryName) && (
                        loadOption.fileNameFilter == null || loadOption.fileNameFilter
                            .test(fileName)),
                    owner,
                    new File(baseDirectory.toAbsolutePath().toString()),
                    loadOption.importFromResources().getValue()
                );
            }

            File directory = new File(baseDirectory.toAbsolutePath().toString());
            for (File file : directory.listFiles()) {
                if (!loadOption.fileNameFilter.test(file.getName())) {
                    continue;
                }

                PlainConfig config = new PlainConfig(file);
                T apply = loadOption.provider.apply(config);

                associatedConfigs.put(file.getName().split("\\.")[0], apply);
            }
        }

        return null;
    }

    public String path() {
        if (parent == null) {
            return directoryName;
        }
        return addAndReturn(parents(), directoryName)
            .stream()
            .filter(it -> it.trim().length() != 0)
            .collect(Collectors.joining(File.separator));
    }

    // We're getting the parents with same class to get the path in resource
    private List<String> parents() {
        List<String> parents = new LinkedList<>();
        Configs current = parent;
        Class<?> baseClass = owner;

        while (true) {
            if (current == null) {
                break;
            }

            if (current.owner != baseClass) {
                break;
            }

            parents.add(current.directoryName);
            current = Optional.ofNullable(current.parent).orElse(null);
        }

        Collections.reverse(parents);
        return parents;
    }

    public <T> T loadAs(LoadOptions<T> loadOptions) {
        return null;
    }

    @Data
    @Accessors(fluent = true, chain = true)
    public static class LoadOptions<T> {

        private Predicate<String> fileNameFilter = in -> true;
        private @NonNull Function<ParentNode, T> provider;
        private @NonNull InteliPair<Boolean, Paths.CopyOption> importFromResources = new InteliPair<>(
            false, Paths.CopyOption.COPY_IF_NOT_EXIST);
    }
}
