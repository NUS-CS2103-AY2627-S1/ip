package yawned.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and manages the tasks in the chatbot's list.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks Tasks to add to the new list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task with the given one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Removed task.
     * @throws IndexOutOfBoundsException If {@code taskNumber} does not identify a task in this list.
     */
    public Task deleteTask(int taskNumber) {
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the task with the given one-based number.
     *
     * @param taskNumber One-based task number.
     * @return Selected task.
     * @throws IndexOutOfBoundsException If {@code taskNumber} does not identify a task in this list.
     */
    public Task getTask(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /**
     * Marks the task with the given one-based number as done.
     *
     * @param taskNumber One-based task number.
     * @return Task after its status has been updated.
     */
    public Task markTask(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks the task with the given one-based number as not done.
     *
     * @param taskNumber One-based task number.
     * @return Task after its status has been updated.
     */
    public Task unmarkTask(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsUndone();
        return task;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to search for in task descriptions
     * @return matching tasks in their list order
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return True if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a read-only view of the tasks for display and storage.
     *
     * @return Read-only tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }
}
