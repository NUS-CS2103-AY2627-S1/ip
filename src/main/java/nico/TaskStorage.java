package nico;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes tasks using Nico's saved-file format.
 */
public final class TaskStorage {
    private static final int TASK_TYPE_START_INDEX = 1;
    private static final int TASK_TYPE_END_INDEX = 2;
    private static final int TASK_STATUS_INDEX = 4;
    private static final int TASK_DETAILS_START_INDEX = 7;
    private static final int CLOSING_PARENTHESIS_LENGTH = 1;

    private static final char DONE_STATUS = 'X';
    private static final String TODO_TASK_TYPE = "T";
    private static final String DEADLINE_TASK_TYPE = "D";
    private static final String EVENT_TASK_TYPE = "E";
    private static final String DEADLINE_TIME_PREFIX = " (by: ";
    private static final String EVENT_START_TIME_PREFIX = " (from: ";
    private static final String EVENT_END_TIME_PREFIX = " to: ";

    private TaskStorage() {
    }

    /**
     * Reads all tasks from the data file, creating an empty file if necessary.
     *
     * @param filePath path to the task data file.
     * @return tasks represented by the saved records
     * @throws NicoException if the task file cannot be read or contains an invalid record
     */
    public static List<Task> readTasks(Path filePath) throws NicoException {
        try {
            createTasksFile(filePath);
            return parseTaskRecords(Files.readAllLines(filePath));
        } catch (IOException exception) {
            throw new NicoException("Aiyoh, cannot load tasks.txt.", exception);
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException exception) {
            throw new NicoException("Aiyoh, got a task in tasks.txt I cannot understand.", exception);
        }
    }

    private static List<Task> parseTaskRecords(List<String> taskRecords) {
        List<Task> tasks = new ArrayList<>();
        for (String taskRecord : taskRecords) {
            if (!taskRecord.isBlank()) {
                tasks.add(parseTaskRecord(taskRecord));
            }
        }
        return tasks;
    }

    private static Task parseTaskRecord(String taskRecord) {
        // Priority metadata precedes the description, so old descriptions remain unambiguous.
        Priority priority = null;
        int priorityStartIndex = TASK_DETAILS_START_INDEX - 1;
        if (taskRecord.charAt(priorityStartIndex) == '[') {
            int priorityEndIndex = taskRecord.indexOf(']', priorityStartIndex);
            String priorityText = taskRecord.substring(priorityStartIndex + 1, priorityEndIndex);
            try {
                priority = Priority.parse(priorityText);
            } catch (NicoException exception) {
                throw new IllegalArgumentException("Invalid saved priority", exception);
            }
            taskRecord = taskRecord.substring(0, priorityStartIndex)
                    + taskRecord.substring(priorityEndIndex + 1);
        }
        String taskType = taskRecord.substring(TASK_TYPE_START_INDEX, TASK_TYPE_END_INDEX);
        boolean isDone = taskRecord.charAt(TASK_STATUS_INDEX) == DONE_STATUS;
        String taskDetails = taskRecord.substring(TASK_DETAILS_START_INDEX);
        Task task = createTask(taskType, taskDetails);
        task.setPriority(priority);

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    private static Task createTask(String taskType, String taskDetails) {
        switch (taskType) {
            case TODO_TASK_TYPE:
                return new Todo(taskDetails);
            case DEADLINE_TASK_TYPE:
                return createDeadline(taskDetails);
            case EVENT_TASK_TYPE:
                return createEvent(taskDetails);
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    private static Deadline createDeadline(String taskDetails) {
        int dueTimeStartIndex = taskDetails.lastIndexOf(DEADLINE_TIME_PREFIX);
        String description = taskDetails.substring(0, dueTimeStartIndex);
        String dueTimeText = taskDetails.substring(
                dueTimeStartIndex + DEADLINE_TIME_PREFIX.length(),
                taskDetails.length() - CLOSING_PARENTHESIS_LENGTH);
        return new Deadline(description, parseSavedDateTime(dueTimeText));
    }

    private static Event createEvent(String taskDetails) {
        int startTimeStartIndex = taskDetails.lastIndexOf(EVENT_START_TIME_PREFIX);
        int endTimeStartIndex = taskDetails.lastIndexOf(EVENT_END_TIME_PREFIX);
        String description = taskDetails.substring(0, startTimeStartIndex);
        String startTimeText = taskDetails.substring(
                startTimeStartIndex + EVENT_START_TIME_PREFIX.length(), endTimeStartIndex);
        String endTimeText = taskDetails.substring(
                endTimeStartIndex + EVENT_END_TIME_PREFIX.length(),
                taskDetails.length() - CLOSING_PARENTHESIS_LENGTH);
        return new Event(description, parseSavedDateTime(startTimeText), parseSavedDateTime(endTimeText));
    }

    private static LocalDateTime parseSavedDateTime(String dateTimeText) {
        try {
            return LocalDateTime.parse(dateTimeText, Task.DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Invalid saved task date and time", exception);
        }
    }

    /**
     * Appends one newly created task to the data file.
     *
     * @param filePath Path to the task data file.
     * @param task Newly created task to save.
     * @throws NicoException If the task cannot be saved.
     */
    public static void writeTask(Path filePath, Task task) throws NicoException {
        try {
            createTasksFile(filePath);
            assert Files.exists(filePath) : "the task file must exist before a task is written";
            Files.writeString(filePath, task + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new NicoException("Aiyoh, cannot save this task to tasks.txt.", exception);
        }
    }

    /**
     * Rewrites the data file so it matches the current task list.
     *
     * @param filePath Path to the task data file.
     * @param tasks Tasks to save.
     * @throws NicoException If the tasks cannot be saved.
     */
    public static void writeAllTasks(Path filePath, List<Task> tasks) throws NicoException {
        try {
            createTasksFile(filePath);
            assert Files.exists(filePath) : "the task file must exist before the task list is written";
            StringBuilder savedTasks = new StringBuilder();
            for (Task task : tasks) {
                savedTasks.append(task).append(System.lineSeparator());
            }
            Files.writeString(filePath, savedTasks.toString(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            throw new NicoException("Aiyoh, cannot update tasks.txt.", exception);
        }
    }

    /**
     * Creates the data folder and task file when they do not yet exist.
     *
     * @param filePath Path to the task data file.
     * @throws IOException If the folder or file cannot be created.
     */
    public static void createTasksFile(Path filePath) throws IOException {
        Path parentDirectory = filePath.getParent();
        if (parentDirectory != null) {
            Files.createDirectories(parentDirectory);
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }
}
