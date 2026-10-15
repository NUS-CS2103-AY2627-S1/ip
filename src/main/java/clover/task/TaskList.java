package clover.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores Clover's tasks and provides list operations for commands.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a copy of the supplied tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list must have a backing collection.";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to this list.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks.";
        tasks.add(task);
    }

    /** Inserts a task at the supplied zero-based index. */
    public void add(int index, Task task) {
        assert task != null : "A task list must not contain null tasks.";
        tasks.add(index, task);
    }

    /** Returns whether a task with the same type and user-visible details is already stored. */
    public boolean containsEquivalent(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the last task in this list.
     */
    public Task getLast() {
        assert !tasks.isEmpty() : "The last task is requested only after a task is added.";
        return tasks.getLast();
    }

    /**
     * Returns the number of tasks in this list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks in this list.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
