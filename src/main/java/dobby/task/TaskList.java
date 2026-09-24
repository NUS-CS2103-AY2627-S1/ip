package dobby.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks for the current Dobby session and provides list operations.
 */
public class TaskList {
    /** Tasks in the order they should be displayed to the user. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to add to this list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "The initial task collection must exist";
        assert tasks.stream().allMatch(task -> task != null) : "The initial task collection cannot contain null";

        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null";

        tasks.add(task);
    }

    /** Returns the task at a zero-based position. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at a zero-based position. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns a copy of the tasks for persistence. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
