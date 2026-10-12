package echo.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores tasks in insertion order and provides one-based lookup.
 */
public class TaskList {
    /** The tasks currently in the user's list. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns a task using its one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return the removed task.
     */
    public Task removeTask(int taskNumber) {
        assert hasTask(taskNumber) : "A task number must refer to an existing task";
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns a task using its one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return task at that position.
     */
    public Task getTask(int taskNumber) {
        assert hasTask(taskNumber) : "A task number must refer to an existing task";
        return tasks.get(taskNumber - 1);
    }

    /**
     * Checks whether a task number is available.
     *
     * @param taskNumber one-based task number.
     * @return whether the task exists.
     */
    public boolean hasTask(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Checks whether an identical task already exists in this list.
     *
     * @param task task to test for duplicates.
     * @return true if an identical task is found; false otherwise.
     */
    public boolean hasDuplicate(Task task) {
        return tasks.stream().anyMatch(existing -> existing.isDuplicate(task));
    }

    /**
     * Returns the number of stored tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a read-only snapshot of the tasks in insertion order.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns a list of tasks whose description contains the search keyword.
     *
     * @param keyword the keyword to search for.
     * @return list of matching tasks in insertion order.
     */
    public List<Task> findByKeyword(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Sorts the tasks in this list using the supplied comparator.
     *
     * @param comparator comparator used to determine the task ordering.
     */
    public void sort(Comparator<Task> comparator) {
        tasks.sort(comparator);
    }
}
