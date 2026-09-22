package friday.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.TaskList;
import friday.task.Todo;

/**
 * Loads and saves tasks using a line-based text file.
 */
public class Storage {
    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_START_INDEX = 3;
    private static final int EVENT_END_INDEX = 4;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final String TODO_TYPE = "[T]";
    private static final String DEADLINE_TYPE = "[D]";
    private static final String EVENT_TYPE = "[E]";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";

    private final Path filePath;
    private boolean isSaveBlocked;
    private int rejectedRecordCount;

    /**
     * Creates storage backed by the specified file.
     *
     * @param filePath Path of the task data file.
     * @throws IllegalArgumentException If filePath is {@code null}.
     */
    public Storage(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("Storage path cannot be null.");
        }
        this.filePath = filePath;
    }

    /**
     * Loads all valid tasks. Blank or malformed lines are ignored so that one
     * damaged record does not prevent the remaining tasks from loading.
     *
     * @return Tasks read from the file.
     * @throws IOException If the file exists but cannot be read.
     */
    public ArrayList<Task> load() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        isSaveBlocked = true;
        rejectedRecordCount = 0;
        if (Files.notExists(filePath)) {
            isSaveBlocked = false;
            return tasks;
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("Storage path is not a regular file: " + filePath);
        }

        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);


        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            Task task = parseTask(line);
            if (task != null && tasks.stream().noneMatch(existing -> existing.hasSameDetails(task))) {
                tasks.add(task);
            } else {
                rejectedRecordCount++;
            }
        }
        isSaveBlocked = rejectedRecordCount > 0;
        return tasks;
    }

    /**
     * Saves all tasks, creating a missing parent directory when necessary.
     *
     * @param tasks Tasks to save.
     * @throws IllegalArgumentException If tasks is {@code null}.
     * @throws IOException If the file cannot be written.
     */
    public void save(TaskList tasks) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Task list cannot be null.");
        }

        if (isSaveBlocked) {
            throw new IOException("Saving is disabled because the original file could not be fully loaded. "
                    + "Repair or back up the file and restart Friday.");
        }
        ArrayList<String> lines = new ArrayList<>();

        for (Task task : tasks) {
            if (task == null) {
                continue;
            }
            lines.add(task.toFileString());
        }

        Path parent = filePath.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path temporaryFile = Files.createTempFile(parent, "friday-", ".tmp");
        try {
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            Files.move(temporaryFile, filePath.toAbsolutePath(), StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /** Returns the number of malformed or duplicate records skipped during loading. */
    public int getRejectedRecordCount() {
        return rejectedRecordCount;
    }

    private Task parseTask(String line) {
        try {
            List<String> parts = parseLine(line);
            Task task = createTask(parts);
            if (task != null && parts.get(TASK_STATUS_INDEX).equals(COMPLETE_STATUS)) {
                task.markAsDone();
            }
            return task;
        } catch (IllegalArgumentException | DateTimeParseException exception) {
            // Ignore malformed records so that the remaining tasks can still be loaded.
            return null;
        }
    }

    private Task createTask(List<String> parts) {
        if (parts.size() < TODO_FIELD_COUNT) {
            return null;
        }

        String status = parts.get(TASK_STATUS_INDEX);
        if (!status.equals(INCOMPLETE_STATUS) && !status.equals(COMPLETE_STATUS)) {
            return null;
        }

        return switch (parts.get(TASK_TYPE_INDEX)) {
            case TODO_TYPE -> createTodo(parts);
            case DEADLINE_TYPE -> createDeadline(parts);
            case EVENT_TYPE -> createEvent(parts);
            default -> null;
        };
    }

    private Todo createTodo(List<String> parts) {
        if (parts.size() != TODO_FIELD_COUNT) {
            return null;
        }
        return new Todo(parts.get(TASK_DESCRIPTION_INDEX));
    }

    private Deadline createDeadline(List<String> parts) {
        if (parts.size() != DEADLINE_FIELD_COUNT) {
            return null;
        }
        return new Deadline(new String[] {
            parts.get(TASK_DESCRIPTION_INDEX), parts.get(DEADLINE_DATE_INDEX)
        });
    }

    private Event createEvent(List<String> parts) {
        if (parts.size() != EVENT_FIELD_COUNT) {
            return null;
        }
        return new Event(new String[] {
            parts.get(TASK_DESCRIPTION_INDEX),
            parts.get(EVENT_START_INDEX),
            parts.get(EVENT_END_INDEX)
        });
    }

    /** Parses fields while supporting escaped commas, slashes, and line breaks. */
    private List<String> parseLine(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;

        for (char character : line.toCharArray()) {
            if (escaped) {
                switch (character) {
                    case 'n' -> field.append('\n');
                    case 'r' -> field.append('\r');
                    case ',', '\\' -> field.append(character);
                    default -> throw new IllegalArgumentException("Unknown escape in save record.");
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == ',') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (escaped) {
            throw new IllegalArgumentException("Save record ends with an incomplete escape.");
        }
        fields.add(field.toString());
        return fields;
    }
}
