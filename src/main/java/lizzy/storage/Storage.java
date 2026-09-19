package lizzy.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import lizzy.exception.LizzyException;
import lizzy.task.Deadline;
import lizzy.task.Event;
import lizzy.task.Task;
import lizzy.task.Todo;
import lizzy.task.WithinPeriodTask;

/**
 * Saves Lizzy's task list in a text file on disk.
 */
public class Storage {
    private static final String FIELD_SEPARATOR = " | ";
    private static final String FIELD_SEPARATOR_PATTERN = "\\s*\\|\\s*";
    private static final String TODO_RECORD_TYPE = "T";
    private static final String DEADLINE_RECORD_TYPE = "D";
    private static final String EVENT_RECORD_TYPE = "E";
    private static final String WITHIN_PERIOD_RECORD_TYPE = "W";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    /**
     * The relative or user-configured path of the task data file.
     */
    private final Path filePath;

    /**
     * Creates storage that writes to the given path.
     *
     * @param filePath the path of the task data file
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from disk, or returns an empty list when the file does not yet exist.
     *
     * @return the tasks stored in the data file
     * @throws LizzyException if the file cannot be read or contains an invalid record
     */
    public List<Task> load() throws LizzyException {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                if (!lines.get(i).isBlank()) {
                    tasks.add(deserialize(lines.get(i), i + 1));
                }
            }
            return tasks;
        } catch (IOException | SecurityException exception) {
            throw new LizzyException("I couldn't read your saved tasks from " + filePath + ".\n"
                    + "Starting with an empty task list for this session.");
        }
    }

    /**
     * Writes the complete task list, creating its parent folder when necessary.
     * Text fields are Base64-encoded so descriptions containing delimiters remain safe.
     *
     * @param tasks the tasks to save
     * @throws LizzyException if the data file cannot be written
     */
    public void save(List<Task> tasks) throws LizzyException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serialize(task));
        }

        try {
            Path parentFolder = filePath.getParent();
            if (parentFolder != null) {
                Files.createDirectories(parentFolder);
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException exception) {
            throw new LizzyException("I updated your task list, but couldn't save it to "
                    + filePath + ".");
        }
    }

    /**
     * Converts one task into a delimiter-separated storage record.
     * Descriptions and date values are encoded before they are written.
     *
     * @param task the task to store
     * @return one complete line for the data file
     */
    private static String serialize(Task task) {
        String status = task.isDone() ? COMPLETE_STATUS : INCOMPLETE_STATUS;
        String description = encode(task.getDescription());
        if (task instanceof WithinPeriodTask periodTask) {
            return String.join(FIELD_SEPARATOR, WITHIN_PERIOD_RECORD_TYPE, status, description,
                    encode(periodTask.getFrom().toString()), encode(periodTask.getTo().toString()));
        }
        if (task instanceof Deadline deadline) {
            return String.join(FIELD_SEPARATOR, DEADLINE_RECORD_TYPE, status, description,
                    encode(deadline.getBy().toString()));
        }
        if (task instanceof Event event) {
            List<String> fields = new ArrayList<>(List.of(EVENT_RECORD_TYPE, status, description,
                    encode(event.getFrom().toString()), encode(event.getTo().toString())));
            event.getStartTime().ifPresent(time -> {
                fields.add(encode(time.toString()));
                fields.add(encode(event.getEndTime().orElseThrow().toString()));
            });
            return String.join(FIELD_SEPARATOR, fields);
        }
        return String.join(FIELD_SEPARATOR, TODO_RECORD_TYPE, status, description);
    }

    /**
     * Restores one task from a storage record.
     *
     * @param line one raw line from the data file
     * @param lineNumber the one-based line number used in error messages
     * @return the restored task
     * @throws LizzyException if the record has an invalid type, status, field count, or value
     */
    private static Task deserialize(String line, int lineNumber) throws LizzyException {
        String[] fields = line.split(FIELD_SEPARATOR_PATTERN, -1);
        try {
            validateRecord(fields);
            boolean isDone = fields[1].equals(COMPLETE_STATUS);
            String description = decode(fields[2]);
            return switch (fields[0]) {
                case TODO_RECORD_TYPE -> new Todo(description, isDone);
                case DEADLINE_RECORD_TYPE -> new Deadline(description, LocalDate.parse(decode(fields[3])), isDone);
                case EVENT_RECORD_TYPE -> deserializeEvent(fields, description, isDone);
                case WITHIN_PERIOD_RECORD_TYPE -> new WithinPeriodTask(description,
                        LocalDate.parse(decode(fields[3])), LocalDate.parse(decode(fields[4])), isDone);
                default -> throw new IllegalArgumentException("Unknown task type");
            };
        } catch (IllegalArgumentException | DateTimeParseException exception) {
            throw new LizzyException("I couldn't understand the saved task data at line " + lineNumber + ".\n"
                    + "Starting with an empty task list for this session.");
        }
    }

    /**
     * Validates the type, completion state, and field count of a storage record.
     *
     * @param fields the delimiter-separated fields in a raw record
     * @throws IllegalArgumentException if the record cannot represent a supported task
     */
    private static void validateRecord(String[] fields) {
        if (fields.length < 2 || !isValidStatus(fields[1])) {
            throw new IllegalArgumentException("Invalid task status");
        }

        int expectedFieldCount = switch (fields[0]) {
            case TODO_RECORD_TYPE -> 3;
            case DEADLINE_RECORD_TYPE -> 4;
            case EVENT_RECORD_TYPE -> fields.length == 7 ? 7 : 5;
            case WITHIN_PERIOD_RECORD_TYPE -> 5;
            default -> throw new IllegalArgumentException("Unknown task type");
        };
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException("Invalid field count");
        }
    }

    /**
     * Restores an all-day or timed event from validated storage fields.
     *
     * @param fields the complete event record
     * @param description the decoded event description
     * @param isDone whether the event has been completed
     * @return the restored event
     */
    private static Event deserializeEvent(String[] fields, String description, boolean isDone) {
        LocalDate from = LocalDate.parse(decode(fields[3]));
        LocalDate to = LocalDate.parse(decode(fields[4]));
        if (fields.length == 5) {
            return new Event(description, from, to, isDone);
        }
        if (!from.equals(to)) {
            throw new IllegalArgumentException("A timed event must occur on one date");
        }
        return new Event(description, from, LocalTime.parse(decode(fields[5])),
                LocalTime.parse(decode(fields[6])), isDone);
    }

    /**
     * Returns whether a stored completion marker is supported.
     *
     * @param status the completion marker from a storage record
     * @return {@code true} if the marker represents a complete or incomplete task
     */
    private static boolean isValidStatus(String status) {
        return status.equals(INCOMPLETE_STATUS) || status.equals(COMPLETE_STATUS);
    }

    /**
     * Encodes user-entered text so it cannot be confused with file delimiters.
     *
     * @param text the text to encode
     * @return the UTF-8 Base64 representation of the text
     */
    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes one Base64-encoded text field from the data file.
     *
     * @param text the encoded field value
     * @return the decoded UTF-8 text
     */
    private static String decode(String text) {
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
