package shrekanddonkey.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shrekanddonkey.task.Deadline;
import shrekanddonkey.task.Event;
import shrekanddonkey.task.Task;
import shrekanddonkey.task.Todo;

/**
 * Handles loading and saving the chatbot's task file.
 */
public class Storage {
    /** Default location of the task data file. */
    public static final Path DEFAULT_FILE_PATH = Path.of("data", "happyFile.txt");

    private static final String FIELD_SEPARATOR = " | ";
    private static final DateTimeFormatter LEGACY_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);
    private static final Pattern LEGACY_TODO_PATTERN = Pattern.compile("^\\[T\\]\\[([ X])\\] (.*)$");
    private static final Pattern LEGACY_DEADLINE_PATTERN =
            Pattern.compile("^\\[D\\]\\[([ X])\\] (.*) \\(by: (.+)\\)$");
    private static final Pattern LEGACY_EVENT_PATTERN =
            Pattern.compile("^\\[E\\]\\[([ X])\\] (.*) \\(from: (.+) to: (.+)\\)$");

    private final Path filePath;

    /**
     * Creates storage at the default task file location.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Creates storage backed by the given file path.
     *
     * @param filePath path of the task file.
     */
    public Storage(String filePath) {
        this(Path.of(filePath));
    }

    /**
     * Creates storage backed by the given file path.
     *
     * @param filePath path of the task file.
     */
    private Storage(Path filePath) {
        assert filePath != null : "Storage file path cannot be null";
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks, creating an empty data file when it does not exist.
     *
     * @return tasks loaded from the data file.
     * @throws IOException if the directory or file cannot be read or created, or its contents are invalid.
     */
    public List<Task> load() throws IOException {
        ensureFileExists();
        List<Task> tasks = new ArrayList<>();
        List<String> taskLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (int index = 0; index < taskLines.size(); index++) {
            String taskLine = taskLines.get(index);
            if (!taskLine.isBlank()) {
                tasks.add(parseTask(taskLine, index + 1));
            }
        }
        return tasks;
    }

    /**
     * Saves each task using a machine-readable format.
     *
     * @param tasks tasks to save.
     * @throws IOException if the directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        assert tasks != null : "Task list to save cannot be null";
        ensureFileExists();

        List<String> taskLines = tasks.stream()
                .map(this::serializeTask)
                .toList();
        Files.write(filePath, taskLines, StandardCharsets.UTF_8);
    }

    /**
     * Creates the data directory and its task file when either is missing.
     *
     * @throws IOException if the directory or file cannot be created.
     */
    private void ensureFileExists() throws IOException {
        Path parent = filePath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Converts one stored task line into a task object.
     *
     * @param taskLine task data to parse.
     * @param lineNumber one-based source line number.
     * @return reconstructed task.
     * @throws IOException if the task data is invalid.
     */
    private Task parseTask(String taskLine, int lineNumber) throws IOException {
        String[] fields = taskLine.split(Pattern.quote(FIELD_SEPARATOR), -1);
        if (fields.length >= 3 && isStoredTaskType(fields[0])) {
            return parseStoredTask(fields, lineNumber);
        }
        return parseLegacyTask(taskLine, lineNumber);
    }

    /**
     * Returns whether the text represents one of the supported stored task types.
     *
     * @param taskType potential stored task type.
     * @return {@code true} when the type is supported.
     */
    private boolean isStoredTaskType(String taskType) {
        return taskType.equals("T") || taskType.equals("D") || taskType.equals("E");
    }

    /**
     * Converts a machine-readable stored line into a task object.
     *
     * @param fields separated stored fields.
     * @param lineNumber one-based source line number.
     * @return reconstructed task.
     * @throws IOException if the stored task data is invalid.
     */
    private Task parseStoredTask(String[] fields, int lineNumber) throws IOException {
        boolean isDone = parseCompletionStatus(fields, lineNumber);
        try {
            Task task;
            switch (fields[0]) {
            case "T":
                requireFieldCount(fields, 3, lineNumber);
                task = new Todo(fields[2]);
                break;
            case "D":
                requireFieldCount(fields, 4, lineNumber);
                task = new Deadline(fields[2], LocalDateTime.parse(fields[3]));
                break;
            case "E":
                requireFieldCount(fields, 5, lineNumber);
                task = new Event(fields[2], LocalDateTime.parse(fields[3]), LocalDateTime.parse(fields[4]));
                break;
            default:
                throw invalidData(lineNumber);
            }
            applyCompletionStatus(task, isDone);
            return task;
        } catch (RuntimeException exception) {
            throw invalidData(lineNumber, exception);
        }
    }

    /**
     * Converts an existing display-formatted line into a task object.
     *
     * @param taskLine legacy task data to parse.
     * @param lineNumber one-based source line number.
     * @return reconstructed task.
     * @throws IOException if the legacy task data is invalid.
     */
    private Task parseLegacyTask(String taskLine, int lineNumber) throws IOException {
        try {
            Matcher todoMatcher = LEGACY_TODO_PATTERN.matcher(taskLine);
            if (todoMatcher.matches()) {
                return createTask(new Todo(todoMatcher.group(2)), todoMatcher.group(1).equals("X"));
            }

            Matcher deadlineMatcher = LEGACY_DEADLINE_PATTERN.matcher(taskLine);
            if (deadlineMatcher.matches()) {
                LocalDateTime by = LocalDateTime.parse(deadlineMatcher.group(3), LEGACY_DATE_TIME_FORMATTER);
                return createTask(new Deadline(deadlineMatcher.group(2), by), deadlineMatcher.group(1).equals("X"));
            }

            Matcher eventMatcher = LEGACY_EVENT_PATTERN.matcher(taskLine);
            if (eventMatcher.matches()) {
                LocalDateTime from = LocalDateTime.parse(eventMatcher.group(3), LEGACY_DATE_TIME_FORMATTER);
                LocalDateTime to = LocalDateTime.parse(eventMatcher.group(4), LEGACY_DATE_TIME_FORMATTER);
                return createTask(new Event(eventMatcher.group(2), from, to), eventMatcher.group(1).equals("X"));
            }
        } catch (RuntimeException exception) {
            throw invalidData(lineNumber, exception);
        }
        throw invalidData(lineNumber);
    }

    /**
     * Validates the stored completion status field.
     *
     * @param fields separated stored fields.
     * @param lineNumber one-based source line number.
     * @return whether the task is completed.
     * @throws IOException if the status field is invalid.
     */
    private boolean parseCompletionStatus(String[] fields, int lineNumber) throws IOException {
        if (fields.length < 2 || (!fields[1].equals("0") && !fields[1].equals("1"))) {
            throw invalidData(lineNumber);
        }
        return fields[1].equals("1");
    }

    /**
     * Validates the number of fields for a stored task.
     *
     * @param fields separated stored fields.
     * @param expectedCount required field count.
     * @param lineNumber one-based source line number.
     * @throws IOException if the field count is invalid.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber) throws IOException {
        if (fields.length != expectedCount) {
            throw invalidData(lineNumber);
        }
    }

    /**
     * Marks a task as done when the saved completion state requires it.
     *
     * @param task task whose state should be restored.
     * @param isDone saved completion state.
     * @return the restored task.
     */
    private Task createTask(Task task, boolean isDone) {
        applyCompletionStatus(task, isDone);
        return task;
    }

    /**
     * Applies a stored completion state to a task.
     *
     * @param task task whose state should be restored.
     * @param isDone saved completion state.
     */
    private void applyCompletionStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        }
    }

    /**
     * Converts a task into one machine-readable storage line.
     *
     * @param task task to serialize.
     * @return serialized task data.
     */
    private String serializeTask(Task task) {
        String completionStatus = task.getStatusIcon().equals("X") ? "1" : "0";
        if (task instanceof Todo) {
            return String.join(FIELD_SEPARATOR, "T", completionStatus, task.getDescription());
        }
        if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return String.join(FIELD_SEPARATOR, "D", completionStatus, deadline.getDescription(),
                    deadline.getBy().toString());
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return String.join(FIELD_SEPARATOR, "E", completionStatus, event.getDescription(),
                    event.getFrom().toString(), event.getTo().toString());
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getSimpleName());
    }

    /**
     * Creates an exception for invalid saved data.
     *
     * @param lineNumber one-based source line number.
     * @return exception describing the invalid data.
     */
    private IOException invalidData(int lineNumber) {
        return new IOException("Invalid task data at line " + lineNumber + ".");
    }

    /**
     * Creates an exception for invalid saved data with its underlying cause.
     *
     * @param lineNumber one-based source line number.
     * @param cause parsing failure.
     * @return exception describing the invalid data.
     */
    private IOException invalidData(int lineNumber, RuntimeException cause) {
        return new IOException("Invalid task data at line " + lineNumber + ".", cause);
    }
}
