package gatsby.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gatsby.storage.Storage;

/**
 * Owns Gatsby's in-memory collection of tasks.
 *
 * Keeping list operations here gives the rest of the program one place to
 * manage task storage, while still allowing {@link Storage} to save a read-only
 * view of the current tasks.
 */
public class TaskList {
    /** The tasks currently known to Gatsby, in the order they were added. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param initialTasks tasks recovered from storage
     */
    public TaskList(List<Task> initialTasks) {
        if (initialTasks == null) {
            throw new IllegalArgumentException("A task list must be initialized with a task collection.");
        }
        for (Task task : initialTasks) {
            if (task == null) {
                throw new IllegalArgumentException("A task list must not contain null tasks.");
            }
        }
        this.tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("A task list must not contain null tasks.");
        }
        int previousSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousSize + 1 : "Adding one task must increase the list size by one.";
        assert tasks.get(previousSize) == task : "A newly added task must be stored at the end of the list.";
    }

    /**
     * Returns the task at a zero-based position.
     *
     * @param index the zero-based position
     * @return the task at that position
     */
    public Task get(int index) {
        checkIndex(index);
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based position.
     *
     * @param index the zero-based position
     * @return the removed task
     */
    public Task remove(int index) {
        checkIndex(index);
        int previousSize = tasks.size();
        Task removedTask = tasks.remove(index);
        assert tasks.size() == previousSize - 1 : "Removing one task must decrease the list size by one.";
        assert removedTask != null : "Removing a task must return a non-null task.";
        return removedTask;
    }

    /**
     * Returns whether a task with the same details is already in this list.
     *
     * @param candidate the task to look for
     * @return true when an equivalent task is already present
     */
    public boolean containsEquivalent(Task candidate) {
        if (candidate == null) {
            return false;
        }
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /** Validates a zero-based task index before accessing the backing list. */
    private void checkIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index must refer to an existing task.");
        }
    }

    /**
     * Returns a read-only view for persistence.
     *
     * @return the current tasks as an unmodifiable list
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true when the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }
}
