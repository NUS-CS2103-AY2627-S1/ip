package alzara.task;

import java.util.ArrayList;

/**
 * Wraps the in-memory task list and exposes the operations Alzara performs
 * on it, so callers manipulate tasks through named operations instead of
 * reaching into a raw {@code ArrayList} directly.
 */
public class TaskList {
    private final ArrayList<Task> memory;

    /**
     * Wraps an existing list of tasks, e.g. one just loaded by
     * {@link alzara.storage.Storage#load()}.
     *
     * @param tasks the list to wrap; mutated in place by {@link #add}/{@link #delete}
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "TaskList must wrap a non-null list; Storage.load() never returns null";
        this.memory = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        this.memory.add(task);
    }

    /**
     * Returns true if {@code task} has the same details (per {@link
     * Task#equals}) as a task already in the list, e.g. two {@code todo}s
     * with the same description, or two {@code deadline}s with the same
     * description and due date. Ignores each task's completion state.
     *
     * @param task the task to check for a duplicate of
     * @return true if an equal task already exists in the list
     */
    public boolean hasDuplicate(Task task) {
        return this.memory.contains(task);
    }

    /**
     * Returns the task at {@code index}.
     *
     * @param index 0-based index; not bounds-checked with a thrown exception -
     *         guarded by an assert instead (an {@link AssertionError} when
     *         assertions are enabled, otherwise whatever {@link ArrayList#get}
     *         does). Callers (see the {@code Command} classes) are
     *         responsible for validating the index first.
     * @return the task at {@code index}
     */
    public Task get(int index) {
        assert index >= 0 && index < memory.size()
                : "index out of bounds - caller must validate before calling get()";
        return this.memory.get(index);
    }

    /**
     * Removes the task at {@code index}, shifting later tasks down by one.
     *
     * @param index 0-based index; not bounds-checked here (see {@link #get})
     * @return the removed task
     */
    public Task delete(int index) {
        assert index >= 0 && index < memory.size()
                : "index out of bounds - caller must validate before calling delete()";
        return this.memory.remove(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return the current task count
     */
    public int size() {
        return this.memory.size();
    }

    /**
     * Exposes the underlying list for callers that need to read or persist
     * every task at once, e.g. {@link alzara.storage.Storage#save} and
     * {@link alzara.ui.Ui#showTaskList}.
     *
     * @return the underlying list of every task currently tracked
     */
    public ArrayList<Task> getTasks() {
        return this.memory;
    }
}
