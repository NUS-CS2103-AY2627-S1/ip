package bibi.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bibi.BibiException;

/**
 * Stores the tasks managed by Bibi and provides safe indexed access to them.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates a task list holding the tasks given, in the order given.
     *
     * <p>Varargs, so that both common cases read directly: {@code new TaskList()}
     * for an empty list, and {@code new TaskList(todo, deadline)} where the tasks
     * are known where the list is built, as they are throughout the tests. This
     * replaces a separate no-argument constructor, which it already covers.
     *
     * @param initialTasks the tasks to start with, if any
     */
    public TaskList(Task... initialTasks) {
        tasks = new ArrayList<>(List.of(initialTasks));
    }

    /**
     * Creates a task list holding the supplied tasks, in the given order.
     *
     * <p>Kept alongside the varargs form for callers that already hold a list,
     * such as the one just read from the save file. The tasks are copied, so
     * later changes to this list do not disturb the list handed in.
     *
     * @param initialTasks the tasks to start with
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to store
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Reports whether this list contains no tasks.
     *
     * @return {@code true} when there are no stored tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns every task in list order.
     *
     * <p>The returned list is unmodifiable so callers such as {@link bibi.Storage Storage} can
     * read the tasks without accidentally changing them.
     *
     * @return a read-only view of the stored tasks
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the matching task
     * @throws BibiException if the task number does not exist
     */
    public Task get(int taskNumber) throws BibiException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the removed task
     * @throws BibiException if the task number does not exist
     */
    public Task remove(int taskNumber) throws BibiException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Converts and validates a displayed one-based task number.
     *
     * @param taskNumber one-based task number
     * @return the matching zero-based list index
     * @throws BibiException if the task number does not exist
     */
    private int toIndex(int taskNumber) throws BibiException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new BibiException("That task number does not exist.");
        }
        return taskIndex;
    }
}
