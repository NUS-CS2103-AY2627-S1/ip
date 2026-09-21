package caitlyn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Caitlyn's task list in the local data file.
 */
public final class TaskStorage {
    private static final int FIELD_INDEX_TYPE = 0;
    private static final int FIELD_INDEX_STATUS = 1;
    private static final int FIELD_INDEX_DESCRIPTION = 2;
    private static final int FIELD_INDEX_DEADLINE = 3;
    private static final int FIELD_INDEX_START = 3;
    private static final int FIELD_INDEX_END = 4;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;
    private static final int FIELD_COUNT_WITHIN = 5;

    /** The file used by this storage instance, allowing tests to use temporary files. */
    private final Path taskFile;

    /**
     * Creates storage using the application's default task file.
     */
    public TaskStorage() {
        this(Path.of("data", "duke.txt"));
    }

    /**
     * Creates storage using a supplied task file.
     *
     * @param taskFile the file to read and replace when saving tasks.
     */
    TaskStorage(Path taskFile) {
        this.taskFile = taskFile.toAbsolutePath();
    }

    /**
     * Replaces the saved task list with the current tasks.
     *
     * @param tasks the tasks to save.
     * @throws IOException if the directory or file cannot be written.
     */
    public void save(List<Task> tasks) throws IOException {
        List<String> lines = serializeTasks(tasks);
        Files.createDirectories(taskFile.getParent());
        replaceTaskFile(lines);
    }

    /**
     * Converts tasks to saved lines after validating the list and its elements.
     */
    private static List<String> serializeTasks(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("The task list cannot be null.");
        }
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task == null) {
                throw new IllegalArgumentException("The task list cannot contain null tasks.");
            }
            lines.add(task.toStorageString());
        }
        return lines;
    }

    /**
     * Writes complete task data before replacing the existing file and cleans up on failure.
     */
    private void replaceTaskFile(List<String> lines) throws IOException {
        Path temporaryFile = Files.createTempFile(
                taskFile.getParent(), taskFile.getFileName().toString(), ".tmp");
        boolean hasMoved = false;
        try {
            Files.write(
                    temporaryFile,
                    lines,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            moveTaskFile(temporaryFile);
            hasMoved = true;
        } finally {
            if (!hasMoved) {
                Files.deleteIfExists(temporaryFile);
            }
        }
    }

    /**
     * Replaces the task file atomically when the file system supports it.
     */
    private void moveTaskFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, taskFile,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads all saved tasks from the local data file.
     *
     * @return the saved tasks, or an empty list when no data file exists.
     * @throws IOException if the data file cannot be read.
     * @throws IllegalArgumentException if a saved line has an invalid format.
     */
    public List<Task> load() throws IOException {
        if (Files.notExists(taskFile)) {
            return new ArrayList<>();
        }

        List<Task> tasks = new ArrayList<>();
        List<String> lines = Files.readAllLines(taskFile, StandardCharsets.UTF_8);
        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            if (!line.isBlank()) {
                try {
                    tasks.add(parseTask(line));
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException(
                            "Invalid saved task on line " + (index + 1) + ": " + exception.getMessage(),
                            exception);
                }
            }
        }
        return tasks;
    }

    /**
     * Converts one saved line into a task object.
     *
     * @param line the pipe-separated task line.
     * @return the task represented by the line.
     * @throws IllegalArgumentException if the line is not valid storage data.
     */
    private static Task parseTask(String line) {
        List<String> fields = splitFields(line);
        if (fields.size() < FIELD_COUNT_TODO) {
            throw new IllegalArgumentException("A saved task must have a type, status, and description.");
        }

        boolean isDone = switch (fields.get(FIELD_INDEX_STATUS)) {
            case "0" -> false;
            case "1" -> true;
            default -> throw new IllegalArgumentException("A saved task status must be 0 or 1.");
        };

        TaskType taskType = TaskType.parseMarker(fields.get(FIELD_INDEX_TYPE));
        Task task = switch (taskType) {
            case TODO -> {
                requireFieldCount(fields, FIELD_COUNT_TODO);
                yield new Todo(fields.get(FIELD_INDEX_DESCRIPTION));
            }
            case DEADLINE -> {
                requireFieldCount(fields, FIELD_COUNT_DEADLINE);
                yield new Deadline(fields.get(FIELD_INDEX_DESCRIPTION), fields.get(FIELD_INDEX_DEADLINE));
            }
            case EVENT -> {
                requireFieldCount(fields, FIELD_COUNT_EVENT);
                yield new Event(fields.get(FIELD_INDEX_DESCRIPTION),
                        fields.get(FIELD_INDEX_START), fields.get(FIELD_INDEX_END));
            }
            case WITHIN -> {
                requireFieldCount(fields, FIELD_COUNT_WITHIN);
                yield new WithinTask(fields.get(FIELD_INDEX_DESCRIPTION),
                        fields.get(FIELD_INDEX_START), fields.get(FIELD_INDEX_END));
            }
            default -> throw new IllegalStateException("Unhandled task type: " + taskType);
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Ensures a parsed task has exactly the fields required by its type.
     *
     * @param fields the fields parsed from a saved line.
     * @param expectedCount the number of fields required by the task type.
     * @throws IllegalArgumentException if there are too few or too many fields.
     */
    private static void requireFieldCount(List<String> fields, int expectedCount) {
        if (fields.size() != expectedCount) {
            throw new IllegalArgumentException("The saved task has the wrong number of fields.");
        }
    }

    /**
     * Splits a saved line while treating escaped pipes as part of a field.
     *
     * @param line the saved task line.
     * @return decoded and trimmed fields.
     * @throws IllegalArgumentException if the line ends with an incomplete escape.
     */
    private static List<String> splitFields(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '|') {
                fields.add(field.toString().trim());
                field.setLength(0);
            } else if (character == '\\') {
                if (index + 1 >= line.length()) {
                    throw new IllegalArgumentException("A saved task has an incomplete escape sequence.");
                }
                index++;
                field.append(decodeEscape(line.charAt(index)));
            } else {
                field.append(character);
            }
        }
        fields.add(field.toString().trim());
        return fields;
    }

    /**
     * Decodes a known escape while preserving the backslash for an unknown escape.
     */
    private static String decodeEscape(char escapedCharacter) {
        return switch (escapedCharacter) {
            case '\\' -> "\\";
            case '|' -> "|";
            case 'n' -> "\n";
            case 'r' -> "\r";
            default -> "\\" + escapedCharacter;
        };
    }
}
