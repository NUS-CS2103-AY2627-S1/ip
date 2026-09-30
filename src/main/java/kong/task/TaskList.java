package kong.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Holds the user's tasks and provides operations on the task collection.
 */
public class TaskList {
    /** Orders dated tasks by date before undated tasks, while treating equal dates as equal. */
    private static final Comparator<Task> CHRONOLOGICAL_COMPARATOR =
            Comparator.comparing((Task task) -> task.getSortDate().isEmpty())
                    .thenComparing(task -> task.getSortDate().orElse(LocalDate.MIN));

    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list backed by the supplied collection, ignoring duplicate tasks.
     *
     * @param tasks non-null initial task collection
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "A task list must be initialized with a collection";
        assert !tasks.contains(null) : "A task list cannot be initialized with null tasks";
        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!hasDuplicate(task)) {
                this.tasks.add(task);
            }
        }
        this.tasks.sort(CHRONOLOGICAL_COMPARATOR);
    }

    /**
     * Adds a task in chronological order, with undated tasks placed last.
     * Tasks with the same date retain their insertion order.
     *
     * @param task non-null task to add
     */
    public void add(Task task) {
        assert task != null : "A task list cannot contain null tasks";
        int insertionIndex = 0;
        while (insertionIndex < tasks.size()
                && CHRONOLOGICAL_COMPARATOR.compare(tasks.get(insertionIndex), task) <= 0) {
            insertionIndex++;
        }
        tasks.add(insertionIndex, task);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return task at the index
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Indicates whether the list contains no tasks.
     *
     * @return {@code true} when the list is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the underlying task collection for persistence.
     *
     * @return current tasks
     */
    public ArrayList<Task> asList() {
        return tasks;
    }

    /**
     * Returns all tasks that happen on the given date.
     *
     * @param date date to query
     * @return matching deadlines and events in list order
     */
    public ArrayList<Task> getTasksOnDate(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     */
    public ArrayList<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Checks whether an identical task already exists in the task list.
     *
     * @param task task to check
     * @return {@code true} if a task with the same details exists
     */
    public boolean hasDuplicate(Task task) {
        assert task != null : "Cannot check for duplicate of null task";
        return tasks.stream().anyMatch(existing -> existing.isSameTask(task));
    }
}
