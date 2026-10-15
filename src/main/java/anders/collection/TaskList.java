package anders.collection;

import java.util.ArrayList;
import java.util.List;

import anders.task.Task;

/**
 * Stores the tasks currently known to Anders and provides list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task collection must not be null";
        this.tasks = new ArrayList<>(tasks);
        assert !this.tasks.contains(null) : "A task list must not contain null tasks";
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /** Removes and returns the task at the zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at the zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }
}
