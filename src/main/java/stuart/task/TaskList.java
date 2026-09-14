package stuart.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import stuart.exception.StuartException;

/**
 * Contains the list of tasks, with operations to add, delete, and access
 * tasks in the list. Validates task numbers itself, so callers don't need
 * a separate bounds check before every {@link #get} or {@link #delete}.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks, e.g. ones just loaded
     * from disk.
     *
     * @param tasks the initial tasks, in order
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Storage#load always returns a list, even an empty one, never null";
        this.tasks = tasks;
    }

    /**
     * Appends {@code task} to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Checks whether this list already contains a task with the same
     * details as {@code candidate} (same type, description, and dates).
     *
     * @param candidate the task to check for
     * @return true if an equivalent task is already in this list
     */
    public boolean containsDuplicateOf(Task candidate) {
        return tasks.stream().anyMatch(t -> t.hasSameDetails(candidate));
    }

    /**
     * Removes and returns the task at {@code index}.
     *
     * @param index the 0-based index of the task to remove
     * @return the removed task
     * @throws StuartException if {@code index} is not a valid task number
     */
    public Task delete(int index) throws StuartException {
        checkIndex(index);
        return tasks.remove(index);
    }

    /**
     * Returns the task at {@code index}, without removing it.
     *
     * @param index the 0-based index of the task to get
     * @return the task at {@code index}
     * @throws StuartException if {@code index} is not a valid task number
     */
    public Task get(int index) throws StuartException {
        checkIndex(index);
        return tasks.get(index);
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
     * Returns a read-only view of the tasks in this list, in order.
     * Callers cannot add/remove through it; use {@link #add}/{@link #delete}.
     *
     * @return an unmodifiable view of the tasks
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks that {@code index} refers to an actual task in this list.
     *
     * @param index the 0-based index to check
     * @throws StuartException if {@code index} is out of range
     */
    private void checkIndex(int index) throws StuartException {
        if (index < 0 || index >= tasks.size()) {
            throw new StuartException("That's not a valid task number.");
        }
    }
}
