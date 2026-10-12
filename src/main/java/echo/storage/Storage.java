package echo.storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import echo.EchoException;
import echo.task.DeadlineTask;
import echo.task.EventTask;
import echo.task.Task;
import echo.task.TodoTask;

/**
 * Loads tasks from and saves tasks to E.C.H.O.'s persistence file.
 */
public class Storage {
    /** File used by the default application instance. */
    private static final Path DEFAULT_FILE_PATH = Paths.get(".", "data", "echo.txt");

    /** File used to persist tasks. */
    private final Path filePath;

    /** Tracks whether unparseable lines were encountered during loading. */
    private boolean hasCorruptedEntries;

    /**
     * Creates a new Storage instance using the application's default persistence file.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates a new Storage instance using a given persistence filepath.
     *
     * @param filePath file used to load and save tasks.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
        this.hasCorruptedEntries = false;
    }

    /**
     * Loads all valid tasks from the persistence file.
     *
     * @return tasks loaded from the file, in insertion order.
     * @throws UncheckedIOException if the file cannot be read.
     */
    public List<Task> load() {
        if (!Files.exists(filePath)) {
            return List.of();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task == null) {
                    hasCorruptedEntries = true;
                } else {
                    tasks.add(task);
                }
            }
            return List.copyOf(tasks);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to load tasks from " + filePath, exception);
        }
    }

    /**
     * Saves all tasks, replacing the previous contents of the persistence file.
     *
     * @param tasks tasks to save.
     * @throws UncheckedIOException if the file cannot be written.
     */
    public void save(List<Task> tasks) {
        if (hasCorruptedEntries && Files.exists(filePath)) {
            try {
                Path backupPath = filePath.resolveSibling(filePath.getFileName().toString() + ".corrupted.bak");
                Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
                hasCorruptedEntries = false;
            } catch (IOException ignored) {
                // If backup fails, proceed with standard save attempt
            }
        }

        List<String> lines = tasks.stream()
                .map(Task::toFileFormat)
                .toList();

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to save tasks to " + filePath, exception);
        }
    }

    /**
     * Returns whether any corrupted entries were skipped during loading.
     *
     * @return true if corrupted lines were detected; false otherwise.
     */
    public boolean hasCorruptedEntries() {
        return hasCorruptedEntries;
    }

    /**
     * Returns the file path used for task storage.
     *
     * @return file path.
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Creates one task from a saved line in the storage file.
     *
     * @param line String entry representing one task.
     * @return {@link Task} object, or {@code null} if the line cannot be parsed.
     */
    private Task parseTask(String line) {
        String[] fields = line.split("\\s*\\|\\s*", -1);
        if (fields.length < 3) {
            return null;
        }

        String description = fields[2];
        if (description.isBlank()) {
            return null;
        }

        Task task;
        try {
            switch (fields[0]) {
                case "T" -> task = new TodoTask(description);
                case "D" -> {
                    if (fields.length < 4) {
                        return null;
                    }
                    task = new DeadlineTask(description, fields[3]);
                }
                case "E" -> {
                    if (fields.length < 5) {
                        return null;
                    }
                    task = new EventTask(description, fields[3], fields[4]);
                }
                default -> {
                    return null;
                }
            }
        } catch (DateTimeParseException | EchoException exception) {
            return null;
        }

        if ("1".equals(fields[1])) {
            task.mark();
        } else if (!"0".equals(fields[1])) {
            return null;
        }
        return task;
    }
}
