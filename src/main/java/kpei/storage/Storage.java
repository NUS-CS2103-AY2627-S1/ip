package kpei.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import kpei.datatypes.Task;
import kpei.datatypes.TaskList;
import kpei.exceptions.BertException;

/**
 * Handles persistent storage of {@link TaskList} tasks to and from a local file.
 * Uses a human-readable pipe-delimited plain text format.
 */
public class Storage {
    private final Path filePath;
    private final StorageParser storageParser;

    /**
     * Constructs a {@code Storage} handler with a custom file path.
     *
     * @param filePath Relative or absolute path to the data storage file.
     */
    public Storage(String filePath) {
        this.filePath = parseFilePath(filePath);
        storageParser = new StorageParser();
    }

    /**
     * Converts a storage file path string into a valid {@link Path}.
     *
     * @param filePath The storage file path string.
     * @return The parsed storage file path.
     * @throws IllegalArgumentException If the path string has invalid syntax.
     */
    private Path parseFilePath(String filePath) {
        try {
            return Path.of(filePath);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Invalid storage file path: " + filePath, e);
        }
    }

    /**
     * Loads tasks from the save file into the provided {@link TaskList}.
     * If the file does not exist, no action is taken and the list remains as-is.
     * Corrupted or unrecognized lines are safely ignored.
     *
     * @param taskList The list to populate with loaded tasks.
     * @throws BertException If an error occurs while reading or parsing the data file.
     */
    public void load(TaskList taskList) throws BertException {
        if (!Files.exists(filePath)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(filePath)) {
                if (storageParser.isStoredTask(line)) {
                    taskList.add(storageParser.parseStoredTask(line));
                }
            }
        } catch (IOException e) {
            throw new BertException("Warning: Unable to load data from " + filePath + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Saves all tasks from the specified {@link TaskList} into the storage file.
     * Creates any missing parent directories automatically.
     *
     * @param taskList The list containing tasks to save.
     * @throws BertException If an I/O error occurs while saving data.
     */
    public void save(TaskList taskList) throws BertException {
        try {
            Files.createDirectories(filePath.toAbsolutePath().getParent());

            List<String> lines = new ArrayList<>();
            for (Task task : taskList.getTasks()) {
                lines.add(task.toFileFormat());
            }
            assert lines.size() == taskList.size()
                    : "Each task should produce exactly one storage line";

            Files.write(filePath, lines);
        } catch (IOException e) {
            throw new BertException("Warning: Unable to save data to " + filePath + " (" + e.getMessage() + ")", e);
        }
    }

    /**
     * Returns the file name of the storage file path.
     *
     * @return The storage file name.
     */
    public String getFileName() {
        return filePath.getFileName().toString();
    }
}
