package alice.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Stores and manages the tasks currently known by Alice.
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
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks The tasks to add to this list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks.";
        tasks.add(task);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task.
     * @return The task at the specified index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must identify an existing task.";
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index The zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must identify an existing task.";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return The task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns an immutable view of the current tasks for saving.
     *
     * @return The current tasks.
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns the deadlines and events that occur on the specified date.
     *
     * @param date The date to match.
     * @return The tasks occurring on the specified date.
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        assert date != null : "A date search requires a date.";
        return getMatchingTasks(task -> occursOn(task, date));
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword, ignoring case.
     *
     * @param keyword The text to search for in task descriptions.
     * @return The matching tasks in their original order.
     */
    public List<Task> getTasksWithKeyword(String keyword) {
        assert keyword != null && !keyword.isBlank() : "A keyword search requires a non-blank keyword.";
        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return getMatchingTasks(task -> task.getDescription()
                .toLowerCase(Locale.ROOT).contains(lowercaseKeyword));
    }

    /** Returns the tasks that satisfy the supplied condition, in their original order. */
    private List<Task> getMatchingTasks(Predicate<Task> matches) {
        return tasks.stream()
                .filter(matches)
                .toList();
    }

    /**
     * Returns the zero-based position of a task in this list.
     *
     * @param task The task to locate.
     * @return The zero-based position of the task.
     */
    public int indexOf(Task task) {
        return tasks.indexOf(task);
    }

    /**
     * Returns whether a task's date or date range includes the specified date.
     *
     * @param task The task to check.
     * @param date The date to match.
     * @return Whether the task occurs on the specified date.
     */
    private boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().equals(date);
        }
        if (task instanceof Event event) {
            return !date.isBefore(event.getFrom().toLocalDate())
                    && !date.isAfter(event.getTo().toLocalDate());
        }
        return false;
    }
}
