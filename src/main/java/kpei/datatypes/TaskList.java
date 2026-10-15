package kpei.datatypes;

import java.util.ArrayList;

import kpei.exceptions.InvalidIndexException;

/**
 * Represents an ordered, 1-based indexed collection of tasks (Task, Deadline, Event).
 * Handles adding, retrieving, removing, marking, and unmarking tasks with range validation.
 */
public class TaskList {
    private final ArrayList<Task> tasks;
    private String description;

    /**
     * Initializes an empty task list.
     */
    public TaskList(String description) {
        this.tasks = new ArrayList<>();
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Validates that the specified 1-based index is within valid list bounds.
     *
     * @param index The 1-based index to validate.
     * @throws InvalidIndexException If the index is less than 1 or greater than the number of tasks.
     */
    private void validateIndex(int index) throws InvalidIndexException {
        if (index < 1 || index > tasks.size()) {
            throw new InvalidIndexException(index, tasks.size());
        }
    }

    /**
     * Retrieves the task at the specified 1-based index.
     *
     * @param index The 1-based index of the task.
     * @return The task at the given index.
     * @throws InvalidIndexException If the index is outside the valid range.
     */
    public Task get(int index) throws InvalidIndexException {
        validateIndex(index);
        return tasks.get(index - 1);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Removes and returns the task at the specified 1-based index.
     *
     * @param index The 1-based index of the task to remove.
     * @return The removed task.
     * @throws InvalidIndexException If the index is outside the valid range.
     */
    public Task remove(int index) throws InvalidIndexException {
        validateIndex(index);
        return tasks.remove(index - 1);
    }

    /**
     * Marks the task at the specified 1-based index as done.
     *
     * @param index The 1-based index of the task.
     * @return The marked task.
     * @throws InvalidIndexException If the index is outside the valid range.
     */
    public Task mark(int index) throws InvalidIndexException {
        Task task = get(index);
        task.mark();
        assert task.isMarked() : "A task should be marked after mark()";
        return task;
    }

    /**
     * Unmarks the task at the specified 1-based index.
     *
     * @param index The 1-based index of the task.
     * @return The unmarked task.
     * @throws InvalidIndexException If the index is outside the valid range.
     */
    public Task unmark(int index) throws InvalidIndexException {
        Task task = get(index);
        task.unmark();
        assert !task.isMarked() : "A task should be unmarked after unmark()";
        return task;
    }

    /**
     * Returns the total number of tasks in the list.
     *
     * @return The number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks if the list contains no tasks.
     *
     * @return {@code true} if the list is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Finds and returns a task list of tasks whose descriptions contain the specified keyword.
     * The search is case-insensitive.
     *
     * @param keyword The search keyword.
     * @return A {@code TaskList} containing all matching tasks.
     */
    public TaskList find(String keyword) {
        TaskList matchingTasks = new TaskList("Matching tasks");
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}
