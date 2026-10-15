package zinc.task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves tasks to, and restores tasks from, Zinc's local data file.
 */
public class TaskStorage {
    /** The separator between fields in the task-storage format. */
    private static final String FIELD_SEPARATOR = " | ";

    /** The regular expression used to split stored task fields. */
    private static final String FIELD_SEPARATOR_REGEX = " \\| ";

    /** The field index containing the task type. */
    private static final int TYPE_INDEX = 0;

    /** The field index containing the completion status. */
    private static final int STATUS_INDEX = 1;

    /** The field index containing the task description. */
    private static final int DESCRIPTION_INDEX = 2;

    /** The field index containing a deadline's due date and time. */
    private static final int DEADLINE_INDEX = 3;

    /** The field index containing an event's start date and time. */
    private static final int EVENT_START_INDEX = 3;

    /** The field index containing an event's end date and time. */
    private static final int EVENT_END_INDEX = 4;

    /** The marker representing a completed task. */
    private static final String COMPLETED_STATUS = "1";

    /** The marker representing an incomplete task. */
    private static final String INCOMPLETE_STATUS = "0";

    /** The file used to retain tasks between application runs. */
    private static final Path STORAGE_FILE = Path.of("data", "zincTasks.txt");

    /** Whether the most recent load found malformed task data. */
    private boolean wasDataCorrupted;

    /**
     * Returns the tasks currently saved in the storage file.
     * If any line has an invalid layout, the storage file is cleared and no
     * tasks are loaded. This avoids restoring only part of a damaged list.
     * When an I/O error occurs, the storage file is also reset.
     *
     * @return All valid saved tasks, in their stored order.
     */
    public List<Task> loadTasks() {
        wasDataCorrupted = false;
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(STORAGE_FILE)) {
            return tasks;
        }

        try {
            Files.readAllLines(STORAGE_FILE).stream()
                    .map(this::parseTask)
                    .forEach(tasks::add);
        } catch (IllegalArgumentException exception) {
            wasDataCorrupted = true;
            System.out.println("Saved task file has an invalid layout. Clearing saved tasks.\n");
            resetStorage();
            tasks.clear();
        } catch (IOException exception) {
            System.out.println("Unable to load saved tasks. Starting with an empty list.\n");
            resetStorage();
        }
        return tasks;
    }

    /** Returns whether the most recent load found malformed task data. */
    boolean wasDataCorrupted() {
        return wasDataCorrupted;
    }

    /**
     * Writes every task in the supplied list to the storage file.
     *
     * @param tasks The task list to save.
     */
    public void saveTasks(List<Task> tasks) {
        assert tasks != null : "Tasks to save must not be null";
        List<String> lines = tasks.stream()
                .map(this::formatTask)
                .toList();

        try {
            Files.createDirectories(STORAGE_FILE.getParent());
            Files.write(STORAGE_FILE, lines);
        } catch (IOException exception) {
            System.out.println("Unable to save tasks. Error: " + exception + "\n");
        }
    }

    /**
     * Converts one correctly formatted storage line into its corresponding task.
     *
     * @param line The serialized task line read from the storage file.
     * @return The task represented by {@code line}.
     * @throws IllegalArgumentException If the line does not match the storage layout.
     */
    private Task parseTask(String line) {
        String[] storedFields = line.split(FIELD_SEPARATOR_REGEX, -1);
        validateCommonFields(storedFields);

        TaskType taskType = TaskType.fromStorageIdentifier(storedFields[TYPE_INDEX]);
        Task task = createTask(taskType, storedFields);
        if (storedFields[STATUS_INDEX].equals(COMPLETED_STATUS)) {
            task.markAsCompleted();
        }
        return task;
    }

    /** Validates fields shared by every stored task type. */
    private void validateCommonFields(String[] storedFields) {
        if (storedFields.length <= DESCRIPTION_INDEX) {
            throw new IllegalArgumentException("Stored task is missing required fields");
        }
        String completionStatus = storedFields[STATUS_INDEX];
        if (!completionStatus.equals(COMPLETED_STATUS) && !completionStatus.equals(INCOMPLETE_STATUS)) {
            throw new IllegalArgumentException("Invalid task completion status");
        }
        for (int i = DESCRIPTION_INDEX; i < storedFields.length; i++) {
            if (storedFields[i].isBlank()) {
                throw new IllegalArgumentException("Task information cannot be blank");
            }
        }
    }

    /** Creates the task represented by validated storage fields. */
    private Task createTask(TaskType taskType, String[] storedFields) {
        switch (taskType) {
            case TODO:
                requireFieldCount(storedFields, 3);
                return new Todo(storedFields[DESCRIPTION_INDEX]);
            case DEADLINE:
                requireFieldCount(storedFields, 4);
                return new Deadline(storedFields[DESCRIPTION_INDEX],
                        LocalDateTime.parse(storedFields[DEADLINE_INDEX]));
            case EVENT:
                requireFieldCount(storedFields, 5);
                return new Event(storedFields[DESCRIPTION_INDEX],
                        LocalDateTime.parse(storedFields[EVENT_START_INDEX]),
                        LocalDateTime.parse(storedFields[EVENT_END_INDEX]));
            default:
                throw new IllegalArgumentException("Unsupported task type");
        }
    }

    /** Ensures that a saved task has exactly the expected number of fields. */
    private void requireFieldCount(String[] storedFields, int expectedCount) {
        if (storedFields.length != expectedCount) {
            throw new IllegalArgumentException("Incorrect number of task fields");
        }
    }

    /** Clears a malformed storage file so Zinc can start with an empty list. */
    private void resetStorage() {
        try {
            Files.write(STORAGE_FILE, List.of());
        } catch (IOException exception) {
            System.out.println("Unable to clear the invalid saved task file.\n");
        }
    }

    /** Converts a task into the pre-determined storage format. */
    private String formatTask(Task task) {
        String status = task.isCompleted() ? COMPLETED_STATUS : INCOMPLETE_STATUS;
        String commonFields = task.getTaskType().getStorageIdentifier() + FIELD_SEPARATOR
                + status + FIELD_SEPARATOR + task.getDescription();
        switch (task.getTaskType()) {
            case TODO:
                return commonFields;
            case DEADLINE:
                Deadline deadline = (Deadline) task;
                return commonFields + FIELD_SEPARATOR + deadline.getDeadline();
            case EVENT:
                Event event = (Event) task;
                return commonFields + FIELD_SEPARATOR + event.getStart() + FIELD_SEPARATOR + event.getEnd();
            default:
                throw new IllegalArgumentException("Unsupported task type");
        }
    }
}
