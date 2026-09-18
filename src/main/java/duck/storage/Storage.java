package duck.storage;

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
import java.util.Objects;

import duck.DuckException;
import duck.task.Deadline;
import duck.task.DeadlineDateParser;
import duck.task.Event;
import duck.task.Task;
import duck.task.TaskType;
import duck.task.Todo;

/**
 * Loads tasks from and saves tasks to a plain-text data file.
 */
public class Storage {
    /** Error included in the line-specific message for invalid saved dates. */
    private static final String INVALID_SAVED_DATE_MESSAGE =
            "the deadline date must be a valid yyyy-MM-dd date.";

    /** Original path text used in user-facing storage errors. */
    private final String filePathText;

    /** Path of the task data file. */
    private final Path dataFilePath;

    /** Whether saving is blocked to protect a data file that failed to load. */
    private boolean isSaveBlocked;

    /**
     * Creates storage backed by the given file.
     *
     * @param filePath Path to the task data file.
     */
    public Storage(String filePath) {
        this.filePathText = Objects.requireNonNull(filePath, "Storage file path cannot be null.");
        this.dataFilePath = Path.of(filePath);
        this.isSaveBlocked = false;
    }

    /**
     * Writes the current task list to disk, replacing the previous contents atomically
     * where the file system supports it.
     *
     * @param tasks Tasks to save.
     * @throws DuckException if a previous load failed or the task list cannot be saved
     */
    public void save(List<Task> tasks) throws DuckException {
        if (this.isSaveBlocked) {
            throw new DuckException("Tasks cannot be changed because Duck could not load "
                    + "the saved task file. Repair or move " + this.filePathText
                    + ", then restart Duck.");
        }

        List<String> taskLines = tasks.stream()
                .map(Task::formatForStorage)
                .toList();

        Path temporaryFile = null;
        try {
            Path parentDirectory = this.dataFilePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
            temporaryFile = Files.createTempFile(temporaryDirectory, "duck-", ".tmp");
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);
            try {
                Files.move(temporaryFile, this.dataFilePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, this.dataFilePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new DuckException("Unable to save tasks to " + this.filePathText + ".");
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error is more useful than a temporary-file cleanup error.
                }
            }
        }
    }

    /**
     * Reads saved tasks from disk. A missing file represents an empty task list. A failed
     * load blocks future saves for this storage instance.
     *
     * @return tasks reconstructed from the data file
     * @throws DuckException if the data file cannot be read or contains an invalid task
     */
    public List<Task> load() throws DuckException {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try {
            if (Files.notExists(this.dataFilePath)) {
                return loadedTasks;
            }

            List<String> taskLines = Files.readAllLines(this.dataFilePath, StandardCharsets.UTF_8);
            for (int i = 0; i < taskLines.size(); i++) {
                String taskLine = taskLines.get(i);
                if (taskLine.isBlank()) {
                    continue;
                }
                try {
                    loadedTasks.add(parseTask(taskLine));
                } catch (DuckException e) {
                    throw new DuckException("Unable to load tasks from line " + (i + 1)
                            + ": " + e.getMessage());
                }
            }
            return loadedTasks;
        } catch (DuckException e) {
            this.isSaveBlocked = true;
            throw e;
        } catch (IOException | SecurityException e) {
            this.isSaveBlocked = true;
            throw new DuckException("Unable to read tasks from " + this.filePathText + ".");
        }
    }

    /**
     * Reconstructs one task from its plain-text storage representation.
     *
     * @param taskLine One line from the data file.
     * @return reconstructed task
     * @throws DuckException if the line does not match the expected storage format
     */
    private Task parseTask(String taskLine) throws DuckException {
        List<String> fields = splitFileFields(taskLine);
        if (fields.size() < 3) {
            throw new DuckException("the record has too few fields.");
        }

        String savedStatus = fields.get(1);
        validateSavedStatus(savedStatus);

        Task task = createTask(fields);
        applySavedStatus(task, savedStatus);
        return task;
    }

    /** Validates the completion-status field shared by every saved task. */
    private void validateSavedStatus(String savedStatus) throws DuckException {
        if (!"0".equals(savedStatus) && !"1".equals(savedStatus)) {
            throw new DuckException("the status must be 0 or 1.");
        }
    }

    /** Creates the concrete task represented by the saved task-type code. */
    private Task createTask(List<String> fields) throws DuckException {
        String taskTypeCode = fields.get(0);
        if (TaskType.TODO.getFileCode().equals(taskTypeCode)) {
            return createTodoTask(fields);
        }
        if (TaskType.DEADLINE.getFileCode().equals(taskTypeCode)) {
            return createDeadlineTask(fields);
        }
        if (TaskType.EVENT.getFileCode().equals(taskTypeCode)) {
            return createEventTask(fields);
        }
        throw new DuckException("the task type is not recognized.");
    }

    /** Creates a todo from a saved record after validating its type-specific fields. */
    private Task createTodoTask(List<String> fields) throws DuckException {
        requireExactFieldCount(fields, 3);
        requireNonBlank(fields.get(2), "todo description");
        return new Todo(fields.get(2));
    }

    /** Creates a deadline from a saved record after validating its type-specific fields. */
    private Task createDeadlineTask(List<String> fields) throws DuckException {
        requireExactFieldCount(fields, 4);
        requireNonBlank(fields.get(2), "deadline description");
        requireNonBlank(fields.get(3), "deadline date");
        return new Deadline(fields.get(2), parseDeadlineDate(fields.get(3)));
    }

    /** Creates an event from a saved record after validating its type-specific fields. */
    private Task createEventTask(List<String> fields) throws DuckException {
        requireExactFieldCount(fields, 5);
        requireNonBlank(fields.get(2), "event description");
        requireNonBlank(fields.get(3), "event start time");
        requireNonBlank(fields.get(4), "event end time");
        return new Event(fields.get(2), fields.get(3), fields.get(4));
    }

    /** Validates that a saved task type has exactly its required number of fields. */
    private void requireExactFieldCount(List<String> fields, int expectedCount) throws DuckException {
        if (fields.size() != expectedCount) {
            throw new DuckException("the task type has the wrong number of fields.");
        }
    }

    /** Applies a saved completion status to a reconstructed task. */
    private void applySavedStatus(Task task, String savedStatus) {
        if ("1".equals(savedStatus)) {
            task.markAsDone();
        }
        assert task.isDone() == "1".equals(savedStatus)
                : "Loaded task status must match its saved status.";
    }

    /**
     * Splits a stored line while decoding escaped backslashes and pipe characters.
     *
     * @param taskLine Line to split.
     * @return decoded storage fields
     */
    private ArrayList<String> splitFileFields(String taskLine) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < taskLine.length(); i++) {
            char currentCharacter = taskLine.charAt(i);
            if (currentCharacter == '\\' && i + 1 < taskLine.length()) {
                char escapedCharacter = taskLine.charAt(i + 1);
                if (escapedCharacter == '\\' || escapedCharacter == '|') {
                    currentField.append(escapedCharacter);
                    i++;
                    continue;
                }
            }
            if (taskLine.startsWith(" | ", i)) {
                fields.add(currentField.toString());
                currentField.setLength(0);
                i += 2;
            } else {
                currentField.append(currentCharacter);
            }
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Rejects required storage fields that contain no visible text.
     *
     * @param value Field value.
     * @param fieldName User-facing name of the field.
     * @throws DuckException if the value is blank
     */
    private void requireNonBlank(String value, String fieldName) throws DuckException {
        if (value.isBlank()) {
            throw new DuckException("the " + fieldName + " cannot be empty.");
        }
    }

    /** Parses a deadline date, translating validation failures into a storage error. */
    private LocalDate parseDeadlineDate(String dateText) throws DuckException {
        try {
            return DeadlineDateParser.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new DuckException(INVALID_SAVED_DATE_MESSAGE);
        }
    }
}
