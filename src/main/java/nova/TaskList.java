package nova;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nova.task.Task;

/**
 * The list of tasks the user is keeping track of.
 *
 * <p>This wraps a plain {@code ArrayList} so that operations on the list —
 * adding, removing, and searching by date — live next to the data instead of
 * being spread through the command handling.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, e.g. the ones just loaded
     * from the save file.
     *
     * @param tasks the initial tasks; copied, so the caller's list is not aliased
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds one or more tasks to the end of the list, in the order given.
     *
     * @param tasksToAdd the tasks to add
     */
    public void add(Task... tasksToAdd) {
        for (Task task : tasksToAdd) {
            assert task != null : "a null task would break every later read of the list";
        }
        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index zero-based position of the task
     * @return the task that was removed
     */
    public Task remove(int index) {
        assert isInRange(index) : "index " + index + " out of range for a list of " + tasks.size();
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index zero-based position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        assert isInRange(index) : "index " + index + " out of range for a list of " + tasks.size();
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks falling on the given date.
     *
     * @param date the date to search for
     * @return the matching tasks, in list order
     */
    public List<Task> getTasksOn(LocalDate date) {
        return tasks.stream()
                .filter(task -> task.isOn(date))
                .toList();
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text to search for
     * @return the matching tasks, in list order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Reorders the list chronologically, soonest first.
     *
     * <p>Undated todos have nothing to compare, so they sort after everything
     * dated rather than being dropped or treated as due now. Tasks sharing a
     * moment, and todos among themselves, fall back to their description so
     * that sorting the same list twice always gives the same order.
     */
    public void sort() {
        tasks.sort(Comparator
                .comparing((Task task) -> task.getScheduledTime().isPresent() ? 0 : 1)
                .thenComparing(task -> task.getScheduledTime().orElse(LocalDateTime.MAX))
                .thenComparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Checks that the given position addresses a task that actually exists.
     *
     * @param index zero-based position the user asked for
     * @throws NovaException if there is no task at that position
     */
    public void checkIndexInRange(int index) throws NovaException {
        if (!isInRange(index)) {
            throw new NovaException("There is no task " + (index + 1) + ". Your list has "
                    + tasks.size() + ".");
        }
    }

    /**
     * Returns the tasks as an unmodifiable list, for display and for saving.
     *
     * @return a read-only view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /** Returns whether the given index addresses a task that exists. */
    private boolean isInRange(int index) {
        return index >= 0 && index < tasks.size();
    }
}
