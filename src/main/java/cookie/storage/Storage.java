package cookie.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import cookie.command.CookieException;
import cookie.task.DateTimeValue;
import cookie.task.Deadline;
import cookie.task.Event;
import cookie.task.Task;
import cookie.task.TaskList;
import cookie.task.TaskType;
import cookie.task.Todo;

/** Handles loading tasks from and saving tasks to Cookie's data file. */
public class Storage {
    /** Position of the task type in a saved record. */
    private static final int TYPE_FIELD_INDEX = 0;

    /** Position of the task status in a saved record. */
    private static final int STATUS_FIELD_INDEX = 1;

    /** Position of the task description in a saved record. */
    private static final int DESCRIPTION_FIELD_INDEX = 2;

    /** Position of date or time details in a saved record. */
    private static final int DETAILS_FIELD_INDEX = 3;

    /** Number of fields in a saved todo record. */
    private static final int TODO_FIELD_COUNT = 3;

    /** Number of fields in a saved deadline or event record. */
    private static final int DATED_TASK_FIELD_COUNT = 4;

    /** Number of temporal values in a saved event's details field. */
    private static final int EVENT_TIME_VALUE_COUNT = 2;

    /** Position of the start value in a saved event's details field. */
    private static final int EVENT_START_VALUE_INDEX = 0;

    /** Position of the end value in a saved event's details field. */
    private static final int EVENT_END_VALUE_INDEX = 1;

    /**
     * Contains recovered tasks and the one-based line numbers of malformed records.
     *
     * @param tasks The tasks successfully loaded from storage.
     * @param malformedLineNumbers The line numbers of records that could not be parsed.
     */
    public record LoadResult(TaskList tasks, List<Integer> malformedLineNumbers) {
        /** Creates a load result with an immutable copy of its malformed line numbers. */
        public LoadResult {
            malformedLineNumbers = List.copyOf(malformedLineNumbers);
        }
    }

    /** The file used to persist Cookie's tasks. */
    private final Path filePath;

    /**
     * Creates storage backed by the specified file path.
     *
     * @param filePath The path of the task file.
     */
    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads valid task records from the data file, or an empty list if it does not exist.
     *
     * @return The loaded tasks and line numbers of malformed records that were skipped.
     * @throws IOException If the data file cannot be read.
     */
    public LoadResult load() throws IOException {
        try {
            TaskList tasks = new TaskList();
            List<Integer> malformedLineNumbers = new ArrayList<>();
            if (!Files.exists(filePath)) {
                return new LoadResult(tasks, malformedLineNumbers);
            }

            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) {
                        continue;
                    }

                    try {
                        Task task = parseTask(line);
                        if (tasks.containsSameDetails(task)) {
                            throw new CookieException("A saved task is duplicated.");
                        }
                        tasks.add(task);
                    } catch (CookieException exception) {
                        malformedLineNumbers.add(lineNumber);
                    }
                }
            }
            return new LoadResult(tasks, malformedLineNumbers);
        } catch (SecurityException exception) {
            throw new IOException("Access to the task file was denied.", exception);
        }
    }

    /**
     * Saves all tasks to the data file using a temporary file and an atomic replacement when supported.
     *
     * @param tasks The tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path temporaryFile = null;
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            temporaryFile = parentDir == null
                    ? Files.createTempFile("cookie-", ".tmp")
                    : Files.createTempFile(parentDir, ".cookie-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFile, StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat());
                    writer.newLine();
                }
            }
            try {
                Files.move(temporaryFile, filePath, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException exception) {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Preserve the original save error for the user.
                }
            }
            if (exception instanceof IOException ioException) {
                throw ioException;
            }
            throw new IOException("Access to the task file was denied.", exception);
        }
    }

    /** Converts one saved task record into a task object. */
    private Task parseTask(String line) throws CookieException {
        String[] fields = line.trim().split("\\s*\\|\\s*", -1);
        if (fields.length < TODO_FIELD_COUNT) {
            throw new CookieException("A saved task record is incomplete.");
        }

        Task task = switch (TaskType.fromCode(fields[TYPE_FIELD_INDEX])) {
            case TODO -> parseTodo(fields);
            case DEADLINE -> parseDeadline(fields);
            case EVENT -> parseEvent(fields);
            default -> throw new CookieException("A saved task record is malformed.");
        };
        restoreStatus(task, fields[STATUS_FIELD_INDEX]);
        return task;
    }

    /** Creates a todo from a validated saved-record shape. */
    private Todo parseTodo(String[] fields) throws CookieException {
        if (fields.length != TODO_FIELD_COUNT
                || isInvalidDescription(fields[DESCRIPTION_FIELD_INDEX])) {
            throw new CookieException("A saved todo record is malformed.");
        }
        return new Todo(fields[DESCRIPTION_FIELD_INDEX]);
    }

    /** Creates a deadline from a validated saved-record shape. */
    private Deadline parseDeadline(String[] fields) throws CookieException {
        if (fields.length != DATED_TASK_FIELD_COUNT
                || isInvalidDescription(fields[DESCRIPTION_FIELD_INDEX])
                || fields[DETAILS_FIELD_INDEX].isBlank()) {
            throw new CookieException("A saved deadline record is malformed.");
        }
        try {
            DateTimeValue deadline = DateTimeValue.parseStorageValue(fields[DETAILS_FIELD_INDEX]);
            return new Deadline(fields[DESCRIPTION_FIELD_INDEX], deadline);
        } catch (DateTimeParseException exception) {
            throw new CookieException("A saved deadline record is malformed.");
        }
    }

    /** Creates an event from a validated saved-record shape. */
    private Event parseEvent(String[] fields) throws CookieException {
        if (fields.length != DATED_TASK_FIELD_COUNT
                || isInvalidDescription(fields[DESCRIPTION_FIELD_INDEX])) {
            throw new CookieException("A saved event record is malformed.");
        }

        String[] timeValues = fields[DETAILS_FIELD_INDEX].split("\\s+to\\s+", EVENT_TIME_VALUE_COUNT);
        if (timeValues.length != EVENT_TIME_VALUE_COUNT
                || timeValues[EVENT_START_VALUE_INDEX].isBlank()
                || timeValues[EVENT_END_VALUE_INDEX].isBlank()) {
            throw new CookieException("A saved event record is malformed.");
        }
        try {
            DateTimeValue start = DateTimeValue.parseStorageValue(timeValues[EVENT_START_VALUE_INDEX]);
            DateTimeValue end = DateTimeValue.parseStorageValue(timeValues[EVENT_END_VALUE_INDEX]);
            return new Event(fields[DESCRIPTION_FIELD_INDEX], start, end);
        } catch (DateTimeParseException | IllegalArgumentException exception) {
            throw new CookieException("A saved event record is malformed.");
        }
    }

    /** Returns whether a saved description is blank or contains unsafe control characters. */
    private boolean isInvalidDescription(String description) {
        return description.isBlank()
                || description.chars().anyMatch(Character::isISOControl);
    }

    /** Restores a saved completion status onto a task. */
    private void restoreStatus(Task task, String status) throws CookieException {
        boolean isStoredAsDone = "Done".equalsIgnoreCase(status);
        if (isStoredAsDone) {
            task.mark();
        } else if (!"Not Done".equalsIgnoreCase(status)) {
            throw new CookieException("A saved task record has an invalid status.");
        }
        assert task.isDone() == isStoredAsDone
                : "A loaded task's state must match its validated stored status";
    }
}
