package trybot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import trybot.task.Deadline;
import trybot.task.Event;
import trybot.task.Task;
import trybot.task.Todo;

/**
 * Saves TryBot's task list to a file on disk.
 */
public class Storage {
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String COMPLETED_STATUS = "1";
    private final Path taskFile;

    /**
     * Creates storage using TryBot's default task file.
     */
    public Storage() {
        this(Path.of("data", "trybot.txt"));
    }

    /**
     * Creates storage using the specified task file.
     *
     * @param filePath path to the task file.
     */
    public Storage(String filePath) {
        this(filePath == null ? null : Path.of(filePath));
    }

    /**
     * Creates storage using the specified task file.
     *
     * @param taskFile path to the task file.
     */
    public Storage(Path taskFile) {
        if (taskFile == null) {
            throw new IllegalArgumentException("The task file path cannot be null.");
        }
        this.taskFile = taskFile;
    }

    /**
     * Replaces the saved task data with the current task list.
     *
     * @param tasks tasks to save.
     * @throws IOException if the task file cannot be created or written
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        if (tasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null.");
        }
        if (tasks.stream().anyMatch(task -> task == null)) {
            throw new IllegalArgumentException("The task list cannot contain null tasks.");
        }

        Path parentDirectory = taskFile.toAbsolutePath().getParent();
        Files.createDirectories(parentDirectory);

        List<String> fileLines = tasks.stream()
                .map(Task::toStorageString)
                .toList();
        Path temporaryFile = Files.createTempFile(parentDirectory, "trybot-", ".tmp");
        boolean moved = false;
        try {
            Files.write(temporaryFile, fileLines, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            try {
                Files.move(temporaryFile, taskFile, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
        } finally {
            if (!moved) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException ignored) {
                    // Preserve the original write failure when temporary-file cleanup also fails.
                }
            }
        }
    }

    /**
     * Loads all valid tasks from the saved task data.
     *
     * @return tasks restored from disk, or an empty list when no file exists
     * @throws IOException if the task file cannot be read
     */
    public List<Task> loadTasks() throws IOException {
        if (Files.notExists(taskFile)) {
            return new ArrayList<>();
        }
        if (!Files.isRegularFile(taskFile)) {
            throw new IOException("The task data path is not a regular file.");
        }

        return Files.readAllLines(taskFile, StandardCharsets.UTF_8).stream()
                .map(Storage::parseTaskSafely)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Parses one record without allowing one corrupt line to abort the whole load. */
    private static Task parseTaskSafely(String line) {
        try {
            return parseTask(line);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    /**
     * Converts one saved line into a task.
     *
     * @param line saved task data.
     * @return the parsed task, or null when the line is blank or malformed
     */
    private static Task parseTask(String line) {
        if (line.isBlank()) {
            return null;
        }

        List<String> fields = splitFields(line);
        if (fields == null || fields.size() < 3) {
            return null;
        }

        SavedTaskFields savedTask = decodeTaskFields(fields);
        if (savedTask == null || !isValidStatus(savedTask.status())) {
            return null;
        }

        Task task;
        try {
            task = createTask(fields, savedTask);
        } catch (IllegalArgumentException exception) {
            return null;
        }

        if (task != null && savedTask.status().equals(COMPLETED_STATUS)) {
            task.markAsDone();
        }
        if (task != null) {
            addSavedTags(task, fields, savedTask.taskType());
        }

        // A record accepted by this method must produce a task with usable core data.
        assert task == null || task.getDescription() != null && !task.getDescription().isBlank();
        return task;
    }

    /**
     * Decodes the common fields in a saved task record.
     *
     * @param fields raw fields from a saved record.
     * @return decoded common fields, or null when a field cannot be decoded
     */
    private static SavedTaskFields decodeTaskFields(List<String> fields) {
        String taskType = unescapeField(fields.get(0));
        String status = unescapeField(fields.get(1));
        String description = unescapeField(fields.get(2));
        if (taskType == null || status == null || description == null) {
            return null;
        }
        return new SavedTaskFields(taskType.trim().replace("\uFEFF", "").toUpperCase(Locale.ROOT),
                status.trim(), description.trim());
    }

    /**
     * Checks whether a saved status is one of the supported values.
     *
     * @param status decoded completion status.
     * @return true when the status represents an incomplete or completed task
     */
    private static boolean isValidStatus(String status) {
        return status.equals("0") || status.equals(COMPLETED_STATUS);
    }

    /**
     * Creates a task from decoded common fields and its type-specific fields.
     *
     * @param fields raw fields from a saved record.
     * @param savedTask decoded common fields.
     * @return the constructed task, or null when the record shape is invalid
     */
    private static Task createTask(List<String> fields, SavedTaskFields savedTask) {
        switch (savedTask.taskType()) {
            case TODO_TYPE:
                return fields.size() >= 3 && !savedTask.description().isEmpty()
                        ? new Todo(savedTask.description()) : null;
            case DEADLINE_TYPE:
                return createDeadline(fields, savedTask.description());
            case EVENT_TYPE:
                return createEvent(fields, savedTask.description());
            default:
                return null;
        }
    }

    /**
     * Creates a deadline from its saved fields when all fields are valid.
     *
     * @param fields raw fields from a saved record.
     * @param description decoded task description.
     * @return the constructed deadline, or null when the record is invalid
     */
    private static Task createDeadline(List<String> fields, String description) {
        String by = fields.size() >= 4 ? unescapeField(fields.get(3)) : null;
        return by != null && !description.isEmpty() && !by.trim().isEmpty()
                ? new Deadline(description, by.trim()) : null;
    }

    /**
     * Creates an event from its saved fields when all fields are valid.
     *
     * @param fields raw fields from a saved record.
     * @param description decoded task description.
     * @return the constructed event, or null when the record is invalid
     */
    private static Task createEvent(List<String> fields, String description) {
        String from = fields.size() >= 5 ? unescapeField(fields.get(3)) : null;
        String to = fields.size() >= 5 ? unescapeField(fields.get(4)) : null;
        boolean hasValidDetails = from != null && to != null
                && !description.isEmpty() && !from.trim().isEmpty() && !to.trim().isEmpty();
        return hasValidDetails ? new Event(description, from.trim(), to.trim()) : null;
    }

    /** Restores optional tag fields from a saved task record. */
    private static void addSavedTags(Task task, List<String> fields, String taskType) {
        int firstTagIndex = switch (taskType) {
            case TODO_TYPE -> 3;
            case DEADLINE_TYPE -> 4;
            case EVENT_TYPE -> 5;
            default -> fields.size();
        };
        for (int i = firstTagIndex; i < fields.size(); i++) {
            String tag = unescapeField(fields.get(i));
            if (tag == null || tag.isBlank()) {
                continue;
            }
            task.addTag(tag.trim());
        }
    }

    /** Holds the common fields decoded from one saved task record. */
    private record SavedTaskFields(String taskType, String status, String description) {
    }

    /**
     * Splits a saved line at unescaped field separators.
     *
     * @param line saved task data.
     * @return fields, or null when the line ends with an incomplete escape
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaped) {
                currentField.append('\\').append(character);
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '|') {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(character);
            }
        }
        if (escaped) {
            return null;
        }
        fields.add(currentField.toString());
        return fields;
    }

    /**
     * Decodes an escaped field from the task data file.
     *
     * @param field escaped field text.
     * @return decoded field, or null when the escape sequence is incomplete
     */
    private static String unescapeField(String field) {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < field.length(); i++) {
            char character = field.charAt(i);
            if (character != '\\') {
                decoded.append(character);
                continue;
            }
            if (i + 1 >= field.length()) {
                return null;
            }
            char escapedCharacter = field.charAt(++i);
            switch (escapedCharacter) {
                case '\\':
                    // Fallthrough
                case '|':
                    decoded.append(escapedCharacter);
                    break;
                case 'n':
                    decoded.append('\n');
                    break;
                case 'r':
                    decoded.append('\r');
                    break;
                default:
                    decoded.append('\\').append(escapedCharacter);
            }
        }
        return decoded.toString();
    }
}
