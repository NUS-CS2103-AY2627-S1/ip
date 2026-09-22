package longfrog.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Stores the tasks in their display order.
 *
 * <p>An {@link ArrayList} is used so later delete operations can remove a task and shift following
 * tasks without manually managing array capacity or indices.</p>
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list with the specified list of tasks
     */
    public TaskList(List<Task> loadedTasks) {
        assert loadedTasks == null || loadedTasks.stream().noneMatch(Objects::isNull)
                : "Loaded task list must not contain null entries";
        tasks = loadedTasks == null ? new ArrayList<>() : new ArrayList<>(loadedTasks);
    }

    /**
     * Returns the tasks in their display order.
     *
     * @return an unmodifiable snapshot of the tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of tasks
     */
    public int getCount() {
        return tasks.size();
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        assert task != null : "Task list must not contain null entries";
        tasks.add(task);
    }

    /**
     * Finds the first task with the same duplicate identity as a proposed task.
     *
     * @param proposedTask the task being considered for addition
     * @return the zero-based index of the first duplicate, or {@code -1} when none exists
     */
    public int findDuplicateIndex(Task proposedTask) {
        assert proposedTask != null : "Proposed task must not be null";

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).isDuplicateOf(proposedTask)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Counts task occurrences beyond the first occurrence of each duplicate identity.
     *
     * @return the number of duplicate entries in the list
     */
    public int countDuplicateEntries() {
        int duplicateCount = 0;
        for (int currentIndex = 0; currentIndex < tasks.size(); currentIndex++) {
            Task currentTask = tasks.get(currentIndex);
            for (int earlierIndex = 0; earlierIndex < currentIndex; earlierIndex++) {
                if (tasks.get(earlierIndex).isDuplicateOf(currentTask)) {
                    duplicateCount++;
                    break;
                }
            }
        }

        return duplicateCount;
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task, or {@code null} when the index is invalid
     */
    public Task removeTask(int index) {
        if (!taskExists(index)) {
            return null;
        }

        return tasks.remove(index);
    }

    /**
     * Checks whether a zero-based index identifies a stored task.
     *
     * @param index the zero-based task index
     * @return whether the index is within the list bounds
     */
    public boolean taskExists(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the task at a zero-based index, if present.
     *
     * @param index the zero-based task index
     * @return the task, or {@code null} when the index is invalid
     */
    public Task getTask(int index) {
        if (!taskExists(index)) {
            return null;
        }

        Task task = tasks.get(index);
        assert task != null : "Task list must not contain null entries";
        return task;
    }
}
