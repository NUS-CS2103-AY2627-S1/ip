package et.storage;

import et.parser.DateTimeParser;
import et.task.Deadline;
import et.task.Event;
import et.task.Task;
import et.task.TaskType;
import et.task.Todo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Saves ET's task list to, and loads it from, a file below the project root.
 *
 * <p>The format has one task per line. Text fields are Base64 encoded so that
 * task descriptions can safely contain the separator used between fields.</p>
 */
public class Storage {
    /** The portable, project-relative location of ET's saved tasks. */
    private static final Path STORAGE_FILE = Path.of("data", "tasks.txt");

    /** Separates the simple fields in a saved task line. */
    private static final String FIELD_SEPARATOR = " | ";

    /** Represents an incomplete task in the storage format. */
    private static final String INCOMPLETE_STATUS = "0";

    /** Represents a completed task in the storage format. */
    private static final String COMPLETE_STATUS = "1";

    /**
     * Loads every valid task in the storage file.
     *
     * <p>A missing file means that ET is being used for the first time, so an
     * empty list is returned. Malformed lines are skipped to let ET recover
     * from a partially corrupted storage file.</p>
     *
     * @return the valid tasks that were read from disk
     * @throws IOException if the storage file cannot be read
     */
    public List<Task> load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        if (Files.notExists(STORAGE_FILE)) {
            return tasks;
        }

        for (String line : Files.readAllLines(STORAGE_FILE, StandardCharsets.UTF_8)) {
            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException e) {
                // Skip only the corrupted line; the remaining saved tasks are still useful.
            }
        }
        return tasks;
    }

    /**
     * Replaces the storage file with the current task list.
     *
     * @param tasks the tasks to save
     * @throws IOException if ET cannot create the data directory or write the file
     */
    public void save(List<Task> tasks) throws IOException {
        Files.createDirectories(STORAGE_FILE.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(STORAGE_FILE, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Task task : tasks) {
                writer.write(serializeTask(task));
                writer.newLine();
            }
        }
    }

    /**
     * Converts one storage line into its matching task object.
     *
     * @param line one line from the storage file
     * @return the reconstructed task
     * @throws IllegalArgumentException if the line is not in ET's storage format
     */
    private Task parseTask(String line) {
        String[] fields = line.split(Pattern.quote(FIELD_SEPARATOR), -1);
        if (fields.length < 3 || !isValidStatus(fields[1])) {
            throw new IllegalArgumentException("Invalid saved task");
        }

        Task task;
        TaskType taskType = TaskType.fromDisplayCode(fields[0]);
        switch (taskType) {
        case TODO:
            requireFieldCount(fields, 3);
            task = new Todo(decode(fields[2]));
            break;
        case DEADLINE:
            requireFieldCount(fields, 4);
            DateTimeParser.ParsedDateTime deadlineDate = DateTimeParser.parseStored(decode(fields[3]));
            task = new Deadline(decode(fields[2]), deadlineDate.value(), deadlineDate.hasTime());
            break;
        case EVENT:
            requireFieldCount(fields, 5);
            DateTimeParser.ParsedDateTime startDate = DateTimeParser.parseStored(decode(fields[3]));
            DateTimeParser.ParsedDateTime endDate = DateTimeParser.parseStored(decode(fields[4]));
            task = new Event(decode(fields[2]), startDate.value(), startDate.hasTime(),
                    endDate.value(), endDate.hasTime());
            break;
        default:
            throw new IllegalArgumentException("Unknown saved task type");
        }

        if (fields[1].equals(COMPLETE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns whether a stored status is one of ET's recognized values.
     *
     * @param status the status field to check
     * @return {@code true} if the status represents a complete or incomplete task
     */
    private boolean isValidStatus(String status) {
        return status.equals(COMPLETE_STATUS) || status.equals(INCOMPLETE_STATUS);
    }

    /**
     * Verifies that a saved line contains exactly the required number of fields.
     *
     * @param fields the fields from a saved line
     * @param expectedCount the required number of fields
     * @throws IllegalArgumentException if the field count does not match
     */
    private void requireFieldCount(String[] fields, int expectedCount) {
        if (fields.length != expectedCount) {
            throw new IllegalArgumentException("Invalid saved task field count");
        }
    }

    /**
     * Serializes one task to its one-line storage representation.
     *
     * @param task the task to serialize
     * @return the corresponding storage line
     */
    private String serializeTask(Task task) {
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        String taskTypeCode = task.getTaskType().getDisplayCode();
        switch (task.getTaskType()) {
        case TODO:
            return String.join(FIELD_SEPARATOR, taskTypeCode, status, encode(task.getDescription()));
        case DEADLINE:
            assert task instanceof Deadline : "Deadline task type must use the Deadline class";
            Deadline deadline = (Deadline) task;
            return String.join(FIELD_SEPARATOR, taskTypeCode, status, encode(deadline.getDescription()),
                    encode(DateTimeParser.formatForStorage(deadline.getBy(), deadline.hasTime())));
        case EVENT:
            assert task instanceof Event : "Event task type must use the Event class";
            Event event = (Event) task;
            return String.join(FIELD_SEPARATOR, taskTypeCode, status, encode(event.getDescription()),
                    encode(DateTimeParser.formatForStorage(event.getFrom(), event.hasStartTime())),
                    encode(DateTimeParser.formatForStorage(event.getTo(), event.hasEndTime())));
        default:
            throw new IllegalArgumentException("Unknown task type");
        }
    }

    /**
     * Encodes a text field so it cannot interfere with the storage separator.
     *
     * @param value the text to encode
     * @return a Base64 representation of the text
     */
    private String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes one text field from the storage file.
     *
     * @param value the Base64 field to decode
     * @return the original text
     * @throws IllegalArgumentException if the field is not valid Base64 text
     */
    private String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
