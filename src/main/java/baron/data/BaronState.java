// javadocs created by AI

package baron.data;

import java.util.ArrayList;

import baron.exception.BaronException;
import baron.exception.IndexException;
import baron.task.Task;

/**
 * Stores the application state for the Baron task manager.
 * This includes the exit flag and the current list of tasks.
 */
public class BaronState {
    /** Indicates whether the user has requested to exit the application. */
    private static boolean isExit = false;

    /** The list of tasks currently managed by the application. */
    private static ArrayList<Task> tasks;

    /**
     * Resets the application state for a fresh session.
     */
    public static void init() {
        isExit = false;
        try {
            tasks = TaskPersistence.load();
        } catch (BaronException e) {
            tasks = new ArrayList<>();
        }
    }

    /**
     * Marks the application to exit at the next loop iteration.
     */
    public static void exit() {
        isExit = true;
    }

    /**
     * Returns whether the application should terminate.
     *
     * @return true if the user has chosen to exit; false otherwise.
     */
    public static boolean getExitStatus() {
        return isExit;
    }

    /**
     * Returns the task list currently stored in the application state.
     *
     * @return the list of tasks.
     */
    public static ArrayList<Task> getTasks() {
        assert tasks != null : "BaronState must be initialized before reading tasks";
        return new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the current task list.
     *
     * @param t the task to add.
     * @throws BaronException if the updated task list cannot be saved.
     */
    public static void addTask(Task t) throws BaronException {
        assert tasks != null : "BaronState must be initialized before adding tasks";
        assert t != null : "The task list must not contain null tasks";
        tasks.add(t);
        TaskPersistence.save(tasks);
    }

    /**
     * Marks a task as done if the given index is valid.
     *
     * @param index zero-based index of the task to mark as done.
     * @throws BaronException if the index is outside the valid range.
     */
    public static void markTaskAsDone(int index) throws BaronException {
        assert tasks != null : "BaronState must be initialized before updating tasks";
        validateTaskIndex(index);
        tasks.get(index).markAsDone();
        TaskPersistence.save(tasks);
    }

    /**
     * Marks a task as not done if the given index is valid.
     *
     * @param index zero-based index of the task to unmark.
     * @throws BaronException if the index is outside the valid range.
     */
    public static void unmarkTask(int index) throws BaronException {
        assert tasks != null : "BaronState must be initialized before updating tasks";
        validateTaskIndex(index);
        tasks.get(index).unmark();
        TaskPersistence.save(tasks);
    }

    /**
     * Removes a task from the list using its zero-based index.
     *
     * @param index zero-based index of the task to remove.
     * @return the task that was removed.
     * @throws BaronException if the index is outside the valid range.
     */
    public static Task delete(int index) throws BaronException {
        assert tasks != null : "BaronState must be initialized before deleting tasks";
        validateTaskIndex(index);
        Task t = tasks.remove(index);
        TaskPersistence.save(tasks);
        return t;
    }

    /** Ensures a task index refers to an existing task. */
    private static void validateTaskIndex(int index) throws IndexException {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexException("task");
        }
    }
}
