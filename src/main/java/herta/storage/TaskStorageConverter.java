package herta.storage;

import java.util.ArrayList;
import java.util.List;

import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.Task;
import herta.task.TaskList;
import herta.task.Todo;

/**
 * Converts tasks to and from Herta's line-based storage format.
 *
 * <p>This class validates records without performing any file operations.</p>
 */
final class TaskStorageConverter {
    private static final int TYPE_INDEX = 0;
    private static final int STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int FIRST_DATE_INDEX = 3;
    private static final int SECOND_DATE_INDEX = 4;
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETED_STATUS = "1";
    private static final int MINIMUM_PART_COUNT = 2;
    private static final int TODO_PART_COUNT = 3;
    private static final int DEADLINE_PART_COUNT = 4;
    private static final int EVENT_PART_COUNT = 5;

    /**
     * Converts every task to a validated storage record.
     *
     * @param tasks the task list to serialize.
     * @return validated storage records in task-list order.
     * @throws HertaException if the task list or one of its records is invalid.
     */
    List<String> serializeTasks(TaskList tasks) throws HertaException {
        if (tasks == null) {
            throw new HertaException("Failed to save tasks: task list is null.");
        }

        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(serializeTask(task));
            if (lines.size() > TaskList.MAXIMUM_TASK_COUNT) {
                throw new HertaException("Failed to save tasks: too many tasks.");
            }
        }
        return lines;
    }

    /**
     * Reconstructs all tasks from the lines in the data file.
     *
     * @param lines the lines read from the data file.
     * @param recordPrefix the prefix for malformed-record failures.
     * @return the reconstructed task list.
     * @throws HertaException if a line contains an invalid task record.
     */
    TaskList parseStorageLines(List<String> lines, String recordPrefix) throws HertaException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                throw new HertaException(recordPrefix + "at line " + (i + 1)
                        + ": blank records are not supported.");
            }
            Task task = parseStorageLine(line, i + 1, recordPrefix);
            try {
                tasks.add(task);
            } catch (IllegalArgumentException e) {
                throw new HertaException(recordPrefix + "at line " + (i + 1)
                        + ": " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Converts and validates one task as a storage record.
     *
     * @param task the task to serialize.
     * @return the validated storage record.
     * @throws HertaException if the task or its record is invalid.
     */
    private String serializeTask(Task task) throws HertaException {
        if (task == null) {
            throw new HertaException("Failed to save tasks: task list contains a null task.");
        }

        final String storageString;
        try {
            storageString = task.toStorageString();
        } catch (RuntimeException e) {
            throw new HertaException("Failed to save tasks: task contains invalid data.");
        }
        if (storageString == null) {
            throw new HertaException("Failed to save tasks: task contains invalid data.");
        }
        if (storageString.contains("\n") || storageString.contains("\r")) {
            throw new HertaException("Failed to save tasks: task fields cannot contain line breaks.");
        }
        validateSerializedTask(storageString);
        return storageString;
    }

    /**
     * Confirms that a serialized task can be reconstructed before it is written.
     *
     * @param storageString the candidate storage record.
     * @throws HertaException if the record is invalid.
     */
    private void validateSerializedTask(String storageString) throws HertaException {
        try {
            Task parsedTask = parseStoredTask(storageString);
            if (parsedTask == null) {
                throw new HertaException("Invalid saved task: no task was reconstructed.");
            }
        } catch (HertaException e) {
            throw new HertaException("Failed to save tasks: " + e.getMessage());
        }
    }

    /**
     * Parses one storage line and adds its source line number to any error.
     *
     * @param line one line in Herta's storage format.
     * @param lineNumber the one-based source line number.
     * @param recordPrefix the prefix for malformed-record failures.
     * @return the reconstructed task.
     * @throws HertaException if the line does not contain a valid task record.
     */
    private Task parseStorageLine(String line, int lineNumber, String recordPrefix)
            throws HertaException {
        try {
            Task task = parseStoredTask(line);
            if (task == null) {
                throw new HertaException("Invalid saved task: no task was reconstructed.");
            }
            return task;
        } catch (HertaException e) {
            throw new HertaException(recordPrefix + "at line "
                    + lineNumber + ": " + e.getMessage());
        }
    }

    /**
     * Reconstructs a task from one serialized data-file line.
     *
     * @param line one line in Herta's storage format.
     * @return the reconstructed task.
     * @throws HertaException if the line does not contain a supported task type.
     */
    private Task parseStoredTask(String line) throws HertaException {
        String[] storageFields = splitStorageRecord(line);
        validateStorageFields(storageFields);
        Task task = createTask(storageFields);
        if (hasCompletedStatus(storageFields)) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Splits a serialized task line into its storage fields.
     *
     * @param line one serialized task line.
     * @return the normalized storage fields.
     */
    private String[] splitStorageRecord(String line) {
        String normalizedLine = line.trim();
        if (normalizedLine.startsWith("\uFEFF")) {
            normalizedLine = normalizedLine.substring(1).trim();
        }
        return normalizedLine.split("\\s*\\|\\s*", -1);
    }

    /** Validates all structural and field values in a serialized task record. */
    private void validateStorageFields(String[] storageFields) throws HertaException {
        if (storageFields.length < MINIMUM_PART_COUNT) {
            throw new HertaException("Invalid saved task: missing task type or status.");
        }

        String type = storageFields[TYPE_INDEX];
        int expectedPartCount = getExpectedPartCount(type);
        validatePartCount(storageFields, type, expectedPartCount);
        validateStatus(storageFields[STATUS_INDEX]);
        validateTaskFields(storageFields);
    }

    /** Returns whether a validated record marks its task as complete. */
    private boolean hasCompletedStatus(String[] storageFields) {
        return COMPLETED_STATUS.equals(storageFields[STATUS_INDEX]);
    }

    /**
     * Returns the expected number of fields for a serialized task type.
     *
     * @param type the serialized task type.
     * @return the expected field count.
     * @throws HertaException if the task type is unsupported.
     */
    private int getExpectedPartCount(String type) throws HertaException {
        return switch (type) {
            case TODO_TYPE -> TODO_PART_COUNT;
            case DEADLINE_TYPE -> DEADLINE_PART_COUNT;
            case EVENT_TYPE -> EVENT_PART_COUNT;
            default -> throw new HertaException("Invalid saved task: unknown task type '"
                    + type + "'.");
        };
    }

    /**
     * Checks that a serialized task contains the expected number of fields.
     *
     * @param storageFields the serialized task fields.
     * @param type the serialized task type.
     * @param expectedPartCount the expected field count.
     * @throws HertaException if the field count is invalid.
     */
    private void validatePartCount(String[] storageFields, String type, int expectedPartCount)
            throws HertaException {
        if (storageFields.length != expectedPartCount) {
            throw new HertaException("Invalid saved task: type " + type
                    + " requires " + expectedPartCount + " fields.");
        }
    }

    /**
     * Checks that a serialized task has a supported completion status.
     *
     * @param status the serialized completion status.
     * @throws HertaException if the status is invalid.
     */
    private void validateStatus(String status) throws HertaException {
        if (!INCOMPLETE_STATUS.equals(status) && !COMPLETED_STATUS.equals(status)) {
            throw new HertaException("Invalid saved task: completion status must be "
                    + INCOMPLETE_STATUS + " or " + COMPLETED_STATUS + ".");
        }
    }

    /**
     * Checks that serialized task fields after the status are non-blank.
     *
     * @param storageFields the serialized task fields.
     * @throws HertaException if a task field is blank.
     */
    private void validateTaskFields(String[] storageFields) throws HertaException {
        for (int i = DESCRIPTION_INDEX; i < storageFields.length; i++) {
            if (storageFields[i].isBlank()) {
                throw new HertaException("Invalid saved task: task fields cannot be blank.");
            }
        }
    }

    /**
     * Reconstructs a task from validated storage fields.
     *
     * @param storageFields the validated storage fields.
     * @return the reconstructed task.
     * @throws HertaException if the task type cannot be reconstructed.
     */
    private Task createTask(String[] storageFields) throws HertaException {
        final Task task;
        try {
            task = switch (storageFields[TYPE_INDEX]) {
                case TODO_TYPE -> new Todo(storageFields[DESCRIPTION_INDEX]);
                case DEADLINE_TYPE -> Deadline.fromStorage(storageFields[DESCRIPTION_INDEX],
                        storageFields[FIRST_DATE_INDEX]);
                case EVENT_TYPE -> Event.fromStorage(storageFields[DESCRIPTION_INDEX],
                        storageFields[FIRST_DATE_INDEX], storageFields[SECOND_DATE_INDEX]);
                default -> throw new HertaException("Invalid saved task: unknown task type '"
                        + storageFields[TYPE_INDEX] + "'.");
            };
        } catch (IllegalArgumentException e) {
            throw new HertaException(e.getMessage());
        }
        return task;
    }
}
