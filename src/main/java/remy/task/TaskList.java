package remy.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Stores and provides operations on the tasks managed by Remy.
 *
 * @author LEE-wz
 */
public class TaskList {
    /** The tasks in their current canonical order. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks.
     *
     * @param tasks tasks to add to this list
     * @throws IllegalArgumentException if the collection contains tasks with duplicate details
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task collection must not be null";
        assert tasks.stream().noneMatch(task -> task == null)
                : "Initial task collection must not contain null";

        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!add(task)) {
                throw new IllegalArgumentException("Initial task collection contains duplicate task details");
            }
        }
    }

    /**
     * Adds a task to the end of this list unless a task with the same details already exists.
     *
     * @param task task to add
     * @return true when the task was added, or false when it duplicates an existing task
     */
    public boolean add(Task task) {
        assert task != null : "Task to add must not be null";

        boolean hasDuplicate = tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetailsAs(task));
        if (hasDuplicate) {
            return false;
        }

        tasks.add(task);
        return true;
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task
     * @return the task at the given index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at the given zero-based index.
     *
     * @param index zero-based index of the task
     * @return the removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether this task list contains no tasks.
     *
     * @return true if this list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns tasks whose descriptions contain the given keyword, regardless of letter case.
     *
     * @param keyword keyword to search for
     * @return matching tasks in their current order
     */
    public List<Task> find(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        assert !keyword.isBlank() : "Search keyword must not be blank";

        String lowercaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(lowercaseKeyword))
                .toList();
    }

    /**
     * Sorts dated tasks chronologically while keeping undated tasks at the end.
     *
     * <p>Tasks with equal chronological keys retain their relative order.</p>
     *
     * @param isAscending true to place earlier tasks first, or false to place later tasks first
     */
    public void sortByDate(boolean isAscending) {
        Comparator<LocalDateTime> dateTimeComparator = isAscending
                ? Comparator.naturalOrder()
                : Comparator.reverseOrder();
        Comparator<Task> taskComparator = Comparator.comparing(
                task -> task.getChronologicalDateTime().orElse(null),
                Comparator.nullsLast(dateTimeComparator));

        tasks.sort(taskComparator);
    }

    /**
     * Returns the tasks in their current order.
     *
     * @return an unmodifiable copy of the tasks
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }
}
