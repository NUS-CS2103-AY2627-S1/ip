package duke;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Holds the list of tasks and provides operations to add, delete, and access them.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks the initial tasks (e.g. loaded from storage)
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index the zero-based position of the task
     * @return the removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index the zero-based position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return true if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a new {@code TaskList} containing every task whose name contains
     * the given keyword.
     *
     * @param keyword the substring to search for within task names
     * @return a {@code TaskList} of the matching tasks (empty if none match)
     */
    public TaskList find(String keyword) {
        ArrayList<Task> matches = tasks.stream()
                .filter(task -> task.getName().contains(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new TaskList(matches);
    }
}
