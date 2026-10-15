package alex.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import alex.exception.AlexException;
import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.task.Todo;
import alex.util.DateParser;

/**
 * Loads and saves Alex's task list using a local data file.
 */
public class Storage {
    private static final String FIELD_SEPARATOR_REGEX = "\\s*\\|\\s*";
    private static final String STATUS_INCOMPLETE = "0";
    private static final String STATUS_COMPLETE = "1";
    private static final String TASK_TYPE_TODO = "T";
    private static final String TASK_TYPE_DEADLINE = "D";
    private static final String TASK_TYPE_EVENT = "E";

    private static final int SPLIT_LIMIT_PRESERVE_TRAILING_EMPTY_FIELDS = -1;
    private static final int FIELD_INDEX_TASK_TYPE = 0;
    private static final int FIELD_INDEX_COMPLETION_STATUS = 1;
    private static final int FIELD_INDEX_FIRST_DETAIL = 2;
    private static final int FIELD_INDEX_DEADLINE_DATE = 3;
    private static final int FIELD_INDEX_EVENT_START_DATE = 3;
    private static final int FIELD_INDEX_EVENT_END_DATE = 4;
    private static final int FIELD_COUNT_REQUIRED_HEADER = 2;
    private static final int FIELD_COUNT_TODO = 3;
    private static final int FIELD_COUNT_DEADLINE = 4;
    private static final int FIELD_COUNT_EVENT = 5;

    private final Path filePath;

    /**
     * Creates storage backed by the specified data file.
     *
     * @param filePath path of the task data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the data file. A missing file represents an empty task list.
     *
     * @return tasks reconstructed from the data file.
     * @throws StorageException if an existing file cannot be read or contains invalid data.
     */
    public ArrayList<Task> loadTasks() throws StorageException {
        List<String> lines = readTaskLines();
        ArrayList<Task> tasks = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            Task task = parseTaskLine(line, i + 1);
            tasks.add(task);
        }

        return tasks;
    }

    /**
     * Reads serialized task lines, treating a missing data file as an empty task list.
     *
     * @return lines read from the data file.
     * @throws StorageException if an existing file cannot be read.
     */
    private List<String> readTaskLines() throws StorageException {
        try {
            if (Files.notExists(filePath)) {
                return List.of();
            }
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("I couldn't read the data file at " + filePath + ".", e);
        }
    }

    /**
     * Reconstructs a task and adds its line number to invalid-data errors.
     *
     * @param line serialized task line.
     * @param lineNumber one-based data-file line number.
     * @return task reconstructed from the line.
     * @throws StorageException if the line contains invalid task data.
     */
    private Task parseTaskLine(String line, int lineNumber) throws StorageException {
        try {
            String[] fields = line.split(
                    FIELD_SEPARATOR_REGEX, SPLIT_LIMIT_PRESERVE_TRAILING_EMPTY_FIELDS);
            Task task = createTask(fields);

            if (fields[FIELD_INDEX_COMPLETION_STATUS].equals(STATUS_COMPLETE)) {
                task.markAsDone();
            }
            return task;
        } catch (AlexException | IllegalArgumentException e) {
            throw new StorageException("The data file is invalid at line " + lineNumber
                    + ": " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs the correct task subtype from one line's fields.
     *
     * @param fields fields read from one line in the data file.
     * @return the reconstructed task.
     */
    private Task createTask(String[] fields) throws AlexException {
        if (fields.length < FIELD_COUNT_REQUIRED_HEADER) {
            throw new IllegalArgumentException("missing task type or completion status");
        }

        String taskType = fields[FIELD_INDEX_TASK_TYPE];
        String status = fields[FIELD_INDEX_COMPLETION_STATUS];

        if (!status.equals(STATUS_INCOMPLETE) && !status.equals(STATUS_COMPLETE)) {
            throw new IllegalArgumentException("completion status must be 0 or 1");
        }

        switch (taskType) {
            case TASK_TYPE_TODO:
                validateFields(fields, FIELD_COUNT_TODO);
                return new Todo(fields[FIELD_INDEX_FIRST_DETAIL]);
            case TASK_TYPE_DEADLINE:
                validateFields(fields, FIELD_COUNT_DEADLINE);
                return new Deadline(fields[FIELD_INDEX_FIRST_DETAIL],
                        DateParser.parseTaskDateTime(fields[FIELD_INDEX_DEADLINE_DATE]));
            case TASK_TYPE_EVENT:
                validateFields(fields, FIELD_COUNT_EVENT);
                return new Event(fields[FIELD_INDEX_FIRST_DETAIL],
                        DateParser.parseTaskDateTime(fields[FIELD_INDEX_EVENT_START_DATE]),
                        DateParser.parseTaskDateTime(fields[FIELD_INDEX_EVENT_END_DATE]));
            default:
                throw new IllegalArgumentException("unknown task type '" + taskType + "'");
        }
    }

    private void validateFields(String[] fields, int expectedFieldCount) {
        if (fields.length != expectedFieldCount) {
            throw new IllegalArgumentException(
                    "expected " + expectedFieldCount + " fields, but found " + fields.length);
        }

        for (int i = FIELD_INDEX_FIRST_DETAIL; i < fields.length; i++) {
            if (fields[i].isBlank()) {
                throw new IllegalArgumentException("task details cannot be empty");
            }
        }
    }

    /**
     * Replaces the data file with the current task list, creating its directory if necessary.
     *
     * @param tasks current tasks to save.
     * @throws StorageException if the directory or file cannot be written.
     */
    public void saveTasks(List<Task> tasks) throws StorageException {
        try {
            Files.createDirectories(filePath.getParent());
            List<String> lines = tasks.stream()
                    .map(Task::toDataString)
                    .toList();
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("I couldn't save the task list to " + filePath + ".", e);
        }
    }
}
