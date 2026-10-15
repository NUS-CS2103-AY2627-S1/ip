package dukey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Scanner;

import dukey.exception.DukeyException;
import dukey.task.Deadline;
import dukey.task.Event;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.task.Todo;

/**
 * Handles loading tasks from the data file and saving tasks to the data file.
 */
public class Storage {
    private static final int TASK_TYPE_INDEX = 0;
    private static final int DONE_STATUS_INDEX = 1;
    private static final int DESCRIPTION_INDEX = 2;
    private static final int DEADLINE_BY_INDEX = 3;
    private static final int EVENT_FROM_INDEX = 3;
    private static final int EVENT_TO_INDEX = 4;
    private static final int TODO_DONE_AT_INDEX = 3;
    private static final int DEADLINE_DONE_AT_INDEX = 4;
    private static final int EVENT_DONE_AT_INDEX = 5;
    private static final int MINIMUM_TASK_FIELD_COUNT = 3;
    private static final int MINIMUM_DEADLINE_FIELD_COUNT = 4;
    private static final int MINIMUM_EVENT_FIELD_COUNT = 5;

    private static final String TODO_TASK_TYPE = "T";
    private static final String DEADLINE_TASK_TYPE = "D";
    private static final String EVENT_TASK_TYPE = "E";
    private static final String DONE_STATUS = "1";
    private static final String NOT_DONE_STATUS = "0";
    private static final String DATE_TIME_FORMAT_MESSAGE = "Saved date/time must use format: yyyy-MM-dd HHmm";

    private final String filePath;

    /**
     * Creates a storage handler for the given data file path.
     *
     * @param filePath Path to the task data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads saved tasks from the data file.
     *
     * @return Task list loaded from disk.
     * @throws FileNotFoundException If the data file does not exist.
     * @throws DukeyException If the data file contains invalid task data.
     */
    public TaskList load() throws FileNotFoundException, DukeyException {
        TaskList tasks = new TaskList();
        File file = new File(this.filePath);

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                tasks.add(parseTask(scanner.nextLine()));
            }
        }

        return tasks;
    }

    /**
     * Saves all tasks to the data file.
     *
     * @param tasks Tasks to save.
     * @throws DukeyException If the data directory or file cannot be written.
     */
    public void save(TaskList tasks) throws DukeyException {
        File file = new File(this.filePath);
        File parentDirectory = file.getParentFile();

        if (parentDirectory != null && !parentDirectory.exists()) {
            boolean isParentDirCreated = parentDirectory.mkdirs();

            if (!isParentDirCreated) {
                throw new DukeyException("Could not create data directory");
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (int i = 1; i <= tasks.size(); i++) {
                Task task = tasks.get(i);
                String fileLine = task.toFileString();

                if (fileLine.isEmpty()) {
                    throw new DukeyException("Could not save an unknown task type.");
                }
                fileWriter.write(fileLine);
                fileWriter.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new DukeyException("Could not save tasks to file.");
        }
    }

    private Task parseTask(String fileLine) throws DukeyException {
        try {
            String[] parts = fileLine.split("\\s*\\|\\s*", -1);

            if (parts.length < MINIMUM_TASK_FIELD_COUNT) {
                throw new DukeyException("Saved task is missing fields.");
            }

            if (!Arrays.asList(TODO_TASK_TYPE, DEADLINE_TASK_TYPE, EVENT_TASK_TYPE).contains(parts[TASK_TYPE_INDEX])) {
                throw new DukeyException("Undefined task type.");
            }

            if (parts[DONE_STATUS_INDEX].isEmpty()) {
                throw new DukeyException("Done status is missing for this task.");
            }

            if (!Arrays.asList(NOT_DONE_STATUS, DONE_STATUS).contains(parts[DONE_STATUS_INDEX])) {
                throw new DukeyException("Undefined done status.");
            }

            if (parts[DESCRIPTION_INDEX].isEmpty()) {
                throw new DukeyException("Description is missing for this task.");
            }

            Task task = createTask(parts);
            markLoadedTaskIfDone(task, parts);
            return task;
        } catch (DateTimeParseException e) {
            throw new DukeyException(DATE_TIME_FORMAT_MESSAGE);
        }
    }

    private Task createTask(String[] parts) throws DukeyException {
        String taskType = parts[TASK_TYPE_INDEX];

        if (taskType.equals(TODO_TASK_TYPE)) {
            return new Todo(parts[DESCRIPTION_INDEX]);
        } else if (taskType.equals(DEADLINE_TASK_TYPE)) {
            if (parts.length < MINIMUM_DEADLINE_FIELD_COUNT || parts[DEADLINE_BY_INDEX].isEmpty()) {
                throw new DukeyException("Deadline date/time is missing for this task.");
            }
            return new Deadline(parts[DESCRIPTION_INDEX], parts[DEADLINE_BY_INDEX]);
        } else if (taskType.equals(EVENT_TASK_TYPE)) {
            assert taskType.equals(EVENT_TASK_TYPE) : "Only event tasks should reach this branch.";
            if (parts.length < MINIMUM_EVENT_FIELD_COUNT
                    || parts[EVENT_FROM_INDEX].isEmpty()
                    || parts[EVENT_TO_INDEX].isEmpty()) {
                throw new DukeyException("Event date/time is missing for this task.");
            }
            return new Event(parts[DESCRIPTION_INDEX], parts[EVENT_FROM_INDEX], parts[EVENT_TO_INDEX]);
        }

        throw new DukeyException("Undefined task type.");
    }

    private void markLoadedTaskIfDone(Task task, String[] parts) throws DukeyException {
        int doneAtIndex = getDoneAtIndex(parts[TASK_TYPE_INDEX]);
        String doneAtText = getOptionalField(parts, doneAtIndex);

        if (parts[DONE_STATUS_INDEX].equals(NOT_DONE_STATUS)) {
            if (!doneAtText.isEmpty()) {
                throw new DukeyException("Undone saved tasks should not have a completion date/time.");
            }
            return;
        }

        if (doneAtText.isEmpty()) {
            task.markAsDone();
            return;
        }

        task.markAsDone(LocalDateTime.parse(doneAtText, Task.STORAGE_DATE_TIME_FORMAT));
    }

    private int getDoneAtIndex(String taskType) throws DukeyException {
        if (taskType.equals(TODO_TASK_TYPE)) {
            return TODO_DONE_AT_INDEX;
        } else if (taskType.equals(DEADLINE_TASK_TYPE)) {
            return DEADLINE_DONE_AT_INDEX;
        } else if (taskType.equals(EVENT_TASK_TYPE)) {
            return EVENT_DONE_AT_INDEX;
        }

        throw new DukeyException("Undefined task type.");
    }

    private String getOptionalField(String[] parts, int index) {
        if (parts.length <= index) {
            return "";
        }

        return parts[index];
    }
}
