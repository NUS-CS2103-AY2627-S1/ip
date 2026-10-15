package amy;

import java.util.ArrayList;

import amy.task.Task;

/**
 * Stores Amy's tasks and provides operations for changing the collection.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks
     */
    public TaskList(ArrayList<Task> tasks) {
        // A task list must always have a backing collection so that all list operations are safe.
        assert tasks != null : "Task list backing collection must not be null";
        this.tasks = tasks;
    }

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Returns whether this list contains no tasks.
     *
     * @return true when this list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index the task index
     * @return the task at the index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        // Null tasks cannot be displayed, serialized, or marked later.
        assert task != null : "Task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index the task index
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at the given zero-based index as done.
     *
     * @param index the task index
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at the given zero-based index as not done.
     *
     * @param index the task index
     */
    public void unmark(int index) {
        tasks.get(index).unmarkAsDone();
    }

    /**
     * Returns the current tasks for persistence.
     *
     * @return the current tasks
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /** Replaces the current tasks with a copied snapshot. */
    public void restore(ArrayList<Task> snapshot) {
        tasks.clear();
        for (Task task : snapshot) {
            tasks.add(task.copy());
        }
    }
}
