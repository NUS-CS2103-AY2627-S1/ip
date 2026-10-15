package clearblue.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds the list of tasks and the operations that add, remove, and look
 * them up.
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
     * Creates a task list pre-populated with the given tasks (e.g. loaded
     * from storage).
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at the given index, shifting later tasks back by one.
     * Used by {@link clearblue.command.DeleteCommand#undo} to restore a
     * removed task to its original position.
     *
     * @param index zero-based index to insert at
     * @param task task to insert
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index zero-based index of the task to remove
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index zero-based index
     * @return the task at that index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list has no tasks.
     *
     * @return {@code true} if the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns all tasks currently in the list, e.g. for saving to disk.
     *
     * @return an independent copy of the task list
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the deadlines and events whose date matches {@code queryDate}.
     * A deadline matches on its {@code by} date; an event matches if either
     * its {@code from} or {@code to} date matches. Todos never match, since
     * they carry no date.
     *
     * @param queryDate date to match against
     * @return matching tasks, in list order
     */
    public List<Task> getTasksOnDate(LocalDate queryDate) {
        return tasks.stream()
                .filter(task -> isOnDate(task, queryDate))
                .collect(Collectors.toList());
    }

    /**
     * Returns the tasks whose description contains {@code keyword},
     * matched case-insensitively.
     *
     * @param keyword text to search for within each task's description
     * @return matching tasks, in list order
     */
    public List<Task> getTasksContaining(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether a task falls on the given date: a deadline matches on
     * its {@code by} date, an event on either its {@code from} or
     * {@code to} date, and a todo never matches (it carries no date).
     *
     * @param task task to check
     * @param queryDate date to match against
     * @return {@code true} if the task falls on that date
     */
    private static boolean isOnDate(Task task, LocalDate queryDate) {
        if (task instanceof Deadline deadline) {
            return matchesDate(deadline.getBy(), queryDate);
        } else if (task instanceof Event event) {
            return matchesDate(event.getFrom(), queryDate) || matchesDate(event.getTo(), queryDate);
        }
        return false;
    }

    /**
     * Checks whether a date-or-text value is a real date equal to
     * {@code queryDate}. Free-form text (not recognized as a date) never
     * matches, regardless of its content.
     *
     * @param dateTime value to check
     * @param queryDate date to match against
     * @return {@code true} if {@code dateTime} is a real date equal to {@code queryDate}
     */
    private static boolean matchesDate(TaskDateTime dateTime, LocalDate queryDate) {
        return dateTime.isDate() && dateTime.getDate().equals(queryDate);
    }
}
