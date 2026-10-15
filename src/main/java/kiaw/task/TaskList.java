package kiaw.task;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Manages the collection of tasks tracked by Kiaw.
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
     * @param tasks initial tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        this.tasks = tasks;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index zero-based task index
     * @return task at the specified index
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index should be valid";
        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at the specified index.
     *
     * @param index zero-based task index
     * @return deleted task
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index should be valid";
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as completed.
     *
     * @param index zero-based task index
     */
    public void mark(int index) {
        assert isValidIndex(index) : "Task index should be valid";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified index as not completed.
     *
     * @param index zero-based task index
     */
    public void unmark(int index) {
        assert isValidIndex(index) : "Task index should be valid";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Sorts tasks chronologically, placing tasks without dates last.
     */
    public void sortByDate() {
        tasks.sort(
                Comparator.comparing(
                        Task::getSortDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
    }

    /**
     * Returns the number of tasks in the task list.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the task list contains no tasks.
     *
     * @return true if the task list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the underlying task collection.
     *
     * This is primarily used when saving the task list to storage.
     *
     * @return underlying task collection
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Checks whether an index refers to an existing task.
     *
     * @param index zero-based task index
     * @return true if the index is valid
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
