package duke;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and saves Wangsa tasks using a human-readable text file.
 *
 * <p>A save is written to a temporary file before replacing the previous version.
 * Reading invalid data reports an error instead of discarding existing tasks.</p>
 */
public class Storage implements TaskRepository {
    private static final String FIELD_SEPARATOR = " | ";

    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the supplied path.
     *
     * @param filePath Save-file location.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads and returns the tasks stored in the data file.
     *
     * @return The saved tasks in their original order.
     * @throws StorageException If the file cannot be read or contains invalid data.
     */
    @Override
    public List<Task> loadTasks() throws StorageException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<Task> loadedTasks = new ArrayList<>();
            for (int i = 0; i < lines.size(); i++) {
                loadedTasks.add(parseTask(lines.get(i), i + 1));
            }
            validateTasks(loadedTasks);
            return loadedTasks;
        } catch (IOException exception) {
            throw new StorageException("I couldn't read " + filePath.toAbsolutePath() + ".\n"
                    + "Close other copies of Wangsa and check that you can open this file, then restart.", exception);
        }
    }

    /**
     * Writes the supplied tasks to disk.
     *
     * @param tasks Tasks to save.
     * @throws StorageException If the data folder or file cannot be written.
     */
    @Override
    public void saveTasks(List<Task> tasks) throws StorageException {
        validateTasks(tasks);
        List<String> lines = tasks.stream().map(this::formatTask).toList();

        Path temporaryFile = null;
        try {
            Path destination = filePath.toAbsolutePath();
            Path parentDirectory = destination.getParent();
            Files.createDirectories(parentDirectory);
            if (Files.exists(destination) && !Files.isWritable(destination)) {
                throw new IOException("The save file is read-only.");
            }
            temporaryFile = Files.createTempFile(parentDirectory, "wangsa-", ".tmp");
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceSaveFile(temporaryFile, destination);
        } catch (IOException exception) {
            throw new StorageException("I couldn't save this change to " + filePath.toAbsolutePath() + ".\n"
                    + "Close other copies of Wangsa. Check folder access and free space, then try again.", exception);
        } finally {
            deleteTemporaryFile(temporaryFile);
        }
    }

    /** Removes a staging file without misreporting an already-completed save as failed. */
    private void deleteTemporaryFile(Path temporaryFile) {
        if (temporaryFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFile);
        } catch (IOException exception) {
            // A leftover staging file is harmless; the save result is determined by replacing the destination.
        }
    }

    /** Replaces the old file atomically where the file system supports it. */
    private void replaceSaveFile(Path temporaryFile, Path destination) throws IOException {
        try {
            Files.move(temporaryFile, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Applies the same capacity and field rules as the task-management code. */
    private void validateTasks(List<Task> tasks) throws StorageException {
        try {
            new TaskList(tasks);
        } catch (WangsaException exception) {
            throw new StorageException("The task list is invalid: " + exception.getMessage()
                    + "\nCheck " + filePath.toAbsolutePath() + " or restore a backup, then restart.", exception);
        }
    }

    /**
     * Converts one task to a line in Wangsa's save-file format.
     */
    private String formatTask(Task task) {
        List<String> fields = new ArrayList<>();
        fields.add(task.getTypeIcon());
        fields.add(task.isDone() ? "1" : "0");
        fields.add(escapeField(task.getDescription()));

        if (task.getType() == TaskType.DEADLINE) {
            fields.add(((Deadline) task).getBy().toString());
        } else if (task.getType() == TaskType.EVENT) {
            Event event = (Event) task;
            fields.add(escapeField(event.getFrom()));
            fields.add(escapeField(event.getTo()));
        }
        return String.join(FIELD_SEPARATOR, fields);
    }

    /**
     * Recreates one task from a line in Wangsa's save-file format.
     */
    private Task parseTask(String line, int lineNumber) throws StorageException {
        List<String> fields = splitFields(line, lineNumber);
        if (fields.size() < 3) {
            throw createInvalidLineException(lineNumber, "not enough fields");
        }

        boolean isDone = parseStatus(fields.get(1), lineNumber);
        String description = fields.get(2);
        if (description.isBlank()) {
            throw createInvalidLineException(lineNumber, "task description cannot be empty");
        }

        Task task = createTask(fields, description, lineNumber);
        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Reads the saved completion flag without accepting other numeric values.
     */
    private boolean parseStatus(String value, int lineNumber) throws StorageException {
        return switch (value) {
            case "1" -> true;
            case "0" -> false;
            default -> throw createInvalidLineException(lineNumber, "status must be 0 or 1");
        };
    }

    /**
     * Validates type-specific fields before constructing a saved task.
     */
    private Task createTask(List<String> fields, String description, int lineNumber) throws StorageException {
        return switch (fields.get(0)) {
            case "T" -> {
                requireFieldCount(fields, 3, lineNumber);
                yield new Todo(description);
            }
            case "D" -> {
                requireFieldCount(fields, 4, lineNumber);
                if (fields.get(3).isEmpty()) {
                    throw createInvalidLineException(lineNumber, "deadline value cannot be empty");
                }
                yield new Deadline(description, parseDeadlineDate(fields.get(3), lineNumber));
            }
            case "E" -> {
                requireFieldCount(fields, 5, lineNumber);
                if (fields.get(3).isBlank() || fields.get(4).isBlank()) {
                    throw createInvalidLineException(lineNumber, "event start and end values cannot be empty");
                }
                yield new Event(description, fields.get(3), fields.get(4));
            }
            default -> throw createInvalidLineException(lineNumber, "unknown task type");
        };
    }

    /**
     * Parses a stored ISO deadline date while retaining line-specific diagnostics.
     */
    private LocalDate parseDeadlineDate(String value, int lineNumber) throws StorageException {
        try {
            if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw createInvalidLineException(lineNumber, "deadline date must use yyyy-MM-dd format");
            }
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw createInvalidLineException(lineNumber, "deadline date must be valid and use yyyy-MM-dd format");
        }
    }

    /**
     * Ensures that a saved task has exactly the fields expected for its type.
     */
    private void requireFieldCount(List<String> fields, int expectedCount, int lineNumber)
            throws StorageException {
        if (fields.size() != expectedCount) {
            throw createInvalidLineException(lineNumber, "unexpected number of fields");
        }
    }

    /**
     * Escapes separator and escape characters that occur in user-entered text.
     */
    private String escapeField(String field) {
        return field.replace("\\", "\\\\").replace("|", "\\|").replace("\n", "\\n").replace("\r", "\\r");
    }

    /**
     * Splits a saved line while preserving escaped separators in user-entered text.
     */
    private List<String> splitFields(String line, int lineNumber) throws StorageException {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean isEscaped = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '|' && !isEscaped) {
                fields.add(decodeField(currentField.toString(), lineNumber));
                currentField.setLength(0);
            } else {
                currentField.append(character);
                isEscaped = character == '\\' && !isEscaped;
            }
        }

        if (isEscaped) {
            throw createInvalidLineException(lineNumber, "unfinished escape sequence");
        }
        fields.add(decodeField(currentField.toString(), lineNumber));
        return fields;
    }

    /** Decodes escapes after removing format padding, preserving escaped line breaks at field edges. */
    private String decodeField(String field, int lineNumber) throws StorageException {
        String encoded = field.trim();
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            char character = encoded.charAt(i);
            if (character == '\\') {
                if (++i == encoded.length()) {
                    throw createInvalidLineException(lineNumber, "unfinished escape sequence");
                }
                character = switch (encoded.charAt(i)) {
                    case '\\', '|' -> encoded.charAt(i);
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> throw createInvalidLineException(lineNumber, "invalid escape sequence");
                };
            }
            decoded.append(character);
        }
        return decoded.toString();
    }

    /**
     * Builds a consistent error for a malformed save-file line.
     */
    private StorageException createInvalidLineException(int lineNumber, String reason) {
        return new StorageException("Saved task data is invalid at line "
                + lineNumber + ": " + reason + ".\n"
                + "Close Wangsa, back up " + filePath.toAbsolutePath()
                + ", then correct that line or restore a backup and restart.");
    }
}
