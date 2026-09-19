package nova;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Wraps the in-memory list of tasks and the operations on it.
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
     * Creates a task list backed by the given list of tasks.
     *
     * @param tasks the tasks to start with
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Added task must not be null";
        this.tasks.add(task);
    }

    /**
     * Removes the task at the given index and returns it.
     *
     * @param index the zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Remove index must be within the list";
        return this.tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based index of the task
     * @return the task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Access index must be within the list";
        return this.tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return the list of tasks
     */
    public ArrayList<Task> getAll() {
        return this.tasks;
    }

    /**
     * Removes all completed tasks from the list and returns them.
     *
     * @return the removed completed tasks, in their original order
     */
    public ArrayList<Task> removeCompletedTasks() {
        ArrayList<Task> completed = tasks.stream()
                .filter(Task::isDone)
                .collect(Collectors.toCollection(ArrayList::new));
        tasks.removeIf(Task::isDone);
        return completed;
    }
}
