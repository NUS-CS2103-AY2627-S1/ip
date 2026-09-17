package crystal.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns Crystal's task collection and provides operations on that collection.
 */
public class TaskList {
    /** Mutable task collection hidden behind task-list operations. */
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     * A defensive copy prevents callers from changing the collection directly.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the supplied task arguments.
     * A defensive copy prevents callers from changing the task array directly.
     *
     * @param tasks initial tasks.
     */
    public TaskList(Task... tasks) {
        this(List.of(tasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param taskIndex zero-based task index.
     * @return removed task.
     */
    public Task deleteTask(int taskIndex) {
        return tasks.remove(taskIndex);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param taskIndex zero-based task index.
     * @return selected task.
     */
    public Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns whether the task list contains no tasks.
     *
     * @return {@code true} if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an immutable snapshot for display or storage.
     *
     * @return snapshot of all tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns an immutable temporary view of tasks occurring on a date.
     *
     * @param date date to match.
     * @return matching deadlines and events in task-list order.
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .toList();
    }

    /**
     * Returns an immutable temporary view of tasks matching a description keyword.
     * Matching is case-insensitive and preserves task-list order.
     *
     * @param keyword keyword to find in task descriptions.
     * @return matching tasks in task-list order.
     */
    public List<Task> findTasks(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasDescriptionContaining(keyword))
                .toList();
    }
}
