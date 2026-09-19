package yuno.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

import yuno.exception.FileStorageException;
import yuno.exception.InvalidTaskNumberException;
import yuno.task.Deadline;
import yuno.task.Event;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.task.Todo;
import yuno.util.DateTimeFormats;

/**
 * Loads and saves Yuno tasks in a local text file.
 */
public class Storage {
    /** Default path of the file used to store task data. */
    private static final Path DEFAULT_FILE_PATH = Path.of("./data/yuno.txt");
    /** Path of the file used by this storage instance. */
    private final Path filePath;

    /**
     * Initializes storage and creates the task data file if it does not exist.
     *
     * @throws FileStorageException If the task data file cannot be initialized.
     */
    public Storage() throws FileStorageException {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Initializes storage at the specified path and creates its data file if necessary.
     *
     * @param filePath Path of the task data file.
     * @throws FileStorageException If the task data file cannot be initialized.
     */
    public Storage(Path filePath) throws FileStorageException {
        this.filePath = filePath.toAbsolutePath();
        try {
            Path parent = this.filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.notExists(this.filePath)) {
                Files.createFile(this.filePath);
            } else if (!Files.isRegularFile(this.filePath)) {
                throw new IOException("Storage path is not a regular file");
            }
        } catch (IOException | SecurityException exception) {
            throw new FileStorageException(
                    "I can't initialize your task file. Check the data folder before bothering me again.",
                    exception);
        }
    }

    /**
     * Loads tasks from the task data file into the specified task list.
     *
     * @param taskList Task list into which saved tasks are loaded.
     * @throws FileStorageException If the task data file cannot be read or contains malformed task data.
     */
    public void load(TaskList taskList) throws FileStorageException {
        TaskList loadedTaskList = new TaskList();
        try {
            List<String> lines = Files.readAllLines(filePath);
            for (int i = 0; i < lines.size(); i++) {
                try {
                    loadedTaskList.addTask(parseTask(lines.get(i)));
                } catch (FileStorageException exception) {
                    throw createInvalidDataException(i + 1, exception);
                }
            }
        } catch (IOException | SecurityException exception) {
            throw new FileStorageException(
                    "I can't read your task file. Did you move it while I wasn't looking?",
                    exception);
        }
        taskList.replaceWith(loadedTaskList);
    }

    /**
     * Rewrites the task data file with all tasks in the specified task list.
     *
     * @param taskList Task list to save.
     * @throws FileStorageException If the task data file cannot be written.
     * @throws InvalidTaskNumberException If a task cannot be retrieved from the task list.
     */
    public void save(TaskList taskList) throws FileStorageException, InvalidTaskNumberException {
        Path temporaryFilePath = null;
        try {
            temporaryFilePath = Files.createTempFile(filePath.getParent(), "yuno-", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryFilePath)) {
                for (int i = 0; i < taskList.getCount(); i++) {
                    writer.write(taskList.getTask(i + 1).toStorageString());
                    writer.newLine();
                }
            }
            replaceStorageFile(temporaryFilePath);
        } catch (IOException | SecurityException exception) {
            throw new FileStorageException(
                    "I can't save your tasks. Check the task file before bothering me again.",
                    exception);
        } finally {
            deleteTemporaryFile(temporaryFilePath);
        }
    }

    /**
     * Replaces the storage file with a fully written temporary file.
     *
     * @param temporaryFilePath Temporary file containing all task data.
     * @throws IOException If the temporary file cannot replace the storage file.
     */
    private void replaceStorageFile(Path temporaryFilePath) throws IOException {
        try {
            Files.move(
                    temporaryFilePath,
                    filePath,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFilePath, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Deletes a temporary storage file left behind by an unsuccessful save.
     *
     * @param temporaryFilePath Temporary file to delete, or {@code null} if none was created.
     */
    private void deleteTemporaryFile(Path temporaryFilePath) {
        if (temporaryFilePath == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporaryFilePath);
        } catch (IOException | SecurityException exception) {
            // A cleanup failure should not hide the save failure already being reported.
        }
    }

    /**
     * Returns the task represented by one line of storage data.
     * Descriptions may contain the storage delimiter, but deadline and event time fields may not.
     *
     * @param line Storage line to parse.
     * @return Task represented by the storage line.
     * @throws FileStorageException If the line does not follow a supported storage format.
     */
    private Task parseTask(String line) throws FileStorageException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 2) {
            throw createInvalidDataException();
        }
        boolean isDone = parseCompletionStatus(parts[1]);
        return switch (parts[0]) {
            case "T" -> parseTodo(parts, isDone);
            case "D" -> parseDeadline(parts, isDone);
            case "E" -> parseEvent(parts, isDone);
            default -> throw createInvalidDataException();
        };
    }

    /**
     * Returns a to-do task represented by the specified storage fields.
     *
     * @param parts Storage fields to parse.
     * @param isDone Whether the stored task is completed.
     * @return To-do task represented by the fields.
     * @throws FileStorageException If the fields do not contain a description.
     */
    private Task parseTodo(String[] parts, boolean isDone) throws FileStorageException {
        if (parts.length < 3) {
            throw createInvalidDataException();
        }
        String description = combineParts(parts, 2, parts.length);
        if (description.isBlank()) {
            throw createInvalidDataException();
        }
        return new Todo(description, isDone);
    }

    /**
     * Returns a deadline task represented by the specified storage fields.
     *
     * @param parts Storage fields to parse.
     * @param isDone Whether the stored task is completed.
     * @return Deadline task represented by the fields.
     * @throws FileStorageException If the fields do not contain a description and deadline.
     */
    private Task parseDeadline(String[] parts, boolean isDone) throws FileStorageException {
        if (parts.length < 4) {
            throw createInvalidDataException();
        }
        String description = combineParts(parts, 2, parts.length - 1);
        LocalDateTime deadlineDateTime = parseStoredDateTime(parts[parts.length - 1]);
        if (description.isBlank()) {
            throw createInvalidDataException();
        }
        return new Deadline(description, isDone, deadlineDateTime);
    }

    /**
     * Returns an event task represented by the specified storage fields.
     *
     * @param parts Storage fields to parse.
     * @param isDone Whether the stored task is completed.
     * @return Event task represented by the fields.
     * @throws FileStorageException If the fields do not contain a description, start time, and end time.
     */
    private Task parseEvent(String[] parts, boolean isDone) throws FileStorageException {
        if (parts.length < 5) {
            throw createInvalidDataException();
        }
        String description = combineParts(parts, 2, parts.length - 2);
        LocalDateTime startDateTime = parseStoredDateTime(parts[parts.length - 2]);
        LocalDateTime endDateTime = parseStoredDateTime(parts[parts.length - 1]);
        if (description.isBlank() || !startDateTime.isBefore(endDateTime)) {
            throw createInvalidDataException();
        }
        return new Event(description, isDone, startDateTime, endDateTime);
    }

    /**
     * Returns whether the specified storage status represents a completed task.
     *
     * @param status Storage status to interpret.
     * @return True if the status is {@code X}; false if it is a single space.
     * @throws FileStorageException If the status is not recognized.
     */
    private boolean parseCompletionStatus(String status) throws FileStorageException {
        if (status.equals("X")) {
            return true;
        } else if (status.equals(" ")) {
            return false;
        }
        throw createInvalidDataException();
    }

    /**
     * Returns the date-time represented by the specified storage value.
     *
     * @param dateTimeText Stored date-time text to parse.
     * @return Parsed date-time.
     * @throws FileStorageException If the value does not follow the storage format.
     */
    private LocalDateTime parseStoredDateTime(String dateTimeText) throws FileStorageException {
        try {
            return LocalDateTime.parse(dateTimeText, DateTimeFormats.STORAGE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw createInvalidDataException();
        }
    }

    /**
     * Returns the storage fields in the specified range joined as one value.
     *
     * @param parts Storage fields to combine.
     * @param startIndex Inclusive index of the first field to combine.
     * @param endIndex Exclusive index after the last field to combine.
     * @return Combined field value.
     */
    private String combineParts(String[] parts, int startIndex, int endIndex) {
        assert parts != null : "Storage fields must not be null";
        assert startIndex >= 0 && startIndex < endIndex && endIndex <= parts.length
                : "Storage field range must be valid";
        return String.join(" | ", Arrays.copyOfRange(parts, startIndex, endIndex));
    }

    /**
     * Returns an exception that reports malformed task data in the storage file.
     *
     * @return Exception containing Yuno's user-facing error message.
     */
    private FileStorageException createInvalidDataException() {
        return new FileStorageException("Why did you change the task file? I can't load your tasks now.");
    }

    /**
     * Returns an exception that identifies the malformed line in the storage file.
     *
     * @param lineNumber One-based number of the malformed line.
     * @param cause Parsing failure caused by the malformed line.
     * @return Exception containing a user-facing error message and the parsing failure.
     */
    private FileStorageException createInvalidDataException(int lineNumber, FileStorageException cause) {
        return new FileStorageException(
                "Why did you change the task file? I can't load line " + lineNumber + ".",
                cause);
    }
}
