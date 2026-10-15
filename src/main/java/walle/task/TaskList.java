package walle.task;

import java.util.ArrayList;
import java.util.stream.Collectors;

import walle.exceptions.DuplicateEntryException;
import walle.exceptions.InvalidIndexException;
import walle.storage.Storage;
import walle.ui.Ui;

/**
 * Holds the list of tasks WALLE is tracking, and provides operations to
 * add, remove, mark, and retrieve tasks by their 1-based position in the list.
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
     * Creates a task list wrapping tasks already loaded (e.g. from {@link Storage}).
     *
     * @param tasks the tasks to start with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     * @throws DuplicateEntryException if an identical task (same description
     *                                 and date/time) is already in the list.
     */
    public void add(Task task) {
        if (this.tasks.contains(task)) {
            throw new DuplicateEntryException("task");
        }
        this.tasks.add(task);
    }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @param pos the 1-based position of the task to remove.
     * @return the task that was removed.
     * @throws InvalidIndexException if pos isn't a valid position in the list.
     */
    public Task delete(int pos) {
        requireValidIndex(pos);
        return this.tasks.remove(pos - 1);
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @param pos the 1-based position of the task.
     * @return the task at that position.
     * @throws InvalidIndexException if pos isn't a valid position in the list.
     */
    public Task get(int pos) {
        requireValidIndex(pos);
        return this.tasks.get(pos - 1);
    }

    /**
     * Marks the task at the given 1-based position as done.
     *
     * @param pos the 1-based position of the task to mark.
     * @return the task that was marked.
     * @throws InvalidIndexException if pos isn't a valid position in the list.
     */
    public Task mark(int pos) {
        requireValidIndex(pos);
        Task task = this.tasks.get(pos - 1);
        task.check();
        return task;
    }

    /**
     * Marks the task at the given 1-based position as not done.
     *
     * @param pos the 1-based position of the task to unmark.
     * @return the task that was unmarked.
     * @throws InvalidIndexException if pos isn't a valid position in the list.
     */
    public Task unmark(int pos) {
        requireValidIndex(pos);
        Task task = this.tasks.get(pos - 1);
        task.uncheck();
        return task;
    }

    // Throws a friendly InvalidIndexException instead of letting an out-of-range
    // position surface as a raw IndexOutOfBoundsException.
    private void requireValidIndex(int pos) {
        if (pos < 1 || pos > this.tasks.size()) {
            throw new InvalidIndexException(pos, this.tasks.size(), "task");
        }
    }

    /**
     * Returns the tasks whose description contains the given keyword.
     *
     * @param keyword the keyword to search for.
     * @return the matching tasks, in their original order.
     */
    public ArrayList<Task> find(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.getTask().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the underlying list of tasks, e.g. for {@link Storage#save} or
     * {@link Ui#listTasks}.
     *
     * @return the underlying list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return this.tasks;
    }

}
