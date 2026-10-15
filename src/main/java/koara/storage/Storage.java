package koara.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import koara.exception.KoaraException;
import koara.task.Deadline;
import koara.task.Event;
import koara.task.Task;
import koara.task.TaskList;
import koara.task.Todo;

/**
 * Loads tasks from the data file and saves tasks to it.
 */
public class Storage {
    private static final String DATA_SEPARATOR_REGEX = " \\| ";
    private static final String TODO_TYPE = "T";
    private static final String DEADLINE_TYPE = "D";
    private static final String EVENT_TYPE = "E";
    private static final String INCOMPLETE_STATUS = "0";
    private static final String COMPLETE_STATUS = "1";
    private static final String INVALID_SAVED_DATA_ERROR =
            "Walao, Koara found invalid saved task data and could not load it safely.";
    private static final int TASK_TYPE_INDEX = 0;
    private static final int TASK_STATUS_INDEX = 1;
    private static final int TASK_DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_DATE_INDEX = 3;
    private static final int EVENT_START_DATE_INDEX = 3;
    private static final int EVENT_END_DATE_INDEX = 4;
    private static final int MINIMUM_TASK_PART_COUNT = 3;
    private static final int TODO_PART_COUNT = 3;
    private static final int DEADLINE_PART_COUNT = 4;
    private static final int EVENT_PART_COUNT = 5;

    private final Path dataFilePath;

    /**
     * Creates storage that uses the specified data file.
     *
     * @param dataFilePath Path of the data file.
     */
    public Storage(Path dataFilePath) {
        assert dataFilePath != null : "Data file path must not be null";
        this.dataFilePath = dataFilePath;
    }

    /**
     * Loads tasks from the data file.
     *
     * @return Tasks loaded from the data file.
     * @throws KoaraException If the data file cannot be read or contains invalid data.
     */
    public TaskList load() throws KoaraException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(dataFilePath)) {
            return new TaskList(tasks);
        }

        try {
            for (String taskLine : Files.readAllLines(dataFilePath)) {
                Task task = parseStoredTask(taskLine);
                if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task))) {
                    throw new KoaraException(INVALID_SAVED_DATA_ERROR);
                }
                tasks.add(task);
            }
        } catch (IOException | SecurityException exception) {
            throw new KoaraException("Alamak, Koara couldn't load your saved tasks. Your file is untouched.");
        }
        return new TaskList(tasks);
    }

    /**
     * Saves the complete task list to the data file.
     *
     * @param tasks Tasks to save.
     * @throws KoaraException If the task list cannot be written.
     */
    public void save(TaskList tasks) throws KoaraException {
        assert tasks != null : "Task list must not be null";
        try {
            StorageFile.writeLines(dataFilePath, tasks.toDataLines());
        } catch (IOException | SecurityException exception) {
            throw new KoaraException("Alamak, Koara couldn't save your tasks. Please try again.");
        }
    }

    /**
     * Reconstructs a task from one line of saved task data.
     *
     * @param taskLine Saved task data to parse.
     * @return Task represented by the saved data.
     * @throws KoaraException If the saved data is invalid.
     */
    private Task parseStoredTask(String taskLine) throws KoaraException {
        assert taskLine != null : "Stored task line must not be null";
        String[] taskParts = taskLine.split(DATA_SEPARATOR_REGEX, -1);
        if (taskParts.length < MINIMUM_TASK_PART_COUNT || taskParts[TASK_DESCRIPTION_INDEX].isEmpty()) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }

        Task task = createTask(taskParts);
        applyCompletionStatus(task, taskParts[TASK_STATUS_INDEX]);
        return task;
    }

    private Task createTask(String[] taskParts) throws KoaraException {
        String taskType = taskParts[TASK_TYPE_INDEX];
        switch (taskType) {
            case TODO_TYPE:
                return createTodo(taskParts);
            case DEADLINE_TYPE:
                return createDeadline(taskParts);
            case EVENT_TYPE:
                return createEvent(taskParts);
            default:
                throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
    }

    private Todo createTodo(String[] taskParts) throws KoaraException {
        if (taskParts.length != TODO_PART_COUNT) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
        return new Todo(taskParts[TASK_DESCRIPTION_INDEX]);
    }

    private Deadline createDeadline(String[] taskParts) throws KoaraException {
        boolean hasInvalidDate = taskParts.length != DEADLINE_PART_COUNT
                || taskParts[DEADLINE_DATE_INDEX].isEmpty();
        if (hasInvalidDate) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
        LocalDate dueDate = parseDate(taskParts[DEADLINE_DATE_INDEX]);
        return new Deadline(taskParts[TASK_DESCRIPTION_INDEX], dueDate);
    }

    private Event createEvent(String[] taskParts) throws KoaraException {
        boolean hasInvalidDates = taskParts.length != EVENT_PART_COUNT
                || taskParts[EVENT_START_DATE_INDEX].isEmpty()
                || taskParts[EVENT_END_DATE_INDEX].isEmpty();
        if (hasInvalidDates) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
        LocalDate startDate = parseDate(taskParts[EVENT_START_DATE_INDEX]);
        LocalDate endDate = parseDate(taskParts[EVENT_END_DATE_INDEX]);
        if (startDate.isAfter(endDate)) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
        return new Event(taskParts[TASK_DESCRIPTION_INDEX], startDate, endDate);
    }

    private void applyCompletionStatus(Task task, String completionStatus) throws KoaraException {
        if (completionStatus.equals(COMPLETE_STATUS)) {
            task.markAsDone();
            return;
        }
        if (!completionStatus.equals(INCOMPLETE_STATUS)) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
    }

    /**
     * Parses a date stored in the data file.
     *
     * @param dateText Stored date text to parse.
     * @return Parsed date.
     * @throws KoaraException If the stored date is invalid.
     */
    private static LocalDate parseDate(String dateText) throws KoaraException {
        assert dateText != null && !dateText.isBlank() : "Stored date text must not be blank";
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new KoaraException(INVALID_SAVED_DATA_ERROR);
        }
    }
}
