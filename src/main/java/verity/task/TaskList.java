package verity.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds the list of tasks the user is tracking, and the operations to
 * add, remove, retrieve, and query them.
 */
public class TaskList {
    // A-Collections: dynamically-sized storage for tasks, so there's no
    // fixed capacity to run out of.
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list pre-populated with the given tasks, e.g. ones just loaded from disk.
     *
     * @param tasks Tasks to start with.
     */
    public TaskList(List<Task> tasks) {
        // Storage.load() always returns a list (empty at worst, never null) - a null here
        // would mean that contract was broken, not that the user did anything wrong.
        assert tasks != null : "tasks list must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the given index, shifting later tasks back. Used to restore a task
     * to its original position, e.g. when undoing its removal.
     *
     * @param index 0-based index to insert at.
     * @param task Task to insert.
     */
    public void add(int index, Task task) {
        assert index >= 0 && index <= tasks.size() : "insertion index out of bounds";
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index 0-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        // Every command that removes a task (e.g. DeleteCommand) calls requireValidIndex()
        // first and only reaches here once that's passed - an invalid index at this point
        // would mean a command forgot that check, not that the user gave a bad index.
        assert isValidIndex(index) : "remove() must only be called with a valid index";
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index 0-based index of the task.
     * @return Task at that index.
     */
    public Task get(int index) {
        // Same reasoning as remove(): every caller (Mark/Unmark/Delete commands) checks
        // requireValidIndex() before calling get(), so an invalid index here is a caller bug.
        assert isValidIndex(index) : "get() must only be called with a valid index";
        return tasks.get(index);
    }

    /**
     * Returns whether the given 0-based index refers to an existing task.
     *
     * @param index Index to check.
     * @return {@code true} if a task exists at that index.
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return {@code true} if the list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an unmodifiable view of all tasks, in list order.
     *
     * @return All tasks in the list.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns every deadline due, or event spanning, the given date.
     *
     * @param date Date to filter tasks by.
     * @return Matching tasks, in list order.
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> isOnDate(task, date))
                .collect(Collectors.toList());
    }

    /**
     * Returns every task whose description contains the given keyword (case-insensitive).
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks, in list order.
     */
    public List<Task> findByKeyword(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Returns whether the given task falls on the given date: a {@link Deadline} matches if
     * its due date equals {@code date}; an {@link Event} matches if {@code date} falls within
     * its start and end date (inclusive). A {@link Todo} never matches, since it has no date.
     *
     * @param task Task to check.
     * @param date Date to check against.
     * @return {@code true} if the task falls on the given date.
     */
    private static boolean isOnDate(Task task, LocalDate date) {
        if (task instanceof Deadline) {
            return ((Deadline) task).getBy().equals(date);
        }
        if (task instanceof Event) {
            Event event = (Event) task;
            return !date.isBefore(event.getFrom()) && !date.isAfter(event.getTo());
        }
        return false;
    }
}
