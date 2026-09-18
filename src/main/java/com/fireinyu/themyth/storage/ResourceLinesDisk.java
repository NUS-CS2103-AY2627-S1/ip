package com.fireinyu.themyth.storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.instrument.UnmodifiableModuleException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

import com.fireinyu.themyth.exceptions.FileAccessException;

/**
 * Driver class for reading and writing to a resource on Disk.
 * Resource is read and written line-wise.
 *
 * @see ResourceLinesDisk
 */
public class ResourceLinesDisk implements LinesDiskDriver {

    private final String resourceName;

    /**
     * Initializes a ResourceLinesDisk for line-wise access to a resource with a given resourceName.
     *
     * @param resourceName resourceName to the file.
     * @see String
     * @see Files
     */
    public ResourceLinesDisk(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getDescriptor() {
        return resourceName;
    }

    /**
     * Reads the lines in the resource associated with this ResourceLinesDisk into a Stream buffer.
     *
     * @return Stream of lines in the resource.
     * @see Stream
     * @see String
     */
    public Stream<String> readLines() {
        try (InputStream is = getClass().getResourceAsStream("/" + resourceName)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.readAllLines().stream();
            }
        } catch (IOException e) {
            throw new FileAccessException(resourceName);
        }
    }

    /**
     * Cannot write to resource files.
     *
     * @throws UnmodifiableModuleException when trying to write to resource file.
     */
    @Override
    public void writeLines(Stream<String> lines) {
        throw new UnmodifiableModuleException();
    }
}
