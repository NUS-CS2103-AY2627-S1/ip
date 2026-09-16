package arin.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import arin.task.Task;

/**
 * Loads and safely saves Arin's task list in a text file.
 */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "arin.txt");

    private final Path filePath;

    /**
     * Creates storage that saves tasks in the project's data directory.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage that saves tasks at a specified path.
     * This constructor makes the writing behavior straightforward to test.
     *
     * @param filePath Path of the task file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks in their saved order, treating a missing file as a first run.
     *
     * @return The saved tasks, including their completion states.
     * @throws IOException If the file cannot be read or contains an invalid record.
     */
    public List<Task> loadTasks() throws IOException {
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (NoSuchFileException e) {
            return new ArrayList<>();
        }
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            try {
                tasks.add(TaskFileParser.parse(lines.get(i)));
            } catch (IllegalArgumentException e) {
                throw new IOException("Cannot load " + filePath + " at line " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
        return tasks;
    }

    /**
     * Saves all tasks by replacing the file only after a complete temporary write.
     * Requires atomic replacement so a failed save cannot truncate existing data.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the task file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Path target = filePath.toAbsolutePath();
        Path parentDirectory = target.getParent();
        Files.createDirectories(parentDirectory);
        List<String> lines = tasks.stream()
                .map(Task::storageToString)
                .toList();
        Path temporaryFile = Files.createTempFile(parentDirectory, "arin-", ".tmp");
        try {
            Files.write(temporaryFile, lines);
            Files.move(temporaryFile, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
