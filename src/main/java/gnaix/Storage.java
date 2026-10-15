package gnaix;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.Task;
import gnaix.task.Todo;

/**
 * Represents persistent storage for Gnaix tasks.
 */
public class Storage {
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private static final int TODO_TAGGED_FIELD_COUNT = 4;
    private static final int DEADLINE_TAGGED_FIELD_COUNT = 5;
    private static final int EVENT_TAGGED_FIELD_COUNT = 6;

    private final Path filePath;

    /**
     * Creates storage using the specified file path.
     *
     * @param filePath Path to the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves all tasks to the storage file.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        Path parent = filePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            lines.add(taskToString(task));
        }

        Files.write(
                filePath,
                lines,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Loads tasks from the storage file.
     *
     * @return Tasks loaded from the file, or an empty list if the file does not exist.
     * @throws IOException If the file cannot be read.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                System.out.println("Some stored task data could not be loaded.");
            }
        }

        return tasks;
    }

    /**
     * Parses a stored task record into a task object.
     *
     * @param line Stored task record.
     * @return Task represented by the record.
     * @throws IllegalArgumentException If the record has an invalid format.
     */
    private Task parseTask(String line) {
        String[] fields = parseFields(line);
        boolean isCompleted = parseCompletionStatus(fields);
        Task task = createTask(fields);

        restoreTags(task, fields);

        if (isCompleted) {
            task.markAsComplete();
        }

        return task;
    }

    /**
     * Splits and normalizes the fields in a stored task record.
     *
     * @param line Stored task record.
     * @return Normalized fields in the record.
     * @throws IllegalArgumentException If the record has too few fields.
     */
    private String[] parseFields(String line) {
        String[] fields = line.split("\\|", -1);

        for (int i = 0; i < fields.length; i++) {
            fields[i] = fields[i].trim();
        }

        if (fields.length < TODO_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid task format");
        }

        return fields;
    }

    /**
     * Parses the completion status from a stored task record.
     *
     * @param fields Fields in the stored task record.
     * @return True if the stored task is completed.
     * @throws IllegalArgumentException If the completion status is invalid.
     */
    private boolean parseCompletionStatus(String[] fields) {
        String completionStatus = fields[1];

        if (!completionStatus.equals("0") && !completionStatus.equals("1")) {
            throw new IllegalArgumentException("Invalid completion status");
        }

        return completionStatus.equals("1");
    }

    /**
     * Creates a task from the fields in a stored task record.
     *
     * @param fields Fields in the stored task record.
     * @return Task represented by the fields.
     * @throws IllegalArgumentException If the task type or field count is invalid.
     */
    private Task createTask(String[] fields) {
        String taskType = fields[0];

        switch (taskType) {
            case "T":
                return createTodo(fields);
            case "D":
                return createDeadline(fields);
            case "E":
                return createEvent(fields);
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    /**
     * Creates a todo from a stored task record.
     *
     * @param fields Fields in the stored task record.
     * @return Todo represented by the fields.
     * @throws IllegalArgumentException If the field count is invalid.
     */
    private Todo createTodo(String[] fields) {
        if (fields.length != TODO_FIELD_COUNT
                && fields.length != TODO_TAGGED_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid Todo format");
        }

        return new Todo(fields[2]);
    }

    /**
     * Creates a deadline from a stored task record.
     *
     * @param fields Fields in the stored task record.
     * @return Deadline represented by the fields.
     * @throws IllegalArgumentException If the field count is invalid.
     */
    private Deadline createDeadline(String[] fields) {
        if (fields.length != DEADLINE_FIELD_COUNT
                && fields.length != DEADLINE_TAGGED_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid Deadline format");
        }

        return new Deadline(fields[2], LocalDate.parse(fields[3]));
    }

    /**
     * Creates an event from a stored task record.
     *
     * @param fields Fields in the stored task record.
     * @return Event represented by the fields.
     * @throws IllegalArgumentException If the field count is invalid.
     */
    private Event createEvent(String[] fields) {
        if (fields.length != EVENT_FIELD_COUNT
                && fields.length != EVENT_TAGGED_FIELD_COUNT) {
            throw new IllegalArgumentException("Invalid Event format");
        }

        return new Event(
                fields[2],
                LocalDateTime.parse(fields[3]),
                LocalDateTime.parse(fields[4]));
    }

    /**
     * Restores tags from a stored task record.
     *
     * @param task Task receiving the stored tags.
     * @param fields Fields in the stored task record.
     */
    private void restoreTags(Task task, String[] fields) {
        int tagFieldIndex = getTagFieldIndex(fields[0]);

        if (fields.length <= tagFieldIndex || fields[tagFieldIndex].isEmpty()) {
            return;
        }

        String[] tags = fields[tagFieldIndex].split(",");

        for (String tag : tags) {
            task.addTag(tag.trim());
        }
    }

    /**
     * Returns the position of the optional tag field for a task type.
     *
     * @param taskType Stored task type.
     * @return Index of the optional tag field.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private int getTagFieldIndex(String taskType) {
        switch (taskType) {
            case "T":
                return TODO_FIELD_COUNT;
            case "D":
                return DEADLINE_FIELD_COUNT;
            case "E":
                return EVENT_FIELD_COUNT;
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    /**
     * Converts a task into its persistent storage representation.
     *
     * @param task Task to serialize.
     * @return String representation suitable for storage.
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private String taskToString(Task task) {
        int completed = task.isCompleted() ? 1 : 0;
        String tagField = String.join(",", task.getTags());

        if (task instanceof Todo) {
            return "T | " + completed + " | "
                    + task.getDescription() + " | " + tagField;
        }

        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + completed + " | "
                    + task.getDescription() + " | "
                    + deadline.getDoBy() + " | " + tagField;
        }

        if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + completed + " | "
                    + task.getDescription() + " | "
                    + event.getFrom() + " | "
                    + event.getTo() + " | " + tagField;
        }

        throw new IllegalArgumentException("Unknown task type");
    }
}
