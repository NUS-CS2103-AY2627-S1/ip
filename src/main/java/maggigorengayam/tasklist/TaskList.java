package maggigorengayam.tasklist;

import java.util.ArrayList;
import java.util.List;

import maggigorengayam.task.Task;

/**
 * Holds the in-memory task list and the operations that add to, remove
 * from, or read it. Command handling and storage go through this instead
 * of manipulating a raw {@code List<Task>} directly, so the "list of
 * tasks" concept has one home instead of being spread across callers.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Wraps the given list directly (not copied) as the task list's backing storage. */
    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    /** Appends {@code task} to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at {@code index}, shifting later tasks down by one. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at {@code index}. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** The number of tasks currently in the list. */
    public int size() {
        return tasks.size();
    }

    /** The tasks in insertion order, e.g. for `list`/`on` iteration or saving to disk. */
    public List<Task> getAll() {
        return tasks;
    }
}
