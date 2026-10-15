package peanutbuttercat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Stores the tasks managed by the chatbot and provides collection operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks Tasks to copy into this list.
     */
    public TaskList(Collection<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Inserts a task at the given zero-based index when reverting a failed deletion. */
    void insert(int index, Task task) {
        tasks.add(index, task);
    }

    /** Removes and returns the task at the zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the task at the zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether a task with the same type and details is already stored. */
    public boolean containsSameDetails(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /** Returns the tasks for read-only iteration by coordinating components. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword.
     * Matching is case-insensitive and keeps the task-list order.
     *
     * @param keyword Text to find in task descriptions.
     * @return Matching tasks in their original order.
     */
    public List<Task> findByDescription(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Counts completed tasks whose known completion dates fall within an inclusive range.
     *
     * @param startDate First completion date to include.
     * @param endDate Last completion date to include.
     * @return Number of completed tasks in the date range.
     */
    public long countCompletedBetween(LocalDate startDate, LocalDate endDate) {
        assert startDate != null : "Statistics start date must not be null";
        assert endDate != null : "Statistics end date must not be null";
        assert !startDate.isAfter(endDate) : "Statistics start date must not be after end date";

        return tasks.stream()
                .filter(Task::isDone)
                .map(Task::getCompletionDate)
                .filter(date -> date != null && !date.isBefore(startDate) && !date.isAfter(endDate))
                .count();
    }

    /** Returns the number of completed tasks whose completion dates are unknown. */
    public long countCompletedWithUnknownDate() {
        return tasks.stream()
                .filter(Task::isDone)
                .filter(task -> task.getCompletionDate() == null)
                .count();
    }
}
