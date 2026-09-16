package rambo.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import rambo.exception.RamboException;
import rambo.task.DeadlineTask;
import rambo.task.EventTask;
import rambo.task.Task;

/**
 * Loads tasks from a file and saves tasks to that file.
 */
public class Storage {
    private static final String DONE_MARKER = "X";
    private static final String FIELD_SEPARATOR_REGEX = "\\|";
    private static final int TASK_TYPE_INDEX = 0;
    private static final int DONE_MARKER_INDEX = 1;
    private static final int TASK_NAME_INDEX = 2;
    private static final int FIRST_DATE_INDEX = 3;
    private static final int SECOND_DATE_INDEX = 4;
    private static final int PRIORITY_FIELD_OFFSET = 1;
    private static final int TODO_FIELD_COUNT = 3;
    private static final int DEADLINE_FIELD_COUNT = 4;
    private static final int EVENT_FIELD_COUNT = 5;

    private final Path dataFile;

    /**
     * Creates a storage manager for the application's default task data file.
     */
    public Storage() {
        this.dataFile = Paths.get("./data/Rambo.txt");
    }

    /**
     * Loads saved tasks and creates the corresponding task objects.
     *
     * @return tasks stored in the data file
     * @throws RamboException if the file cannot be read or contains an invalid task record
     */
    public List<Task> loadTasks() throws RamboException {
        if (!Files.exists(dataFile)) {
            return new ArrayList<>();
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(dataFile);
        } catch (IOException e) {
            throw new RamboException("Could not read your saved tasks!", e);
        }

        return lines.stream()
                .filter(line -> !line.isBlank())
                .map(this::createTask)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Replaces the data file contents with the supplied tasks.
     *
     * @param tasks tasks to save
     * @throws RamboException if the data file cannot be written
     */
    public void saveTasks(List<Task> tasks) throws RamboException {
        assert tasks != null : "The task list supplied by the application should not be null";
        assert tasks.stream().allMatch(task -> task != null)
                : "The task list supplied by the application should not contain null tasks";
        List<String> records = tasks.stream()
                .map(Task::toDataString)
                .toList();

        try {
            Path parentDirectory = dataFile.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(dataFile, records, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RamboException("Could not update your saved tasks!", e);
        }
    }

    /**
     * Converts one data-file record into a task.
     */
    private Task createTask(String line) throws RamboException {
        String[] fields = parseFields(line);
        if (fields.length < TODO_FIELD_COUNT) {
            throw createInvalidRecordException(line);
        }

        Task task;
        switch (fields[TASK_TYPE_INDEX]) {
            case "T":
                task = createTodoTask(fields, line);
                break;
            case "D":
                task = createDeadlineTask(fields, line);
                break;
            case "E":
                task = createEventTask(fields, line);
                break;
            default:
                throw createInvalidRecordException(line);
        }
        assert task != null : "A recognised record type should create a task";

        restoreDoneStatus(task, fields[DONE_MARKER_INDEX], line);
        restorePriority(task, fields, line);
        return task;
    }

    private String[] parseFields(String line) {
        return Arrays.stream(line.split(FIELD_SEPARATOR_REGEX))
                .map(String::trim)
                .toArray(String[]::new);
    }

    private void restoreDoneStatus(Task task, String doneMarker, String line) {
        if (!doneMarker.isEmpty() && !doneMarker.equals(DONE_MARKER)) {
            throw createInvalidRecordException(line);
        }

        if (doneMarker.equals(DONE_MARKER)) {
            task.toggleDone();
            assert task.isDone() : "A stored done marker should produce a completed task";
        }
    }

    private Task createTodoTask(String[] fields, String line) {
        validateFieldCount(fields, TODO_FIELD_COUNT, line);
        String taskName = fields[TASK_NAME_INDEX];
        return new Task(taskName);
    }

    private Task createDeadlineTask(String[] fields, String line) {
        validateFieldCount(fields, DEADLINE_FIELD_COUNT, line);
        String taskName = fields[TASK_NAME_INDEX];
        String deadline = fields[FIRST_DATE_INDEX];
        return new DeadlineTask(taskName, deadline);
    }

    private Task createEventTask(String[] fields, String line) {
        validateFieldCount(fields, EVENT_FIELD_COUNT, line);
        String taskName = fields[TASK_NAME_INDEX];
        String fromDate = fields[FIRST_DATE_INDEX];
        String toDate = fields[SECOND_DATE_INDEX];
        return new EventTask(taskName, fromDate, toDate);
    }

    private void validateFieldCount(String[] fields, int expectedFieldCount, String line) {
        if (fields.length != expectedFieldCount && fields.length != expectedFieldCount + PRIORITY_FIELD_OFFSET) {
            throw createInvalidRecordException(line);
        }
    }

    /**
     * Restores a priority from a new-format record while treating legacy records as unprioritized.
     */
    private void restorePriority(Task task, String[] fields, String line) {
        int legacyFieldCount = getLegacyFieldCount(fields[TASK_TYPE_INDEX]);
        if (fields.length == legacyFieldCount) {
            return;
        }

        int priorityLevel;
        try {
            priorityLevel = Integer.parseInt(fields[fields.length - 1]);
        } catch (NumberFormatException e) {
            throw createInvalidRecordException(line);
        }

        if (priorityLevel == Task.NO_PRIORITY) {
            return;
        }

        try {
            task.setPriorityLevel(priorityLevel);
        } catch (RamboException e) {
            throw createInvalidRecordException(line);
        }
    }

    /**
     * Returns the number of fields used by the legacy format for a recognised task type.
     */
    private int getLegacyFieldCount(String taskType) {
        switch (taskType) {
            case "T":
                return TODO_FIELD_COUNT;
            case "D":
                return DEADLINE_FIELD_COUNT;
            case "E":
                return EVENT_FIELD_COUNT;
            default:
                throw new AssertionError("Only recognised task types should reach priority restoration");
        }
    }

    private RamboException createInvalidRecordException(String line) {
        return new RamboException("Invalid task record: " + line);
    }
}
