package waffles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Owns the ordered collection of tasks and its basic list operations.
 */
public class TaskList {
    /** The tasks in the order in which they should be displayed. */
    private final ArrayList<Task> tasks;

    /**
     * Creates a task list containing a copy of the supplied tasks.
     *
     * @param initialTasks tasks loaded from storage
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /** Creates an empty task list. */
    public TaskList() {
        this(Collections.emptyList());
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add
     */
    public void addTask(Task task) {
        tasks.add(Objects.requireNonNull(task));
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the zero-based index
     * @return the task at that index
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based index
     * @return the removed task
     */
    public Task removeTask(int index) {
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
     * Returns a read-only view for storage and display.
     *
     * @return the current tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Finds tasks whose descriptions contain the supplied keyword.
     * The search ignores letter case and preserves task-list order.
     *
     * @param keyword the text to search for
     * @return matching tasks in their original order
     */
    public List<Task> findTasks(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /**
     * Finds scheduled tasks that overlap with the supplied candidate.
     *
     * <p>Events occupy an inclusive date range. Deadlines occupy their due date.
     * Todo tasks have no scheduled date and therefore cannot clash.</p>
     *
     * @param candidate the task that is about to be added
     * @return scheduled tasks that overlap with the candidate
     */
    public List<Task> findSchedulingConflicts(Task candidate) {
        return tasks.stream()
                .filter(existingTask -> isSchedulingConflict(candidate, existingTask))
                .toList();
    }

    /** Determines whether two scheduled tasks occupy at least one common date. */
    private boolean isSchedulingConflict(Task first, Task second) {
        if (first instanceof Event firstEvent && second instanceof Event secondEvent) {
            return rangesOverlap(firstEvent, secondEvent);
        }
        if (first instanceof Event event && second instanceof Deadline deadline) {
            return isWithinEvent(deadline.getBy(), event);
        }
        if (first instanceof Deadline deadline && second instanceof Event event) {
            return isWithinEvent(deadline.getBy(), event);
        }
        return first instanceof Deadline firstDeadline
                && second instanceof Deadline secondDeadline
                && firstDeadline.getBy().equals(secondDeadline.getBy());
    }

    /** Returns whether two inclusive event ranges overlap. */
    private boolean rangesOverlap(Event first, Event second) {
        return !first.getTo().isBefore(second.getFrom())
                && !second.getTo().isBefore(first.getFrom());
    }

    /** Returns whether a date falls within an inclusive event range. */
    private boolean isWithinEvent(LocalDate date, Event event) {
        return !date.isBefore(event.getFrom()) && !date.isAfter(event.getTo());
    }
}
