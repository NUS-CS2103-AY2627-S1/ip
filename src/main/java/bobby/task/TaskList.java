package bobby.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Stores and manages the tasks currently used by Bobby.
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
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the tasks to place in this list
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list must not be null";
        assert tasks.stream().noneMatch(Objects::isNull)
                : "Initial task list must not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        tasks.add(task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based index
     * @return the selected task
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks.get(index);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index the zero-based index
     */
    public void markAsDone(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        tasks.get(index).markAsDone();
    }

    /**
     * Marks the task at a zero-based index as done at the supplied date and time.
     *
     * @param index the zero-based index.
     * @param completionDateTime the date and time at which the task was completed.
     */
    public void markAsDone(int index, LocalDateTime completionDateTime) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        assert completionDateTime != null : "Completion date and time must not be null";
        tasks.get(index).markAsDone(completionDateTime);
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index the zero-based index
     */
    public void markAsNotDone(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        tasks.get(index).markAsNotDone();
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based index
     * @return the removed task
     */
    public Task remove(int index) {
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this list contains no tasks.
     *
     * @return {@code true} when no tasks are stored
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns an unmodifiable snapshot of the current tasks.
     *
     * @return the tasks in their current order
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns whether this list already contains a task with the same defining details.
     *
     * @param candidate the task to compare against the stored tasks.
     * @return {@code true} when an equivalent task is already stored.
     */
    public boolean hasTaskWithSameDetails(Task candidate) {
        assert candidate != null : "Candidate task must not be null";
        return tasks.stream().anyMatch(task -> task.hasSameDetailsAs(candidate));
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, ignoring letter case.
     *
     * @param keyword the keyword to search for
     * @return the matching tasks in their original order
     */
    public List<Task> findTasksContaining(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Search keyword must not be blank";
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription()
                        .toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .toList();
    }

    /**
     * Returns the number of currently completed tasks.
     *
     * @return the number of completed tasks.
     */
    public long countCompletedTasks() {
        return tasks.stream()
                .filter(Task::isDone)
                .count();
    }

    /**
     * Returns the number of currently completed tasks completed within a date-time interval.
     *
     * @param startDateTime the inclusive start of the interval.
     * @param endDateTime the exclusive end of the interval.
     * @return the number of tasks completed within the interval.
     */
    public long countTasksCompletedBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        assert startDateTime != null : "Interval start must not be null";
        assert endDateTime != null : "Interval end must not be null";
        assert startDateTime.isBefore(endDateTime) : "Interval start must be before its end";
        return tasks.stream()
                .filter(task -> task.wasCompletedBetween(startDateTime, endDateTime))
                .count();
    }

    /**
     * Returns whether an index refers to a task currently in this list.
     *
     * @param index the zero-based index to inspect
     * @return {@code true} when the index is within the current list bounds
     */
    private boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }
}
