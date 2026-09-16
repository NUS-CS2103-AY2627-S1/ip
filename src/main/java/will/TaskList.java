package will;

import java.util.ArrayList;
import java.util.Iterator;

import will.task.Task;

/**
 * Contains the task list, with operations to add, remove, and access
 * tasks in it. Wraps an ArrayList&lt;Task&gt; so callers (Will, and later
 * the Command classes) work in terms of tasks rather than the
 * underlying list implementation.
 *
 * Implements Iterable so existing for-each loops over the task list
 * (e.g. "list" and "on &lt;date&gt;") keep working unchanged.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Starts an empty task list, e.g. when loading from disk failed. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Wraps an already-populated list, e.g. one just read from Storage. */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "TaskList should never be constructed with a null list";
        this.tasks = tasks;
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * <p>Callers (currently the Command classes) are expected to have
     * already bounds-checked {@code index} against user input via
     * {@link will.command.Command#requireValidIndex}, which reports a
     * bad index to the user as a {@link will.WillException} rather than
     * letting it reach here. This assertion documents that expectation
     * so a bug that skips that check surfaces immediately in testing,
     * instead of silently corrupting the list.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "remove() index " + index + " should already be validated by the caller";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * <p>Same precondition as {@link #remove(int)}: the index should
     * already be known valid by the time it reaches here.
     *
     * @return The task at the given index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "get() index " + index + " should already be validated by the caller";
        return tasks.get(index);
    }

    /** @return How many tasks are in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying ArrayList, for callers that still need it
     * directly (currently Storage#save, which serializes the whole list
     * to disk).
     *
     * @return The underlying task list.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /** @return An iterator over the tasks, in list order. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
