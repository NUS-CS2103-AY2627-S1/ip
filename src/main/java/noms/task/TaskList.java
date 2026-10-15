package noms.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds Noms' tasks and provides the operations for changing and querying
 * them (adding, deleting, retrieving, and finding tasks on a date).
 *
 * Keeping this logic in its own class means the rest of the program works
 * with intention-revealing methods such as {@code add} and {@code delete}
 * instead of manipulating a raw list directly.
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
     * Creates a task list containing the given tasks, typically the ones
     * loaded from the save file at startup.
     *
     * @param tasks the initial tasks; the list is copied so later changes
     *              here do not affect the caller's list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Creates a task list containing the tasks supplied directly by the caller.
     *
     * @param tasks the initial tasks, in display order
     */
    public TaskList(Task... tasks) {
        this.tasks = new ArrayList<>(List.of(tasks));
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index position of the task, from 0 to {@code size() - 1}
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index is out of range";
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task being added must not be null";
        tasks.add(task);
    }

    /**
     * Adds a task at the given zero-based index, shifting later tasks right.
     *
     * @param index position at which to insert the task
     * @param task the task to insert
     */
    public void add(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "Task index is out of range";
        assert task != null : "Task being added must not be null";
        tasks.add(index, task);
    }

    /**
     * Returns whether this list contains a task with the same user-defined
     * details as the given task.
     *
     * @param task the proposed task to compare against the list
     * @return {@code true} if a matching task is already present
     */
    public boolean hasMatchingTask(Task task) {
        assert task != null : "Task being matched must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
    }

    /**
     * Removes and returns the task at the given zero-based index. Tasks
     * after it shift down to fill the gap, renumbering the list.
     *
     * @param index position of the task to remove
     * @return the task that was removed
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index is out of range";
        return tasks.remove(index);
    }

    /**
     * Returns the deadlines and events that occur on the given date, in
     * their original list order. Todos never match because they have no
     * date.
     *
     * @param date the date to filter by
     */
    public List<Task> tasksOn(LocalDate date) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (isScheduledOn(task, date)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns whether the task is a dated task that occurs on the given date.
     *
     * @param task the task to check
     * @param date the date to check against
     * @return {@code true} if the task is scheduled on the date
     */
    private static boolean isScheduledOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.occursOn(date);
        }
        if (task instanceof Event event) {
            return event.occursOn(date);
        }
        return false;
    }

    /**
     * Returns the tasks whose description contains the given keyword, in
     * their original list order. Matching is case-insensitive, so
     * {@code find BOOK} still matches a task described as "read book".
     *
     * @param keyword the text to search for within each task's description
     */
    public List<Task> find(String keyword) {
        String needle = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(needle))
                .toList();
    }

    /**
     * Returns the tasks as a plain list, for displaying or saving. The
     * returned list is a copy, so modifying it does not change this
     * TaskList.
     */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
