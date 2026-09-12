package alfred.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores and manages Alfred's ordered collection of tasks.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param tasks tasks with which to initialize the task list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "Task lists must not contain null tasks";
        tasks.add(task);
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int taskIndex) {
        assert isValidIndex(taskIndex) : "Commands must use parser-validated task indices";
        return tasks.get(taskIndex);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int taskIndex) {
        assert isValidIndex(taskIndex) : "Commands must use parser-validated task indices";
        return tasks.remove(taskIndex);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list already contains a task with the same identifying details.
     *
     * @param task task to look for
     * @return whether an equivalent task is already in the list
     */
    public boolean containsEquivalentTask(Task task) {
        assert task != null : "Duplicate checks require a task";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameIdentityAs(task));
    }

    /**
     * Returns tasks whose descriptions contain the keyword, ignoring letter case.
     *
     * @param keyword text to look for in task descriptions
     * @return matching tasks in their original list order
     */
    public List<Task> findTasks(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns a stable chronological view of the tasks without changing their stored order.
     *
     * @param isAscending whether the earliest dated tasks appear first
     * @return dated tasks ordered chronologically, with to-dos on the specified end of the result
     */
    public List<Task> getTasksSortedChronologically(boolean isAscending) {
        Comparator<LocalDateTime> dateTimeComparator = isAscending
                ? Comparator.naturalOrder()
                : Comparator.reverseOrder();
        Comparator<LocalDateTime> nullableDateTimeComparator = isAscending
                ? Comparator.nullsLast(dateTimeComparator)
                : Comparator.nullsFirst(dateTimeComparator);

        return tasks.stream()
                .sorted(Comparator.comparing(this::getChronologicalDateTime, nullableDateTimeComparator))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Returns the date and time used to order a dated task, or {@code null} for a to-do. */
    private LocalDateTime getChronologicalDateTime(Task task) {
        assert task != null : "Task lists must not contain null tasks";
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        assert task instanceof Todo : "Every task must have a supported chronological ordering";
        return null;
    }

    /** Returns an iterator over the tasks in their list order. */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /** Returns whether the index identifies an existing task. */
    private boolean isValidIndex(int taskIndex) {
        return taskIndex >= 0 && taskIndex < tasks.size();
    }
}
