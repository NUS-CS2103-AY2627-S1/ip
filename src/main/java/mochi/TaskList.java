package mochi;

import java.util.ArrayList;
import java.util.List;

import mochi.task.Task;

/**
 * Manages the list of tasks and automatically saves changes to disk.
 */
public class TaskList {
    private final List<Task> tasks;
    private final Storage storage;

    /**
     * Creates an empty task list that auto-saves to the given storage.
     *
     * @param storage the storage backend for persistence
     */
    public TaskList(Storage storage) {
        this.tasks = new ArrayList<>();
        this.storage = storage;
    }

    /**
     * Returns the underlying list for display and iteration.
     */
    public List<Task> getTasks() {
        return tasks;
    }

    /**
     * Adds a task to the list and saves.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
        storage.saveTasks(tasks);
    }

    /**
     * Removes the task at the given index and saves.
     *
     * @param index 0-based position of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        Task removed = tasks.remove(index);
        storage.saveTasks(tasks);
        return removed;
    }

    /**
     * Marks the task at the given index as done and saves.
     *
     * @param index 0-based position of the task
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
        storage.saveTasks(tasks);
    }

    /**
     * Marks the task at the given index as not done and saves.
     *
     * @param index 0-based position of the task
     */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
        storage.saveTasks(tasks);
    }
}
