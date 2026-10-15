package bingusdingus.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Stores and manages the tasks created by Bingus Dingus. */
public class TaskList {
    private final ArrayList<Task> tasks;
    private final TaskStorage storage;
    private boolean hasStorageLoadError;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
        storage = new TaskStorage();
        try {
            tasks.addAll(storage.load());
        } catch (IllegalStateException e) {
            hasStorageLoadError = true;
        }
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        validateNewTask(task, "Cannot add a null task");
        int previousSize = tasks.size();
        tasks.add(task);
        // Adding must append exactly one task and preserve the caller's task object.
        assert tasks.size() == previousSize + 1 && tasks.get(previousSize) == task;
        saveWithRollback(() -> tasks.remove(tasks.size() - 1));
    }

    /** Returns the task at the specified zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at the specified zero-based index. */
    public Task remove(int index) {
        int previousSize = tasks.size();
        Task removedTask = tasks.remove(index);
        // A successful removal changes the list size by exactly one.
        assert tasks.size() == previousSize - 1;
        saveWithRollback(() -> tasks.add(index, removedTask));
        return removedTask;
    }

    /** Inserts a task at the specified zero-based index and saves the list. */
    public void insert(int index, Task task) {
        validateNewTask(task, "Cannot insert a null task");

        tasks.add(index, task);
        saveWithRollback(() -> tasks.remove(index));
    }

    /** Marks the task at the specified zero-based index as done and saves the list. */
    public void markAsDone(int index) {
        updateCompletionState(index, true);
    }

    /** Marks the task at the specified zero-based index as not done and saves the list. */
    public void markAsNotDone(int index) {
        updateCompletionState(index, false);
    }

    /** Sets the completion state of the task at the specified zero-based index and saves the list. */
    public void setDone(int index, boolean isDone) {
        updateCompletionState(index, isDone);
    }

    /** Updates a task's completion state and restores it if saving fails. */
    private void updateCompletionState(int index, boolean isDone) {
        Task task = tasks.get(index);
        boolean wasDone = task.isDone();
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        assert task.isDone() == isDone;

        try {
            save();
        } catch (IllegalStateException e) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw e;
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the task storage could not be read during initialization. */
    public boolean hasStorageLoadError() {
        return hasStorageLoadError;
    }

    /** Returns the zero-based indexes of tasks whose descriptions contain the keyword. */
    public List<Integer> findIndexes(String keyword) {
        // Search results must refer only to valid positions in the current list.
        assert keyword != null;
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matchingIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .boxed()
                .toList();
        assert matchingIndexes.stream().allMatch(index -> index >= 0 && index < tasks.size());
        return matchingIndexes;
    }

    /**
     * Writes the current task list to disk, replacing the previous contents.
     * The parent directory is created on the first task-list change.
     */
    private void save() {
        storage.save(tasks);
    }

    /** Saves the current list and applies the supplied rollback if saving fails. */
    private void saveWithRollback(Runnable rollback) {
        try {
            save();
        } catch (IllegalStateException e) {
            rollback.run();
            throw e;
        }
    }

    /** Validates that a task can be added without duplicating an existing task. */
    private void validateNewTask(Task task, String nullTaskMessage) {
        if (task == null) {
            throw new IllegalArgumentException(nullTaskMessage);
        }
        if (tasks.stream().anyMatch(existingTask -> haveSameDetails(existingTask, task))) {
            throw new IllegalArgumentException("A task with the same details already exists");
        }
    }

    /** Returns whether two tasks have the same type and user-visible details. */
    private boolean haveSameDetails(Task firstTask, Task secondTask) {
        return firstTask.hasSameDetailsAs(secondTask);
    }
}
