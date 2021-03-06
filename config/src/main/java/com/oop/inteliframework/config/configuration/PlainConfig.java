package com.oop.inteliframework.config.configuration;

import com.oop.inteliframework.commons.util.Preconditions;
import com.oop.inteliframework.config.handler.ConfigurationHandler;
import com.oop.inteliframework.config.handler.ConfigurationHandlers;
import com.oop.inteliframework.config.node.Node;
import com.oop.inteliframework.config.node.ParentableNode;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.oop.inteliframework.commons.util.StringFormat.format;

@Accessors(fluent = true)
public class PlainConfig extends ParentableNode {

    @Setter
    private ConfigurationHandler handler;

    public PlainConfig(@NonNull File file) {
        super(file.getName(), null);

        handler = ConfigurationHandlers
                .findHandler(file.getName())
                .orElseThrow(() -> new IllegalStateException("Failed to find configuration handler for filename: " + file.getName()));

        try {
            Node loaded = handler.load(new FileInputStream(file));
            Preconditions.checkArgument(loaded instanceof ParentableNode, "Loaded node must be parentable!");
            merge((ParentableNode) loaded);
        } catch (Throwable throwable) {
            throw new IllegalStateException(format("Failed to load file with name: {} with handler: {}", file.getName(), handler.name()), throwable);
        }
    }

    public PlainConfig(String filename, @NonNull InputStream stream) {
        super(filename, null);

        handler = ConfigurationHandlers
                .findHandler(filename)
                .orElseThrow(() -> new IllegalStateException("Failed to find configuration handler for filename: " + filename));

        try {
            Node loaded = handler.load(stream);
            Preconditions.checkArgument(loaded instanceof ParentableNode, "Loaded node must be parentable!");
            merge((ParentableNode) loaded);
        } catch (Throwable throwable) {
            throw new IllegalStateException(format("Failed to load file with name: {} with handler: {}", filename, handler.name()), throwable);
        }
    }

    @Override
    public @NotNull String key() {
        return "";
    }
}
