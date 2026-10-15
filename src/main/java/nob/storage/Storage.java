package nob.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nob.exception.NobException;
import nob.task.Deadline;
import nob.task.Event;
import nob.task.Task;
import nob.task.Todo;

/**
 * Loads tasks from, and saves tasks to, Nob's data file.
 */
public class Storage {
    private static final String TODO_PREFIX = "[T] ";
    private static final String DEADLINE_PREFIX = "[D: ";
    private static final String EVENT_PREFIX = "[E: ";
    private static final String DETAILS_END = "] ";
    private static final String EVENT_TIME_SEPARATOR = " to ";
    private static final String UNDONE_STATUS_SUFFIX = " [ ]";
    private static final String DONE_STATUS_SUFFIX = " [✓]";

    /** The file used to persist the current task list. */
    private final Path filePath;

    /**
     * Creates storage that reads from and writes to the specified file.
     *
     * @param filePath The task data file.
     */
    public Storage(Path filePath) {
        assert filePath != null : "Storage requires a data-file path";
        assert filePath.getParent() != null : "The data-file path must include a parent directory";
        this.filePath = filePath;
    }

    /**
     * Loads valid task records into the supplied task array.
     *
     * @param tasks The array that receives the loaded tasks.
     * @return The number of tasks loaded.
     * @throws NobException If the task file cannot be read.
     */
    public int loadTasks(Task[] tasks) throws NobException {
        assert tasks != null : "Loading requires a destination task array";
        if (!Files.exists(filePath)) {
            return 0;
        }

        int taskCount = 0;
        try {
            List<String> savedTasks = Files.readAllLines(filePath);
            for (String savedTask : savedTasks) {
                if (taskCount == tasks.length) {
                    break;
                }

                Task task = parseTask(savedTask);
                if (task != null) {
                    tasks[taskCount] = task;
                    taskCount++;
                }
            }
        } catch (IOException exception) {
            throw new NobException("I couldn't load the saved tasks, so I'm starting with an empty list.");
        }
        return taskCount;
    }

    /**
     * Saves all valid tasks in the supplied array to the task data file.
     *
     * @param tasks The array containing tasks to save.
     * @param taskCount The number of tasks to save from the array.
     * @return {@code true} if the task file was written successfully.
     */
    public boolean saveTasks(Task[] tasks, int taskCount) {
        assert tasks != null : "Saving requires a source task array";
        assert taskCount >= 0 && taskCount <= tasks.length
                : "The saved task count must fit within the source array";
        assert Arrays.stream(tasks, 0, taskCount).allMatch(task -> task != null)
                : "Every saved task slot below the task count must be occupied";

        String fileContents = Arrays.stream(tasks, 0, taskCount)
                .map(task -> task + System.lineSeparator())
                .collect(Collectors.joining());

        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, fileContents);
        } catch (IOException exception) {
            return false;
        }
        return true;
    }

    /** Parses one task line written by {@link #saveTasks(Task[], int)}. */
    private Task parseTask(String savedTask) {
        assert savedTask != null : "Files.readAllLines must not produce null lines";
        if (!hasValidStatusSuffix(savedTask)) {
            return null;
        }

        String taskText = removeStatusSuffix(savedTask);
        Task task = parseTaskDetails(taskText);
        if (task == null) {
            return null;
        }

        if (savedTask.endsWith(DONE_STATUS_SUFFIX)) {
            task.markAsDone();
        }
        return task;
    }

    /** Returns whether a saved task ends with a supported completion status. */
    private boolean hasValidStatusSuffix(String savedTask) {
        return savedTask.endsWith(UNDONE_STATUS_SUFFIX) || savedTask.endsWith(DONE_STATUS_SUFFIX);
    }

    /** Removes the completion status from a saved task record. */
    private String removeStatusSuffix(String savedTask) {
        return savedTask.substring(0, savedTask.length() - UNDONE_STATUS_SUFFIX.length()).trim();
    }

    /** Creates the task represented by the type-specific portion of a record. */
    private Task parseTaskDetails(String taskText) {
        if (taskText.startsWith(TODO_PREFIX)) {
            return parseTodo(taskText);
        } else if (taskText.startsWith(DEADLINE_PREFIX)) {
            return parseDeadline(taskText);
        } else if (taskText.startsWith(EVENT_PREFIX)) {
            return parseEvent(taskText);
        }
        return null;
    }

    /** Creates a to-do task from a saved record, or returns {@code null} if invalid. */
    private Task parseTodo(String taskText) {
        String description = taskText.substring(TODO_PREFIX.length()).trim();
        return description.isEmpty() ? null : new Todo(description);
    }

    /** Creates a deadline task from a saved record, or returns {@code null} if invalid. */
    private Task parseDeadline(String taskText) {
        int detailsEnd = taskText.lastIndexOf(DETAILS_END);
        if (detailsEnd <= DEADLINE_PREFIX.length()) {
            return null;
        }

        String by = taskText.substring(DEADLINE_PREFIX.length(), detailsEnd).trim();
        String description = taskText.substring(detailsEnd + DETAILS_END.length()).trim();
        return by.isEmpty() || description.isEmpty() ? null : new Deadline(description, by);
    }

    /** Creates an event task from a saved record, or returns {@code null} if invalid. */
    private Task parseEvent(String taskText) {
        int detailsEnd = taskText.lastIndexOf(DETAILS_END);
        int separator = taskText.lastIndexOf(EVENT_TIME_SEPARATOR, detailsEnd);
        if (detailsEnd <= EVENT_PREFIX.length() || separator <= EVENT_PREFIX.length()) {
            return null;
        }

        String from = taskText.substring(EVENT_PREFIX.length(), separator).trim();
        String to = taskText.substring(separator + EVENT_TIME_SEPARATOR.length(), detailsEnd).trim();
        String description = taskText.substring(detailsEnd + DETAILS_END.length()).trim();
        return from.isEmpty() || to.isEmpty() || description.isEmpty()
                ? null : new Event(description, from, to);
    }
}
