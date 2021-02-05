package com.oop.inteliframework.config.configuration;

import static com.oop.inteliframework.commons.util.StringFormat.format;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.configuration.handler.ConfigurationHandler;
import com.oop.inteliframework.config.configuration.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ParentNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
public class PlainConfig extends ParentNode {

    @Setter
    private ConfigurationHandler handler;

    @Getter
    @Nullable
    private File file;

    public PlainConfig(@NonNull File file) {
        super(file.getName(), null);
        this.file = file;

        handler = ConfigurationHandlers
            .findHandler(file.getName())
            .orElseThrow(() -> new IllegalStateException(
                "Failed to find configuration handler for filename: " + file.getName()));

        try {
            Node loaded = handler.load(new FileInputStream(file));
            Preconditions
                .checkArgument(loaded instanceof ParentNode, "Loaded node must be parentable!");
            merge((ParentNode) loaded);

            //dump();
        } catch (Throwable throwable) {
            throw new IllegalStateException(
                format("Failed to load file with name: {} with handler: {}", file.getName(),
                    handler.name()), throwable);
        }
    }

    public PlainConfig(String filename, @NonNull InputStream stream) {
        super(filename, null);

        handler = ConfigurationHandlers
            .findHandler(filename)
            .orElseThrow(() -> new IllegalStateException(
                "Failed to find configuration handler for filename: " + filename));

        try {
            Node loaded = handler.load(stream);
            Preconditions
                .checkArgument(loaded instanceof ParentNode, "Loaded node must be parentable!");
            merge((ParentNode) loaded);
        } catch (Throwable throwable) {
            throw new IllegalStateException(
                format("Failed to load file with name: {} with handler: {}", filename,
                    handler.name()), throwable);
        }
    }

    @Override
    public @NotNull String key() {
        return "";
    }
}
