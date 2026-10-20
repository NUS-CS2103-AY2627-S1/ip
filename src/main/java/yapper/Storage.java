package yapper;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to disk and restores them when Yapper starts again.
 */
public class Storage {
    /** File from which tasks are loaded and to which they are saved. */
    private final Path filePath;

    /**
     * Creates a storage manager for the given text file.
     *
     * @param filePath Path of the file used to persist tasks.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all saved tasks. A missing file represents a new, empty task list.
     *
     * @return Tasks reconstructed from the storage file.
     * @throws IOException If the file exists but cannot be read.
     * @throws YapperException If a saved line has an invalid format.
     */
    public ArrayList<Task> load() throws IOException, YapperException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            if (!lines.get(i).isBlank()) {
                tasks.add(parseTask(lines.get(i), i + 1));
            }
        }
        return tasks;
    }

    /**
     * Writes tasks to a temporary file before replacing the save, creating parent directories as needed.
     * Uses atomic replacement when supported to protect the previous save from interrupted writes.
     *
     * @param tasks Current tasks to persist.
     * @throws IOException If the directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parentDirectory = filePath.toAbsolutePath().getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = tasks.stream()
                .map(Task::toDataString)
                .toList();
        Path temporaryFile = Files.createTempFile(parentDirectory, "yapper-", ".tmp");
        try {
            Files.write(temporaryFile, lines);
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Reconstructs one task from its pipe-separated storage representation.
     *
     * @param line Saved task record.
     * @param lineNumber One-based file line number for error reporting.
     * @return Task with its saved completion state restored.
     * @throws YapperException If the type, field count, completion state, or date is invalid.
     */
    private Task parseTask(String line, int lineNumber) throws YapperException {
        String[] fields = line.split(" \\| ", -1);
        try {
            Task task = switch (fields[0]) {
                case "T" -> {
                    requireFieldCount(fields, 3);
                    yield new Todo(fields[2]);
                }
                case "D" -> {
                    requireFieldCount(fields, 4);
                    yield new Deadline(fields[2], TaskDateTime.parse(fields[3]));
                }
                case "E" -> {
                    requireFieldCount(fields, 5);
                    java.time.LocalDateTime start = TaskDateTime.parse(fields[3]);
                    java.time.LocalDateTime end = TaskDateTime.parse(fields[4]);
                    if (end.isBefore(start)) {
                        throw new IllegalArgumentException("event end precedes start");
                    }
                    yield new Event(fields[2], start, end);
                }
                default -> throw new IllegalArgumentException("unknown task type");
            };

            if ("1".equals(fields[1])) {
                task.markAsDone();
            } else if (!"0".equals(fields[1])) {
                throw new IllegalArgumentException("invalid completion status");
            }
            return task;
        } catch (IllegalArgumentException exception) {
            throw new YapperException("Saved task on line " + lineNumber
                    + " is invalid (" + exception.getMessage() + ").");
        }
    }

    /**
     * Ensures a stored task contains exactly the fields required by its type.
     *
     * @param fields Fields in a saved record.
     * @param expectedCount Required number of fields.
     * @throws IllegalArgumentException If the number of fields differs.
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("wrong number of fields");
        }
        if (fields[2].isBlank()) {
            throw new IllegalArgumentException("empty task description");
        }
    }
}
