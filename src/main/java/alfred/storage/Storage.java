package alfred.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import alfred.task.Task;

/**
 * Reads and writes the task list as a text file on disk.
 */
public class Storage {
    /** Relative path of the save file, from the working directory. */
    private final Path filePath;

    /**
     * Creates storage for the given relative file path.
     *
     * @param filePath Relative path of the save file.
     */
    public Storage(Path filePath) {
        // Alfred always constructs storage with the configured save-file path.
        assert filePath != null : "Save file path should not be null";
        this.filePath = filePath;
    }

    /**
     * Returns tasks loaded from disk.
     * Missing files are treated as an empty list. Corrupted lines are skipped.
     *
     * @return Tasks read from the save file, in file order.
     * @throws IOException If the save file exists but cannot be read.
     */
    public List<Task> load() throws IOException {
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            return new ArrayList<>();
        }
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        return lines.stream()
                .filter(line -> !line.isBlank())
                .map(Task::fromSaveLine)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Writes the given tasks to disk, creating the parent folder if needed.
     *
     * @param tasks Tasks to write, in the order they should be restored.
     * @throws IOException If the file cannot be created or written.
     */
    public void save(List<Task> tasks) throws IOException {
        // TaskList.getAllTasks() always returns a concrete list of stored tasks.
        assert tasks != null : "Task list to save should not be null";
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> lines = tasks.stream()
                .map(task -> {
                    assert task != null : "Cannot save a null task";
                    return task.toSaveFormat();
                })
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }
}
