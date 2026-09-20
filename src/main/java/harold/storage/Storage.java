package harold.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import harold.task.Deadline;
import harold.task.Event;
import harold.task.Task;
import harold.task.TaskDate;
import harold.task.TaskList;
import harold.task.Todo;

/**
 * Loads and saves Harold's tasks using an OS-independent relative path.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = "\t";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";

    private static final int TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_START_DATE_INDEX = 3;
    private static final int EVENT_END_DATE_INDEX = 4;

    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path filePath;

    /**
     * Creates storage backed by the given file path.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all valid task records, skipping malformed records without failing startup.
     *
     * @return Loaded tasks and the number of malformed records skipped.
     * @throws IOException If the data file cannot be read.
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        int skippedLineCount = 0;

        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, skippedLineCount);
        }

        for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException | DateTimeException e) {
                skippedLineCount++;
            }
        }
        return new LoadResult(tasks, skippedLineCount);
    }

    /**
     * Saves the current tasks, creating the parent directory when necessary.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the data file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        List<String> lines = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            lines.add(formatTask(tasks.get(i)));
        }
        Path temporaryDirectory = parentDirectory == null ? Path.of(".") : parentDirectory;
        Path temporaryFile = Files.createTempFile(temporaryDirectory, ".harold-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Replaces the data file atomically where the file system supports it.
     */
    private void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one persisted record into its corresponding task.
     *
     * @param line Persisted task record.
     * @return Task represented by the record.
     * @throws IllegalArgumentException If the record is malformed.
     */
    private static Task parseTask(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < TODO_FIELD_COUNT) {
            throw new IllegalArgumentException("Task record has too few fields");
        }

        boolean isDone = parseDoneStatus(fields[STATUS_INDEX]);
        String description = decode(fields[DESCRIPTION_INDEX]);
        Task task = switch (fields[TYPE_INDEX]) {
            case TODO_TYPE -> {
                requireFieldCount(fields, TODO_FIELD_COUNT);
                yield new Todo(description);
            }
            case DEADLINE_TYPE -> {
                requireFieldCount(fields, DEADLINE_FIELD_COUNT);
                yield new Deadline(
                        description,
                        TaskDate.parse(decode(fields[DEADLINE_DATE_INDEX]))
                );
            }
            case EVENT_TYPE -> {
                requireFieldCount(fields, EVENT_FIELD_COUNT);
                yield new Event(
                        description,
                        TaskDate.parse(decode(fields[EVENT_START_DATE_INDEX])),
                        TaskDate.parse(decode(fields[EVENT_END_DATE_INDEX]))
                );
            }
            default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (description.isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Converts a persisted status field into its boolean representation.
     *
     * @param status Persisted task status.
     * @return Whether the task is marked as done.
     * @throws IllegalArgumentException If the status is not recognized.
     */
    private static boolean parseDoneStatus(String status) {
        return switch (status) {
            case DONE_STATUS -> true;
            case NOT_DONE_STATUS -> false;
            default -> throw new IllegalArgumentException("Task status must be 0 or 1");
        };
    }

    /**
     * Converts a task into the record format used in the data file.
     *
     * @param task Task to persist.
     * @return Serialized task record.
     */
    private static String formatTask(Task task) {
        String status = task.isDone() ? DONE_STATUS : NOT_DONE_STATUS;
        String commonFields = task.getTypeIcon() + FIELD_SEPARATOR + status
                + FIELD_SEPARATOR + encode(task.getDescription());

        if (task instanceof Deadline deadline) {
            return commonFields + FIELD_SEPARATOR + encode(deadline.getBy().toString());
        } else if (task instanceof Event event) {
            return commonFields + FIELD_SEPARATOR + encode(event.getFrom().toString())
                    + FIELD_SEPARATOR + encode(event.getTo().toString());
        }
        return commonFields;
    }

    /**
     * Encodes an arbitrary task field safely for storage.
     *
     * @param value Field value to encode.
     * @return Base64 representation of the field value.
     */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a task field from its stored representation.
     *
     * @param value Base64 field value.
     * @return Decoded text.
     * @throws IllegalArgumentException If the value is not valid Base64.
     */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    /**
     * Verifies that a persisted task record has the required number of fields.
     *
     * @param fields Fields parsed from a task record.
     * @param expectedCount Required number of fields.
     * @throws IllegalArgumentException If the number of fields is unexpected.
     */
    private static void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Unexpected number of task fields");
        }
    }

    /**
     * Contains successfully loaded tasks and the number of malformed records skipped.
     *
     * @param tasks Successfully loaded tasks.
     * @param skippedLineCount Number of malformed records skipped.
     */
    public record LoadResult(List<Task> tasks, int skippedLineCount) {
    }
}
