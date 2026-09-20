package duchess.task;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/** Owns Duchess's collection of tasks and the operations performed on it. */
public class TaskList {
    /** The tasks currently stored by Duchess. */
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing the supplied tasks.
     *
     * <p>The collection is copied so callers cannot bypass this class's
     * operations by retaining a reference to the original list.</p>
     *
     * @param initialTasks the initial tasks, which may be omitted
     */
    public TaskList(Task... initialTasks) {
        assert initialTasks != null : "A task list requires a non-null initial task array";
        tasks = new ArrayList<>(Arrays.asList(initialTasks));
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Returns whether the list already contains a task with the same type and details.
     *
     * <p>Letter case and repeated whitespace are ignored so superficial input
     * differences cannot create duplicate records.</p>
     *
     * @param candidate the task whose details should be compared
     * @return {@code true} if an equivalent task is already present
     */
    public boolean containsEquivalent(Task candidate) {
        assert candidate != null : "A duplicate check requires a task";
        return tasks.stream().anyMatch(task -> hasSameDetails(task, candidate));
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the specified index
     */
    public Task get(int index) {
        assertValidIndex(index);
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index the zero-based task index
     */
    public void markAsDone(int index) {
        markAsDone(index, Instant.now());
    }

    /**
     * Marks the task at a zero-based index as done at a specified instant.
     *
     * @param index the zero-based task index
     * @param completionTime the completion instant
     */
    public void markAsDone(int index, Instant completionTime) {
        assertValidIndex(index);
        assert completionTime != null : "A completed task must have a completion instant";
        tasks.get(index).markAsDone(completionTime);
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index the zero-based task index
     */
    public void markAsNotDone(int index) {
        assertValidIndex(index);
        tasks.get(index).markAsNotDone();
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the current number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain a keyword, ignoring letter case.
     *
     * @param keyword the text to search for
     * @return matching tasks in their original list order
     */
    public ArrayList<Task> find(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(lowerCaseKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns statistics for the tasks at a specified instant.
     *
     * @param now the instant used as the end of the seven-day period
     * @return statistics for the current task list
     */
    public TaskStatistics getStatistics(Instant now) {
        assert now != null : "Statistics require a reference instant";
        Instant periodStart = now.minus(7, ChronoUnit.DAYS);
        int completedTasks = (int) tasks.stream()
                .filter(Task::isDone)
                .count();
        int recentlyCompletedTasks = (int) tasks.stream()
                .filter(task -> isCompletedWithin(task, periodStart, now))
                .count();
        return new TaskStatistics(tasks.size(), completedTasks, recentlyCompletedTasks);
    }

    /** Returns whether a task was completed within an inclusive time interval. */
    private boolean isCompletedWithin(Task task, Instant periodStart, Instant periodEnd) {
        Instant completedAt = task.getCompletedAt();
        return task.isDone() && completedAt != null
                && !completedAt.isBefore(periodStart)
                && !completedAt.isAfter(periodEnd);
    }

    /** Returns whether two tasks have the same user-entered details. */
    private boolean hasSameDetails(Task first, Task second) {
        if (first.getType() != second.getType()
                || !normalizeText(first.getDescription()).equals(normalizeText(second.getDescription()))) {
            return false;
        }
        if (first instanceof Deadline firstDeadline && second instanceof Deadline secondDeadline) {
            return firstDeadline.getBy().equals(secondDeadline.getBy());
        }
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            if (firstEvent.hasDateRange() != secondEvent.hasDateRange()) {
                return false;
            }
            if (firstEvent.hasDateRange()) {
                return firstEvent.getFrom().equals(secondEvent.getFrom())
                        && firstEvent.getTo().equals(secondEvent.getTo());
            }
            return normalizeText(firstEvent.getAt()).equals(normalizeText(secondEvent.getAt()));
        }
        return true;
    }

    /** Normalizes human-entered text for duplicate comparisons. */
    private String normalizeText(String value) {
        return value.strip().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /** Documents the index precondition shared by operations on one task. */
    private void assertValidIndex(int index) {
        assert index >= 0 && index < tasks.size()
                : "A task index must refer to an existing task";
    }
}
