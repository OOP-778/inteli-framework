package com.oop.inteliframework.config.handler;

import com.oop.inteliframework.config.node.Node;
import lombok.NonNull;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Used to load/save streams to nodes/streams
 */
public interface ConfigurationHandler {
    /**
     * Load file into a node
     *
     * @param stream stream that you're trying to load
     * @return node that it loaded
     */
    Node load(@NonNull InputStream stream);

    /**
     * Save node into a stream
     *
     * @param node   node that you're trying to save
     * @param stream stream that node is gonna be written into
     */
    void save(@NonNull Node node, @NonNull OutputStream stream);

    /**
     * If the handler accepts specified filename
     *
     * @param filename the name of the file
     * @return either if it accepts the filename or not
     */
    boolean accepts(String filename);

    /**
     * Get the name of the handler
     *
     * @return the name of the handler
     */
    String name();
}
