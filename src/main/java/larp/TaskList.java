package larp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Manages the collection of tasks tracked by Larp.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Initial tasks to place in the list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns whether this task list contains no tasks.
     *
     * @return {@code true} if the list is empty, or {@code false} otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Task at the specified index.
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must be within the task list";
        return this.tasks.get(index);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        this.tasks.add(task);
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword, ignoring case.
     *
     * @param keyword Keyword to search for.
     * @return New task list containing matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = this.tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Task index must be within the task list";
        return this.tasks.remove(index);
    }

    /**
     * Marks the selected task as completed.
     *
     * @param index Zero-based task index.
     * @return Updated task.
     */
    public Task markAsDone(int index) {
        Task task = get(index);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the selected task as incomplete.
     *
     * @param index Zero-based task index.
     * @return Updated task.
     */
    public Task markAsNotDone(int index) {
        Task task = get(index);
        task.markAsNotDone();
        return task;
    }

    /**
     * Returns a copy of the tasks for saving or display.
     *
     * @return Copy of the current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(this.tasks);
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < this.tasks.size();
    }
}
