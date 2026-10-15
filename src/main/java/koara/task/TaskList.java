package koara.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manages the tasks stored by Koara.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert !tasks.contains(null) : "Initial task list must not contain null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index Zero-based task index.
     * @return Task at the index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Inserts a task at a specified zero-based index.
     *
     * @param index Position at which to insert the task.
     * @param task Task to insert.
     */
    public void insert(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "Task insertion index must be within the list";
        assert task != null : "Task to insert must not be null";
        tasks.add(index, task);
    }

    /**
     * Returns whether an equivalent task is already in the list.
     *
     * @param task Task whose details should be checked.
     * @return True when a task with the same details exists.
     */
    public boolean containsEquivalent(Task task) {
        assert task != null : "Task to compare must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index Zero-based task index.
     * @return Removed task.
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index Zero-based task index.
     */
    public void mark(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the specified index as not done.
     *
     * @param index Zero-based task index.
     */
    public void unmark(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original order.
     */
    public TaskList find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword must not be blank";
        ArrayList<Task> matchingTasks = tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matchingTasks);
    }

    /**
     * Returns every task in the data-file format.
     *
     * @return Serialized task lines.
     */
    public ArrayList<String> toDataLines() {
        return tasks.stream()
                .map(Task::toDataString)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
