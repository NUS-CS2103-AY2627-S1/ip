package henry.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores Henry's tasks and provides operations that change the task list.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns a read-only view of the tasks for display and storage.
     *
     * @return current tasks.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index task index.
     * @return selected task.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to search for.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
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
     * Inserts a task at a specific position.
     *
     * @param index insertion index.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Deletes and returns the task at the given index.
     *
     * @param index task index.
     * @return deleted task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Sets whether the selected task has been completed.
     *
     * @param index task index.
     * @param isDone true to mark the task as done, or false to mark it as not done.
     */
    public void setDone(int index, boolean isDone) {
        tasks.get(index).setDone(isDone);
    }
}
