package clarry.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Manages the tasks currently known to Clarry.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks tasks to add to the new list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection cannot be null";
        assert tasks.stream().allMatch(task -> task != null)
                : "Initial task collection cannot contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "A task list cannot contain a null task";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task to remove
     * @return removed task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the index
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
     * Returns an unmodifiable snapshot of all tasks.
     *
     * @return all tasks in list order
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Returns every deadline or event that occurs on a given date.
     *
     * @param date date to check
     * @return tasks occurring on the date, in list order
     */
    public List<Task> getTasksOnDate(LocalDate date) {
        assert date != null : "Task filtering requires a date";
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to search for
     * @return matching tasks in their original list order
     */
    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Task search requires a non-blank keyword";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns an iterator over a snapshot of the tasks in list order.
     *
     * @return task iterator
     */
    @Override
    public java.util.Iterator<Task> iterator() {
        return getTasks().iterator();
    }
}
