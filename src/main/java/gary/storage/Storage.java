package gary.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import gary.task.Deadline;
import gary.task.Event;
import gary.task.Task;
import gary.task.Todo;

/**
 * Loads tasks from and saves tasks to a text file.
 */
public class Storage {
    private static final int TASK_TYPE_INDEX = 0;
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
     * Creates a storage manager for the specified data file.
     *
     * @param filePath Path of the task data file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage file path should not be null";

        this.filePath = filePath;
    }

    /**
     * Loads valid task records from the data file.
     *
     * @return Tasks represented by valid records, or an empty list if the file does not exist.
     * @throws IOException If the data file cannot be read.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        return Files.readAllLines(filePath, StandardCharsets.UTF_8).stream()
                .map(this::parseTask)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Saves the tasks, creating the data directory when needed.
     *
     * @param tasks Tasks to save.
     * @throws IOException If the tasks cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        assert tasks != null : "Task list should not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Task list should not contain null tasks";

        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        List<String> lines = tasks.stream()
                .map(this::formatTask)
                .toList();
        Files.write(filePath, lines, StandardCharsets.UTF_8);
    }

    /**
     * Returns the task represented by a storage record.
     *
     * @return Parsed task, or {@code null} if the record is invalid.
     */
    private Task parseTask(String line) {
        String[] fields = line.split(" \\| ", -1);
        if (!hasValidHeader(fields)) {
            return null;
        }

        Task task;
        try {
            task = switch (fields[TASK_TYPE_INDEX]) {
                case "T" -> parseTodo(fields);
                case "D" -> parseDeadline(fields);
                case "E" -> parseEvent(fields);
                default -> null;
            };
        } catch (DateTimeParseException e) {
            task = null;
        }

        if (task != null && fields[STATUS_INDEX].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private boolean hasValidHeader(String[] fields) {
        if (fields.length < TODO_FIELD_COUNT) {
            return false;
        }

        String status = fields[STATUS_INDEX];
        return status.equals("0") || status.equals("1");
    }

    private Task parseTodo(String[] fields) {
        if (fields.length != TODO_FIELD_COUNT || fields[DESCRIPTION_INDEX].isBlank()) {
            return null;
        }
        return new Todo(fields[DESCRIPTION_INDEX]);
    }

    private Task parseDeadline(String[] fields) {
        if (fields.length != DEADLINE_FIELD_COUNT
                || fields[DESCRIPTION_INDEX].isBlank()
                || fields[DEADLINE_DATE_INDEX].isBlank()) {
            return null;
        }
        return new Deadline(
                fields[DESCRIPTION_INDEX],
                LocalDate.parse(fields[DEADLINE_DATE_INDEX]));
    }

    private Task parseEvent(String[] fields) {
        if (fields.length != EVENT_FIELD_COUNT
                || fields[DESCRIPTION_INDEX].isBlank()
                || fields[EVENT_START_DATE_INDEX].isBlank()
                || fields[EVENT_END_DATE_INDEX].isBlank()) {
            return null;
        }
        return new Event(
                fields[DESCRIPTION_INDEX],
                LocalDate.parse(fields[EVENT_START_DATE_INDEX]),
                LocalDate.parse(fields[EVENT_END_DATE_INDEX]));
    }

    /**
     * Returns the storage record for a supported task.
     *
     * @throws IllegalArgumentException If the task type is unsupported.
     */
    private String formatTask(Task task) {
        assert task != null : "Task to format should not be null";

        String status = task.isDone() ? "1" : "0";
        if (task instanceof Todo) {
            return "T | " + status + " | " + task.getDescription();
        }
        if (task instanceof Deadline deadline) {
            return "D | " + status + " | " + task.getDescription() + " | " + deadline.getBy();
        }
        if (task instanceof Event event) {
            return "E | " + status + " | " + task.getDescription() + " | "
                    + event.getFrom() + " | " + event.getTo();
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }
}
