package janet.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import janet.exception.StorageException;
import janet.task.Deadline;
import janet.task.Event;
import janet.task.Task;
import janet.task.TaskList;
import janet.task.Todo;

/**
 * Loads Janet's tasks from disk and saves task changes to disk.
 */
public class Storage {
    /** Relative path of the file that stores Janet's tasks. */
    private final Path dataFilePath;

    /** User-facing warnings produced while loading saved tasks. */
    private final ArrayList<String> loadWarnings = new ArrayList<>();

    /** Whether saving is disabled to protect an unreadable or malformed data file. */
    private boolean isSaveBlocked;

    /**
     * Creates storage for one task data file.
     *
     * @param filePath relative path of the data file.
     */
    public Storage(String filePath) {
        dataFilePath = Path.of(filePath);
    }

    /**
     * Loads all valid tasks from the data file.
     *
     * @return loaded tasks, or an empty list when no data file exists
     */
    public List<Task> load() {
        loadWarnings.clear();
        isSaveBlocked = false;
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(dataFilePath)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFilePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            isSaveBlocked = true;
            loadWarnings.add("I couldn't read your saved tasks. Saving is disabled to protect the data file. "
                    + "Please check the file and restart Janet.");
            return tasks;
        }

        ArrayList<Integer> malformedLineNumbers = new ArrayList<>();
        for (int index = 0; index < lines.size(); index++) {
            Task task = parseStoredTask(lines.get(index));
            if (task == null) {
                malformedLineNumbers.add(index + 1);
            } else {
                tasks.add(task);
            }
        }
        if (!malformedLineNumbers.isEmpty()) {
            isSaveBlocked = true;
            loadWarnings.add(formatMalformedDataWarning(malformedLineNumbers));
        }
        return tasks;
    }

    /**
     * Returns warnings produced during the most recent load.
     *
     * @return read-only warning list
     */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks tasks to persist.
     * @throws StorageException if saving is blocked or the data file cannot be replaced
     */
    public void save(TaskList tasks) {
        if (isSaveBlocked) {
            throw new StorageException("I can't save changes while the saved-data warning is unresolved. "
                    + "Fix the data file and restart Janet.");
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks.getTasks()) {
            lines.add(formatStoredTask(task));
        }

        Path temporaryFilePath = null;
        try {
            Path absoluteDataFilePath = dataFilePath.toAbsolutePath();
            Path parentPath = absoluteDataFilePath.getParent();
            assert parentPath != null : "An absolute data-file path must have a parent directory";

            Files.createDirectories(parentPath);
            temporaryFilePath = Files.createTempFile(parentPath, "janet-", ".tmp");
            Files.write(temporaryFilePath, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFilePath, absoluteDataFilePath);
        } catch (IOException exception) {
            if (temporaryFilePath != null) {
                try {
                    Files.deleteIfExists(temporaryFilePath);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }
            throw new StorageException("I couldn't save your tasks, so that change was reversed. "
                    + "Please check that Janet can write to its data folder.", exception);
        }
    }

    /**
     * Replaces the data file atomically when the file system supports it.
     *
     * @param temporaryFilePath complete temporary file.
     * @param absoluteDataFilePath destination data file.
     * @throws IOException if neither replacement method succeeds
     */
    private void replaceDataFile(Path temporaryFilePath, Path absoluteDataFilePath) throws IOException {
        try {
            Files.move(temporaryFilePath, absoluteDataFilePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFilePath, absoluteDataFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Creates a task from one tab-separated line in the data file.
     *
     * @param line a line read from the data file.
     * @return a reconstructed task, or {@code null} when the line is malformed
     */
    private Task parseStoredTask(String line) {
        String[] parts = line.split("\\t", -1);
        if (parts.length < 3) {
            return null;
        }

        try {
            Task task;
            if (parts[0].equals("T") && parts.length == 3 && !parts[2].isBlank()) {
                task = new Todo(parts[2]);
            } else if (parts[0].equals("D") && parts.length == 4
                    && !parts[2].isBlank() && !parts[3].isBlank()) {
                task = new Deadline(parts[2], LocalDate.parse(parts[3]));
            } else if (parts[0].equals("E") && parts.length == 5
                    && !parts[2].isBlank() && !parts[3].isBlank() && !parts[4].isBlank()) {
                task = new Event(parts[2], parts[3], parts[4]);
            } else {
                return null;
            }

            if (parts[1].equals("1")) {
                task.markAsDone();
            } else if (!parts[1].equals("0")) {
                return null;
            }
            return task;
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Converts one task to a tab-separated data-file line.
     *
     * @param task task to persist.
     * @return a data-file line representing the task
     */
    private String formatStoredTask(Task task) {
        assert task != null : "Janet's task list must contain only valid tasks";

        String completionStatus = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return String.join("\t", "T", completionStatus, task.getRawDescription());
        }
        if (task instanceof Deadline deadline) {
            return String.join("\t", "D", completionStatus, task.getRawDescription(),
                    deadline.getDeadline().toString());
        }
        if (task instanceof Event event) {
            return String.join("\t", "E", completionStatus, task.getRawDescription(), event.getStart(),
                    event.getEnd());
        }
        throw new IllegalArgumentException("Cannot save an unknown task type.");
    }

    /**
     * Describes malformed saved-task lines and how Janet protects the source file.
     *
     * @param lineNumbers one-based malformed line numbers.
     * @return user-facing warning
     */
    private String formatMalformedDataWarning(List<Integer> lineNumbers) {
        String noun = lineNumbers.size() == 1 ? "line" : "lines";
        String numbers = lineNumbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return "I ignored malformed saved-task " + noun + " " + numbers
                + ". Saving is disabled to protect the data file. Fix the file and restart Janet.";
    }
}
