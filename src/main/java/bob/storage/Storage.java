package bob.storage;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.Task;
import bob.task.ToDo;

/**
 * Loads tasks from and saves tasks to a data file.
 */
public class Storage {
    private static final int TASK_TYPE_FIELD_INDEX = 0;
    private static final int COMPLETION_STATUS_FIELD_INDEX = 1;
    private static final int DESCRIPTION_FIELD_INDEX = 2;
    private static final int DEADLINE_DATE_FIELD_INDEX = 3;
    private static final int EVENT_START_DATE_FIELD_INDEX = 3;
    private static final int EVENT_END_DATE_FIELD_INDEX = 4;
    private static final int MINIMUM_FIELD_COUNT = 3;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    /**
     * Represents a completion status encoded in the data file.
     */
    private enum CompletionStatus {
        INCOMPLETE("0", false),
        COMPLETE("1", true);

        private final String storageCode;
        private final boolean isTaskDone;

        /**
         * Creates a completion status with its storage representation.
         *
         * @param storageCode Value used in the data file.
         * @param isTaskDone Whether this status represents a completed task.
         */
        CompletionStatus(String storageCode, boolean isTaskDone) {
            this.storageCode = storageCode;
            this.isTaskDone = isTaskDone;
        }

        /**
         * Returns the completion status represented by a storage code.
         *
         * @param storageCode Value read from the data file.
         * @return Completion status represented by the value.
         * @throws BobException If the value does not represent a completion status.
         */
        private static CompletionStatus fromStorageCode(String storageCode) throws BobException {
            for (CompletionStatus completionStatus : values()) {
                if (completionStatus.storageCode.equals(storageCode)) {
                    return completionStatus;
                }
            }
            throw new BobException("Completion status must be 0 or 1.");
        }

        /**
         * Returns whether this status represents a completed task.
         *
         * @return True if the task is completed.
         */
        private boolean isTaskDone() {
            return isTaskDone;
        }
    }

    /**
     * Represents a task type encoded in the data file.
     */
    private enum TaskType {
        TODO("T"),
        DEADLINE("D"),
        EVENT("E");

        private final String storageCode;

        /**
         * Creates a task type with its storage representation.
         *
         * @param storageCode Value used in the data file.
         */
        TaskType(String storageCode) {
            this.storageCode = storageCode;
        }

        /**
         * Returns the task type represented by a storage code.
         *
         * @param storageCode Value read from the data file.
         * @return Task type represented by the value.
         * @throws BobException If the value does not represent a task type.
         */
        private static TaskType fromStorageCode(String storageCode) throws BobException {
            for (TaskType taskType : values()) {
                if (taskType.storageCode.equals(storageCode)) {
                    return taskType;
                }
            }
            throw new BobException("Unknown task type: " + storageCode);
        }
    }

    private final Path filePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param filePath Path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    // Used Codex to help write this method.
    /**
     * Saves tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws BobException If the tasks cannot be saved.
     */
    public void saveTasks(List<Task> tasks) throws BobException {
        try {
            createParentDirectory();
            writeTasks(tasks);
        } catch (IOException exception) {
            throw new BobException("Unable to save tasks :( " + exception.getMessage());
        }
    }

    // Used Codex to help write this method.
    /**
     * Loads tasks from the data file.
     *
     * @return Tasks reconstructed from the data file.
     * @throws BobException If the tasks cannot be loaded.
     */
    public List<Task> loadTasks() throws BobException {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return parseTasksFromDataLines(lines);
        } catch (IOException exception) {
            throw new BobException("Unable to load tasks :( " + exception.getMessage());
        }
    }

    /**
     * Creates the directory containing the data file when one is specified.
     *
     * @throws IOException If the directory cannot be created.
     */
    private void createParentDirectory() throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
    }

    /**
     * Writes the supplied tasks to the data file.
     *
     * @param tasks Tasks to write.
     * @throws IOException If a task cannot be written.
     */
    private void writeTasks(List<Task> tasks) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath, StandardCharsets.UTF_8)) {
            for (Task task : tasks) {
                writer.write(task.toDataString());
                writer.newLine();
            }
        }
    }

    /**
     * Converts data-file lines into tasks in their stored order.
     *
     * @param dataLines Lines read from the data file.
     * @return Tasks represented by the data-file lines.
     * @throws BobException If a line does not follow the expected format.
     */
    private List<Task> parseTasksFromDataLines(List<String> dataLines) throws BobException {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < dataLines.size(); i++) {
            int lineNumber = i + 1;
            Task task = parseTaskWithLineContext(dataLines.get(i), lineNumber);
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * Converts a data-file line into a task and identifies invalid input by line number.
     *
     * @param dataLine Data-file line to convert.
     * @param lineNumber One-based number of the data-file line.
     * @return Task represented by the data-file line.
     * @throws BobException If the data-file line does not follow the expected format.
     */
    private Task parseTaskWithLineContext(String dataLine, int lineNumber) throws BobException {
        try {
            return parseTaskFromDataLine(dataLine);
        } catch (BobException exception) {
            throw new BobException("Invalid data on line " + lineNumber
                    + ": " + exception.getMessage());
        }
    }

    // Used Codex to help write this method.
    /**
     * Converts a line from the data file into a task.
     *
     * @param dataLine Data-file line to convert.
     * @return Task represented by the data-file line.
     * @throws BobException If the data-file line does not follow the expected format.
     */
    private Task parseTaskFromDataLine(String dataLine) throws BobException {
        String[] taskFields = dataLine.split(" \\| ", -1);
        validateMinimumFieldCount(taskFields);

        CompletionStatus completionStatus = CompletionStatus.fromStorageCode(
                taskFields[COMPLETION_STATUS_FIELD_INDEX]);
        boolean isTaskDone = completionStatus.isTaskDone();
        Task task = createTaskFromFields(taskFields);
        if (isTaskDone) {
            task.markTask();
        }

        return task;
    }

    /**
     * Validates that a data-file line contains the common task fields.
     *
     * @param taskFields Fields extracted from a data-file line.
     * @throws BobException If the line does not contain all common fields.
     */
    private void validateMinimumFieldCount(String[] taskFields) throws BobException {
        if (taskFields.length < MINIMUM_FIELD_COUNT) {
            throw new BobException("Not enough fields.");
        }
    }

    /**
     * Creates the task represented by a collection of stored fields.
     *
     * @param taskFields Fields extracted from a data-file line.
     * @return Task represented by the fields.
     * @throws BobException If the task type, field count, or date is invalid.
     */
    private Task createTaskFromFields(String[] taskFields) throws BobException {
        TaskType taskType = TaskType.fromStorageCode(taskFields[TASK_TYPE_FIELD_INDEX]);
        try {
            return switch (taskType) {
                case TODO -> createToDoFromFields(taskFields);
                case DEADLINE -> createDeadlineFromFields(taskFields);
                case EVENT -> createEventFromFields(taskFields);
            };
        } catch (DateTimeParseException exception) {
            throw new BobException("Invalid date. Use yyyy-MM-dd.");
        }
    }

    /**
     * Creates a to-do task from stored fields.
     *
     * @param taskFields Fields extracted from a data-file line.
     * @return To-do task represented by the fields.
     * @throws BobException If the number of fields is invalid.
     */
    private Task createToDoFromFields(String[] taskFields) throws BobException {
        if (taskFields.length != TODO_FIELD_COUNT) {
            throw new BobException("A to-do task must have 3 fields.");
        }
        return new ToDo(taskFields[DESCRIPTION_FIELD_INDEX]);
    }

    /**
     * Creates a deadline task from stored fields.
     *
     * @param taskFields Fields extracted from a data-file line.
     * @return Deadline task represented by the fields.
     * @throws BobException If the number of fields is invalid.
     */
    private Task createDeadlineFromFields(String[] taskFields) throws BobException {
        if (taskFields.length != DEADLINE_FIELD_COUNT) {
            throw new BobException("A deadline must have 4 fields.");
        }
        LocalDate dueDate = LocalDate.parse(taskFields[DEADLINE_DATE_FIELD_INDEX]);
        return new Deadline(taskFields[DESCRIPTION_FIELD_INDEX], dueDate);
    }

    /**
     * Creates an event task from stored fields.
     *
     * @param taskFields Fields extracted from a data-file line.
     * @return Event task represented by the fields.
     * @throws BobException If the number of fields is invalid.
     */
    private Task createEventFromFields(String[] taskFields) throws BobException {
        if (taskFields.length != EVENT_FIELD_COUNT) {
            throw new BobException("An event must have 5 fields.");
        }
        LocalDate startDate = LocalDate.parse(taskFields[EVENT_START_DATE_FIELD_INDEX]);
        LocalDate endDate = LocalDate.parse(taskFields[EVENT_END_DATE_FIELD_INDEX]);
        return new Event(taskFields[DESCRIPTION_FIELD_INDEX], startDate, endDate);
    }
}
