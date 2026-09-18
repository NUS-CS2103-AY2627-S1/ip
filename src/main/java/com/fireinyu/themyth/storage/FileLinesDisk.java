package com.fireinyu.themyth.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.fireinyu.themyth.exceptions.FileAccessException;

/**
 * Driver class for reading and writing to a file on Disk.
 * File is read and written line-wise.
 *
 * @see FileLinesDisk
 */
public class FileLinesDisk implements LinesDiskDriver {

    private Path path;

    /**
     * Initializes a FileLinesDisk for line-wise access to a file at a given path.
     *
     * @param path path to the file.
     * @see Path
     * @see Files
     */
    public FileLinesDisk(Path path) {
        this.path = path;
    }

    @Override
    public String getDescriptor() {
        return path.toString();
    }

    /**
     * Reads the lines in the file associated with this FileLinesDisk into a Stream buffer.
     *
     * @return Stream of lines in the file.
     * @see Stream
     * @see String
     */
    public Stream<String> readLines() {
        try {
            createFileIfMissing();
            return Files.readAllLines(path).stream();
        } catch (IOException e) {
            throw new FileAccessException(path.toString());
        }
    }

    /**
     * Writes all lines from a String Stream into the file associated with this FileLinesDisk.
     *
     * @param lines Stream of lines to be written.
     * @see Stream
     * @see String
     */
    public void writeLines(Stream<String> lines) {
        try {
            createFileIfMissing();
            Files.write(path, lines.toList());
        } catch (IOException e) {
            throw new FileAccessException(path.toString());
        }
    }

    /**
     * Creates the backing file and its parent directories if the file does not exist.
     *
     * @throws IOException If the directories or file cannot be created.
     */
    private void createFileIfMissing() throws IOException {
        if (!path.toFile().exists()) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
    }
}
