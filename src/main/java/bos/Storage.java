package bos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a text file on the hard disk.
 */
public class Storage {
    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;
    private static final int TASK_TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_INDEX = 3;
    private static final int EVENT_START_INDEX = 3;
    private static final int EVENT_END_INDEX = 4;
    private final Path taskFile;

    /**
     * Creates storage that uses the file at the given path.
     *
     * @param filePath path of the task data file.
     */
    public Storage(String filePath) {
        this.taskFile = Path.of(filePath).toAbsolutePath().normalize();
    }

    /**
     * Loads all tasks from the data file.
     *
     * <p>If the file or its parent directory does not exist, this method creates
     * them and returns an empty task list.</p>
     *
     * @return tasks stored in the data file.
     * @throws IOException if the file cannot be created or read.
     * @throws BosException if a line does not follow the storage format.
     */
    public ArrayList<Task> loadTasks() throws IOException, BosException {
        createFileIfMissing();
        ArrayList<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(taskFile, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                Task task = parseTask(line, lineNumber);
                boolean isDuplicate = tasks.stream()
                        .anyMatch(existingTask -> existingTask.hasSameDescription(task));
                if (isDuplicate) {
                    throw createInvalidDataException(lineNumber, "duplicate task description");
                }
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * Replaces the data file contents with the current task list.
     *
     * @param tasks tasks to save.
     * @throws IOException if the file cannot be created or written.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        createFileIfMissing();
        Path parentDirectory = taskFile.getParent();
        Path temporaryFile = Files.createTempFile(
                parentDirectory,
                taskFile.getFileName().toString() + ".",
                ".tmp");
        boolean isMoved = false;

        try {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    temporaryFile,
                    StandardCharsets.UTF_8)) {
                for (Task task : tasks) {
                    writer.write(task.formatForStorage());
                    writer.newLine();
                }
            }
            moveIntoPlace(temporaryFile);
            isMoved = true;
        } finally {
            if (!isMoved) {
                Files.deleteIfExists(temporaryFile);
            }
        }
    }

    /**
     * Creates the parent directory and data file when they do not exist.
     */
    private void createFileIfMissing() throws IOException {
        Path parentDirectory = taskFile.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }

        if (Files.exists(taskFile) && !Files.isRegularFile(taskFile)) {
            throw new IOException("Task storage path is not a regular file: " + taskFile);
        }

        if (Files.notExists(taskFile)) {
            try {
                Files.createFile(taskFile);
            } catch (FileAlreadyExistsException exception) {
                // Another process created the file after the existence check.
            }
        }
    }

    /**
     * Atomically replaces the task file when the file system supports it.
     */
    private void moveIntoPlace(Path temporaryFile) throws IOException {
        try {
            Files.move(
                    temporaryFile,
                    taskFile,
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, taskFile, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts one validated storage line into a task.
     *
     * @param line stored task record.
     * @param lineNumber line number used in error messages.
     * @return task represented by the line.
     * @throws BosException if the record has invalid or missing fields.
     */
    private Task parseTask(String line, int lineNumber) throws BosException {
        String[] fields = line.split("\\|", -1);
        validateCommonFields(fields, lineNumber);

        Task task = createTask(fields, lineNumber);
        String status = fields[STATUS_INDEX].trim();
        if (status.equals(Task.COMPLETED_STORAGE_STATUS)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Validates the fields shared by every stored task.
     */
    private void validateCommonFields(String[] fields, int lineNumber) throws BosException {
        if (fields.length < MINIMUM_FIELD_COUNT) {
            throw createInvalidDataException(lineNumber, "too few fields");
        }

        String status = fields[STATUS_INDEX].trim();
        boolean isRecognizedStatus = status.equals(Task.INCOMPLETE_STORAGE_STATUS)
                || status.equals(Task.COMPLETED_STORAGE_STATUS);
        if (!isRecognizedStatus) {
            throw createInvalidDataException(lineNumber, "status must be 0 or 1");
        }

        getRequiredField(fields, DESCRIPTION_INDEX, lineNumber, "task description");
    }

    /**
     * Creates the task represented by validated storage fields.
     */
    private Task createTask(String[] fields, int lineNumber) throws BosException {
        String taskType = fields[TASK_TYPE_INDEX].trim();
        String description = getRequiredField(
                fields,
                DESCRIPTION_INDEX,
                lineNumber,
                "task description");
        return switch (taskType) {
            case TodoTask.STORAGE_TYPE -> createTodoTask(fields, description, lineNumber);
            case Deadline.STORAGE_TYPE -> createDeadlineTask(fields, description, lineNumber);
            case Event.STORAGE_TYPE -> createEventTask(fields, description, lineNumber);
            default -> throw createInvalidDataException(lineNumber, "unknown task type");
        };
    }

    /**
     * Creates a todo task from storage fields.
     */
    private Task createTodoTask(String[] fields, String description, int lineNumber)
            throws BosException {
        requireFieldCount(fields, TODO_FIELD_COUNT, lineNumber);
        return new TodoTask(description);
    }

    /**
     * Creates a deadline task from storage fields.
     */
    private Task createDeadlineTask(String[] fields, String description, int lineNumber)
            throws BosException {
        requireFieldCount(fields, DEADLINE_FIELD_COUNT, lineNumber);
        String deadline = getRequiredField(fields, DEADLINE_INDEX, lineNumber, "deadline");
        validateStoredDateTime(deadline, "deadline", lineNumber);
        return new Deadline(description, deadline);
    }

    /**
     * Creates an event task from storage fields.
     */
    private Task createEventTask(String[] fields, String description, int lineNumber)
            throws BosException {
        requireFieldCount(fields, EVENT_FIELD_COUNT, lineNumber);
        String startTime = getRequiredField(fields, EVENT_START_INDEX, lineNumber, "event start time");
        String endTime = getRequiredField(fields, EVENT_END_INDEX, lineNumber, "event end time");
        validateStoredEventTimes(startTime, endTime, lineNumber);
        return new Event(description, startTime, endTime);
    }

    /**
     * Checks that a record contains exactly the expected number of fields.
     */
    private void requireFieldCount(String[] fields, int expectedCount, int lineNumber)
            throws BosException {
        if (fields.length != expectedCount) {
            throw createInvalidDataException(lineNumber, "expected " + expectedCount + " fields");
        }
    }

    /**
     * Returns a trimmed required field or reports that it is empty.
     */
    private String getRequiredField(String[] fields, int fieldIndex, int lineNumber, String fieldName)
            throws BosException {
        String field = fields[fieldIndex].strip();
        if (field.isBlank()) {
            throw createInvalidDataException(lineNumber, fieldName + " is empty");
        }

        try {
            return Parser.normalizeTaskField(field);
        } catch (BosException exception) {
            throw createInvalidDataException(lineNumber, fieldName + " contains unsupported characters");
        }
    }

    /**
     * Converts a date-time validation failure into a storage error with a line number.
     */
    private void validateStoredDateTime(String dateTime, String fieldName, int lineNumber)
            throws BosException {
        try {
            Parser.validateDateTime(dateTime, fieldName);
        } catch (BosException exception) {
            throw createInvalidDataException(lineNumber, exception.getMessage());
        }
    }

    /**
     * Converts an event range validation failure into a storage error with a line number.
     */
    private void validateStoredEventTimes(String startTime, String endTime, int lineNumber)
            throws BosException {
        try {
            Parser.validateEventTimes(startTime, endTime);
        } catch (BosException exception) {
            throw createInvalidDataException(lineNumber, exception.getMessage());
        }
    }

    /**
     * Creates a consistent exception for malformed storage data.
     */
    private BosException createInvalidDataException(int lineNumber, String reason) {
        return new BosException("invalid data on line " + lineNumber + ": " + reason);
    }
}
