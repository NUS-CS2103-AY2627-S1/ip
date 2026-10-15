package charlie.storage;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import charlie.exception.CharlieException;
import charlie.task.Deadline;
import charlie.task.Event;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.task.Todo;

/**
 * Loads tasks from a save file and writes the current task list back to it.
 */
public class Storage {
    /** Location of Charlie's save file. */
    private final Path filePath;

    /** Prevents a failed load from being replaced by an empty task list. */
    private boolean hasLoadFailed;

    /**
     * Creates storage that uses the specified save file.
     *
     * @param filePath Path to Charlie's save file.
     */
    public Storage(String filePath) {
        try {
            this.filePath = Path.of(filePath);
        } catch (InvalidPathException | NullPointerException e) {
            throw new CharlieException("Invalid save-file path.");
        }
    }

    /**
     * Loads all tasks from the save file.
     * A missing file represents a first-time user with no saved tasks.
     *
     * @return Tasks reconstructed from the save file.
     * @throws CharlieException If the file cannot be read or contains invalid task data.
     */
    public List<Task> load() {
        try {
            if (Files.notExists(filePath)) {
                return new ArrayList<>();
            }

            List<Task> tasks = new ArrayList<>();
            for (String line : Files.readAllLines(filePath)) {
                if (!line.isBlank()) {
                    tasks.add(parseSavedTask(line));
                }
            }
            new TaskList(tasks);
            return tasks;
        } catch (CharlieException e) {
            hasLoadFailed = true;
            throw e;
        } catch (IOException | SecurityException e) {
            hasLoadFailed = true;
            throw new CharlieException("Could not read the saved task file.");
        }
    }

    /**
     * Saves every task using Charlie's line-based file format.
     *
     * @param tasks Tasks to save.
     * @throws CharlieException If the tasks cannot be written to the save file.
     */
    public void save(List<Task> tasks) {
        if (hasLoadFailed) {
            throw new CharlieException("Cannot save tasks because saved tasks could not be loaded.");
        }
        StringBuilder content = new StringBuilder();
        for (Task task : tasks) {
            content.append(task.saveFileFormat()).append(System.lineSeparator());
        }
        Path temporaryFile = null;
        try {
            Path parentDirectory = filePath.toAbsolutePath().getParent();
            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(parentDirectory, "charlie-", ".tmp");
            Files.writeString(temporaryFile, content.toString());
            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new CharlieException("Could not save tasks.");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException e) {
                    // A failed cleanup must not hide the original save result.
                }
            }
        }
    }

    /**
     * Recreates one task from a line in Charlie's save-file format.
     *
     * @param line Saved representation of one task.
     * @return The reconstructed task.
     */
    private Task parseSavedTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        int expectedFieldCount = switch (fields[0]) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new CharlieException("Unknown saved task type: " + fields[0]);
        };
        if (fields.length != expectedFieldCount) {
            throw new CharlieException("Invalid number of fields in saved task.");
        }

        boolean isDone = parseSavedStatus(fields[1]);

        return switch (fields[0]) {
            case "T" -> new Todo(fields[2], isDone);
            case "D" -> parseSavedDeadline(fields, isDone);
            case "E" -> parseSavedEvent(fields, isDone);
            default -> throw new AssertionError("Task type was validated above.");
        };
    }

    /**
     * Converts a saved completion status into its boolean representation.
     *
     * @param status Saved status field.
     * @return True for {@code Done}; false for {@code Not done}.
     * @throws CharlieException If the status is not one of Charlie's supported values.
     */
    private boolean parseSavedStatus(String status) {
        return switch (status) {
            case "Done" -> true;
            case "Not done" -> false;
            default -> throw new CharlieException("Invalid completion status in saved task.");
        };
    }

    /**
     * Reconstructs a deadline from validated save-file fields.
     *
     * @param arguments Saved fields containing the description and due date.
     * @param isDone Whether the reconstructed deadline is completed.
     * @return Deadline reconstructed from the saved fields.
     * @throws CharlieException If the saved deadline contains an invalid date.
     */
    private Deadline parseSavedDeadline(String[] arguments, boolean isDone) {
        String description = arguments[2];
        try {
            LocalDate dueDate = LocalDate.parse(arguments[3]);
            return new Deadline(description, isDone, dueDate);
        } catch (DateTimeParseException e) {
            throw new CharlieException("Deadline must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Reconstructs an event from validated save-file fields.
     *
     * @param arguments Saved fields containing the description, start, and end.
     * @param isDone Whether the reconstructed event is completed.
     * @return Event reconstructed from the saved fields.
     * @throws CharlieException If the saved event contains an invalid date-time.
     */
    private Event parseSavedEvent(String[] arguments, boolean isDone) {
        String description = arguments[2];
        try {
            LocalDateTime startDateTime = LocalDateTime.parse(arguments[3]);
            LocalDateTime endDateTime = LocalDateTime.parse(arguments[4]);
            return new Event(description, isDone, startDateTime, endDateTime);
        } catch (DateTimeParseException e) {
            throw new CharlieException("Saved event contains an invalid date-time.");
        }
    }
}
