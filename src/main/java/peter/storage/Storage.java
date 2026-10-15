package peter.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import peter.exception.PeterException;
import peter.task.Deadline;
import peter.task.Event;
import peter.task.Task;
import peter.task.TaskList;
import peter.task.Todo;

/**
 * Handles validated, failure-aware loading and saving of tasks.
 */
public class Storage {
    private final String filePath;

    /**
     * Constructs a Storage object with a target path.
     *
     * @param filePath Relative or absolute path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads valid tasks while counting damaged or duplicate entries that were skipped.
     * A missing file is treated as a new, empty task list.
     *
     * @return Loaded tasks and the number of skipped entries.
     * @throws PeterException If the path is invalid or cannot be read.
     */
    public LoadResult load() throws PeterException {
        Path path = getPath();
        if (Files.notExists(path)) {
            return new LoadResult(List.of(), 0);
        }
        if (!Files.isRegularFile(path)) {
            throw new PeterException("The saved route isn't a regular file: " + filePath);
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            throw new PeterException("I couldn't read the saved route. Check access to: " + filePath);
        }

        List<Task> tasks = new ArrayList<>();
        int skippedEntries = 0;
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                Task task = parseTaskFromFile(line);
                boolean isDuplicate = tasks.stream().anyMatch(existing -> existing.hasSameDetails(task));
                if (isDuplicate) {
                    skippedEntries++;
                } else {
                    tasks.add(task);
                }
            } catch (PeterException e) {
                skippedEntries++;
            }
        }
        return new LoadResult(tasks, skippedEntries);
    }

    /**
     * Saves all tasks through a temporary file before replacing the data file.
     * This prevents a partial write from corrupting the previous saved route.
     *
     * @param taskList Tasks to persist.
     * @throws PeterException If directories cannot be created or the file cannot be replaced.
     */
    public void save(TaskList taskList) throws PeterException {
        Path path = getPath().toAbsolutePath();
        Path parent = path.getParent();
        Path temporaryFile = null;
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            temporaryFile = Files.createTempFile(parent, ".peter-", ".tmp");
            List<String> lines = taskList.getTasks().stream()
                    .map(Task::toFileFormat)
                    .toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceFile(temporaryFile, path);
            temporaryFile = null;
        } catch (IOException | SecurityException e) {
            throw new PeterException("I updated the route in this session, but couldn't save it. "
                    + "Check write access to: " + filePath);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException e) {
                    // The operating system can clean up an abandoned temporary file later.
                }
            }
        }
    }

    private Path getPath() throws PeterException {
        if (filePath == null || filePath.isBlank()) {
            throw new PeterException("Peter needs a valid path for the saved route.");
        }
        try {
            return Path.of(filePath);
        } catch (InvalidPathException e) {
            throw new PeterException("The saved-route path isn't valid: " + filePath);
        }
    }

    private void replaceFile(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private Task parseTaskFromFile(String line) throws PeterException {
        String[] parts = line.split("\\s*\\|\\s*", -1);
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new PeterException("Damaged task entry");
        }
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            throw new PeterException("Invalid completion status");
        }

        Task task = switch (parts[0]) {
            case "T" -> {
                requireFieldCount(parts, 3);
                yield new Todo(parts[2]);
            }
            case "D" -> {
                requireFieldCount(parts, 4);
                yield new Deadline(parts[2], parts[3]);
            }
            case "E" -> {
                requireFieldCount(parts, 5);
                yield new Event(parts[2], parts[3], parts[4]);
            }
            default -> throw new PeterException("Unknown task type");
        };
        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private void requireFieldCount(String[] fields, int expectedCount) throws PeterException {
        if (fields.length != expectedCount) {
            throw new PeterException("Unexpected number of task fields");
        }
    }

    /**
     * Result of reading the saved route.
     *
     * @param tasks Valid tasks recovered from storage.
     * @param skippedEntries Number of damaged or duplicate entries ignored.
     */
    public record LoadResult(List<Task> tasks, int skippedEntries) {
        public LoadResult {
            tasks = List.copyOf(tasks);
        }
    }
}
