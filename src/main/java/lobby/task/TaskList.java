package lobby.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Owns the in-memory task collection and provides operations on task numbers.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list containing zero or more supplied tasks.
     * A defensive copy prevents changes to the supplied array from affecting this list.
     *
     * @param tasks initial tasks.
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Creates a task list containing the supplied tasks.
     * A defensive copy prevents outside code from changing the collection directly.
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
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a one-based position.
     * This supports restoring a deleted task if persistence fails.
     *
     * @param taskNumber one-based position at which to insert the task.
     * @param task task to insert.
     */
    public void add(int taskNumber, Task task) {
        tasks.add(toIndex(taskNumber), task);
    }

    /**
     * Returns the task with the given one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return the selected task
     */
    public Task get(int taskNumber) {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns the task with the given one-based task number.
     *
     * @param taskNumber one-based task number.
     * @return the removed task
     */
    public Task delete(int taskNumber) {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks a task as completed and returns it for display.
     *
     * @param taskNumber one-based task number.
     * @return the updated task
     */
    public Task mark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks a task as incomplete and returns it for display.
     *
     * @param taskNumber one-based task number.
     * @return the updated task
     */
    public Task unmark(int taskNumber) {
        Task task = get(taskNumber);
        task.markAsNotDone();
        return task;
    }

    /**
     * Checks whether a one-based task number identifies a task in this list.
     *
     * @param taskNumber one-based task number to check.
     * @return {@code true} when the task number is valid
     */
    public boolean containsTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Finds tasks containing every whitespace-separated keyword, ignoring case and word order.
     *
     * @param keyword text to search for in task descriptions.
     * @return a new task list containing all matching tasks
     */
    public TaskList find(String keyword) {
        String[] keywords = keyword.trim().toLowerCase(Locale.ROOT).split("\\s+");
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> Arrays.stream(keywords)
                        .allMatch(word -> task.getDescription().toLowerCase(Locale.ROOT).contains(word)))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the task references for display or persistence.
     * Later additions and deletions do not affect the snapshot, but the task objects remain shared and mutable.
     *
     * @return snapshot of the tasks in their current order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Converts a one-based task number to its zero-based list index.
     *
     * @param taskNumber one-based task number.
     * @return zero-based index
     */
    private int toIndex(int taskNumber) {
        return taskNumber - 1;
    }
}
