package aster.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Wraps the mutable list of tasks and exposes task-list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list from the supplied tasks.
     *
     * @param tasks tasks to copy into this list
     */
    public TaskList(Task... tasks) {
        assert tasks != null : "TaskList should always be built from a task array.";
        this.tasks = new ArrayList<>(Arrays.asList(tasks));
        assert !this.tasks.contains(null) : "TaskList must not contain null tasks.";
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "TaskList only stores real tasks.";
        tasks.add(task);
    }

    /**
     * Removes and returns a task at the given zero-based index.
     *
     * @param index index of the task to remove
     * @return removed task
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task removal index should already be validated.";
        return tasks.remove(index);
    }

    /**
     * Returns a task at the given zero-based index.
     *
     * @param index index of task
     * @return task at index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task lookup index should already be validated.";
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks currently in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of all tasks.
     *
     * @return unmodifiable list of tasks
     */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Finds tasks that occur on the specified date.
     *
     * @param targetDate target date
     * @return matching tasks in current list order
     */
    public List<Task> findTasksOnDate(LocalDate targetDate) {
        assert targetDate != null : "Date filtering requires a target date.";
        return tasks.stream()
                .filter(task -> matchesDate(task, targetDate))
                .toList();
    }

    /**
     * Finds tasks whose description contains the given keyword, ignoring case.
     *
     * @param keyword keyword to search for
     * @return matching tasks in current list order
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "Keyword search requires a keyword.";
        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword))
                .toList();
    }

    /**
     * Returns whether a task with the same details already exists in this list.
     *
     * @param candidate task to check
     * @return {@code true} when an equivalent task is already present
     */
    public boolean containsTaskWithSameDetails(Task candidate) {
        assert candidate != null : "Duplicate checks require a candidate task.";
        return tasks.stream().anyMatch(task -> hasSameDetails(task, candidate));
    }

    private static boolean matchesDate(Task task, LocalDate targetDate) {
        assert targetDate != null : "Date filtering requires a target date.";
        if (task instanceof Deadline deadline) {
            LocalDate dueDate = deadline.getByDate();
            return dueDate != null && dueDate.equals(targetDate);
        }
        return task instanceof Event event && event.occursOn(targetDate);
    }

    private static boolean hasSameDetails(Task first, Task second) {
        if (!first.getClass().equals(second.getClass())) {
            return false;
        }
        if (!first.getDescription().equals(second.getDescription())) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getByText().equals(secondDeadline.getByText());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return firstEvent.getFrom().equals(secondEvent.getFrom())
                    && firstEvent.getTo().equals(secondEvent.getTo());
        }
        return true;
    }
}
