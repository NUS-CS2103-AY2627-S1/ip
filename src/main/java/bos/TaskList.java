package bos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Owns the collection of tasks and provides operations that modify it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * <p>The tasks are copied so callers cannot change this task list by
     * retaining and modifying the original list.</p>
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            add(task);
        }
    }

    /**
     * Adds a task to the end of the list unless a task with the same description already exists.
     *
     * @param task task to add.
     * @return true if the task was added, or false if it was a duplicate.
     */
    public boolean add(Task task) {
        if (tasks.stream().anyMatch(existingTask -> existingTask.hasSameDescription(task))) {
            return false;
        }

        tasks.add(task);
        return true;
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to remove.
     * @return removed task.
     */
    public Task delete(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Marks and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to mark.
     * @return marked task.
     */
    public Task mark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsDone();
        return task;
    }

    /**
     * Unmarks and returns the task at the given zero-based index.
     *
     * @param taskIndex index of the task to unmark.
     * @return unmarked task.
     */
    public Task unmark(int taskIndex) {
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword, ignoring case.
     * The returned list preserves the tasks' original order.
     *
     * @param keyword text to search for in task descriptions.
     * @return matching tasks.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
    }

    /**
     * Provides a read-only view for displaying or saving the tasks.
     *
     * @return unmodifiable view of the tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
