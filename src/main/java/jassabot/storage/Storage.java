package jassabot.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jassabot.task.Deadline;
import jassabot.task.Event;
import jassabot.task.Task;
import jassabot.task.Todo;
import jassabot.util.DateTimeFormats;

/**
 * Saves the current task list to and loads it from a file on the hard disk.
 */
public class Storage {
    private static final String FIELD_DELIMITER_REGEX = " \\| ";
    private final Path filePath;

    /**
     * Creates a storage object that writes to the given relative file path.
     *
     * @param filePath Relative path of the file used to store tasks.
     * @throws IllegalArgumentException If {@code filePath} is absolute.
     */
    public Storage(Path filePath) {
        if (filePath.isAbsolute()) {
            throw new IllegalArgumentException("The storage path must be relative.");
        }
        this.filePath = filePath.normalize();
    }

    /**
     * Loads every valid task from the storage file and reports malformed lines as warnings.
     * A missing file represents an empty task list, while unreadable paths produce a warning.
     *
     * @return Loaded tasks together with any non-fatal warnings.
     */
    public LoadResult loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        ArrayList<String> warnings = new ArrayList<>();

        List<String> taskLines;
        try {
            if (!Files.exists(filePath)) {
                return new LoadResult(tasks, warnings);
            }
            if (!Files.isRegularFile(filePath)) {
                warnings.add("The task data path is not a regular file. Starting with an empty task list.");
                return new LoadResult(tasks, warnings);
            }
            taskLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException | SecurityException e) {
            warnings.add("The task data file could not be read. Starting with an empty task list.");
            return new LoadResult(tasks, warnings);
        }

        for (int i = 0; i < taskLines.size(); i++) {
            String taskLine = taskLines.get(i);
            if (taskLine.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseTask(taskLine));
            } catch (StorageException e) {
                warnings.add("Skipped data line " + (i + 1) + ": " + e.getMessage());
            }
        }
        return new LoadResult(tasks, warnings);
    }

    /**
     * Atomically rewrites the storage file so a failed write cannot truncate the last good data.
     *
     * @param tasks Tasks to save.
     * @throws StorageException If the directory or file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws StorageException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory == null) {
            parentDirectory = Path.of(".");
        }
        Path temporaryFile = null;

        try {
            Files.createDirectories(parentDirectory);
            temporaryFile = Files.createTempFile(
                    parentDirectory,
                    filePath.getFileName().toString() + ".",
                    ".tmp");

            List<String> taskLines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(temporaryFile, taskLines, StandardCharsets.UTF_8);

            try {
                Files.move(temporaryFile, filePath,
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | SecurityException e) {
            throw new StorageException("The task data file could not be written.", e);
        } finally {
            if (temporaryFile != null) {
                try {
                    Files.deleteIfExists(temporaryFile);
                } catch (IOException | SecurityException ignored) {
                    // The original save error is more useful than a temporary-file cleanup error.
                }
            }
        }
    }

    /**
     * Reconstructs one task from its delimiter-separated storage representation.
     *
     * @param taskLine One line read from the storage file.
     * @return Reconstructed task.
     * @throws StorageException If the line has an unknown type, invalid status, or wrong field count.
     */
    private Task parseTask(String taskLine) throws StorageException {
        String[] fields = taskLine.split(FIELD_DELIMITER_REGEX, -1);
        String taskType = fields[0];
        int expectedFieldCount = switch (taskType) {
            case "T" -> 3;
            case "D" -> 4;
            case "E" -> 5;
            default -> throw new StorageException("unknown task type '" + taskType + "'.");
        };

        if (fields.length != expectedFieldCount) {
            throw new StorageException("task type '" + taskType + "' expects "
                    + expectedFieldCount + " fields but found " + fields.length + ".");
        }
        if (!fields[1].equals("0") && !fields[1].equals("1")) {
            throw new StorageException("status must be 0 or 1.");
        }

        String description = decodeDataField(fields[2]);
        if (description.isBlank()) {
            throw new StorageException("task description cannot be empty.");
        }

        Task task = switch (taskType) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description,
                    parseStoredDateTime(fields[3], "deadline date and time"));
            case "E" -> new Event(description,
                    parseStoredDateTime(fields[3], "event start date and time"),
                    parseStoredDateTime(fields[4], "event end date and time"));
            default -> throw new AssertionError("Task type was validated above.");
        };

        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Reverses the escaping used by {@link Task#toDataString()}.
     * Unknown escape sequences remain unchanged for compatibility with older data files.
     *
     * @param encodedField Stored text field.
     * @return Decoded user text.
     */
    private String decodeDataField(String encodedField) {
        StringBuilder decodedField = new StringBuilder();
        for (int i = 0; i < encodedField.length(); i++) {
            char currentCharacter = encodedField.charAt(i);
            if (currentCharacter == '\\' && i + 1 < encodedField.length()) {
                char nextCharacter = encodedField.charAt(i + 1);
                if (nextCharacter == '\\' || nextCharacter == '|') {
                    decodedField.append(nextCharacter);
                    i++;
                    continue;
                }
            }
            decodedField.append(currentCharacter);
        }
        return decodedField.toString();
    }

    /**
     * Ensures a required task-specific text field is not blank.
     *
     * @param value Decoded field value.
     * @param fieldName User-facing name of the field.
     * @return Original non-blank value.
     * @throws StorageException If the field is blank.
     */
    private String requireText(String value, String fieldName) throws StorageException {
        if (value.isBlank()) {
            throw new StorageException(fieldName + " cannot be empty.");
        }
        return value;
    }

    /**
     * Parses one date-time field from the stable format used in the task data file.
     *
     * @param value Stored date-time field.
     * @param fieldName Name used to identify the field in a warning.
     * @return Parsed date and time.
     * @throws StorageException If the field is not a valid stored date and time.
     */
    private LocalDateTime parseStoredDateTime(String value, String fieldName)
            throws StorageException {
        try {
            return DateTimeFormats.parseStorageDateTime(
                    requireText(decodeDataField(value), fieldName));
        } catch (DateTimeParseException e) {
            throw new StorageException(
                    fieldName + " must use yyyy-MM-dd'T'HH:mm format.");
        }
    }

    /**
     * Contains successfully loaded tasks and warnings for data that could not be used.
     */
    public static final class LoadResult {
        private final ArrayList<Task> tasks;
        private final List<String> warnings;

        private LoadResult(ArrayList<Task> tasks, List<String> warnings) {
            this.tasks = tasks;
            this.warnings = List.copyOf(warnings);
        }

        /**
         * Returns the successfully loaded tasks in storage-file order.
         *
         * @return Loaded tasks.
         */
        public ArrayList<Task> getTasks() {
            return tasks;
        }

        /**
         * Returns the non-fatal warnings encountered while loading tasks.
         *
         * @return Unmodifiable list of loading warnings.
         */
        public List<String> getWarnings() {
            return warnings;
        }
    }
}
