package trackie.task;

import java.util.ArrayList;
import java.util.List;

/** Manages the tasks currently known to Trackie. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks loaded from storage
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return number of tasks in the list
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the given index
     */
    public Task get(int index) {
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid";
        return this.tasks.get(index);
    }

    /**
     * Appends a task to the list.
     *
     * @param task task to append
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        this.tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based index of the task
     * @return removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < this.tasks.size() : "Task index must be valid";
        return this.tasks.remove(index);
    }

    /**
     * Creates a read-only snapshot of the current tasks.
     *
     * @return task snapshot suitable for saving
     */
    public List<Task> asList() {
        return List.copyOf(this.tasks);
    }

    /**
     * Finds tasks whose descriptions contain a keyword.
     *
     * @param keyword text to search for
     * @return matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
    }
}
