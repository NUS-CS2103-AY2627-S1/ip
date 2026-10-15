package shrekanddonkey.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Stores and provides operations on the chatbot's tasks.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param tasks tasks to include in this task list.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Tasks cannot be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add cannot be null";
        int initialSize = tasks.size();
        tasks.add(task);
        assert tasks.size() == initialSize + 1 : "TaskList size should increment after adding task";
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the given index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the tasks for display or storage.
     *
     * @return tasks in their current order.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns all tasks whose description contains the specified keyword.
     *
     * @param keyword search term to look for in task descriptions.
     * @return list of matching tasks.
     */
    public List<Task> findTasks(String keyword) {
        assert keyword != null : "Search keyword cannot be null";
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        return Collections.unmodifiableList(matchingTasks);
    }

    /**
     * Sorts deadline and event tasks by date, placing tasks without a date last.
     * Tasks with equal dates retain their existing order because list sorting is stable.
     */
    public void sortByDate() {
        tasks.sort(Comparator.comparing(this::getTaskDate,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * Returns the date used to sort a task.
     *
     * @param task task to inspect.
     * @return deadline date, event start date, or {@code null} for other task types.
     */
    private LocalDateTime getTaskDate(Task task) {
        if (task instanceof Deadline) {
            return ((Deadline) task).getBy();
        }

        if (task instanceof Event) {
            return ((Event) task).getFrom();
        }

        return null;
    }
}
