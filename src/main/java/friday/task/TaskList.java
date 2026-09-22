package friday.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Owns and provides operations on Friday's collection of tasks.
 */
public class TaskList implements Iterable<Task> {
    private static final Comparator<Task> ALPHABETICAL_ORDER = Comparator.comparing(
            Task::getDescription, String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<Task> TASK_TYPE_ORDER = Comparator.comparing(Task::getType);

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing tasks loaded from storage.
     *
     * @param tasks Initial tasks copied into the new list.
     * @throws IllegalArgumentException If tasks is null or contains null or duplicate tasks.
     */
    public TaskList(List<Task> tasks) {
        if (tasks == null) {
            throw new IllegalArgumentException("Initial task list cannot be null.");
        }
        this.tasks = new ArrayList<>();
        for (Task task : tasks) {
            add(task);
        }
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     * @throws IllegalArgumentException If task is null or its details duplicate an existing task.
     */
    public void add(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (tasks.stream().anyMatch(existing -> existing.hasSameDetails(task))) {
            throw new IllegalArgumentException("This task already exists.");
        }
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to remove.
     * @return Removed task.
     * @throws IndexOutOfBoundsException If index is outside the list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index Zero-based index of the task to return.
     * @return Task at the specified index.
     * @throws IndexOutOfBoundsException If index is outside the list.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the list contains no tasks.
     *
     * @return {@code true} if the list is empty; otherwise {@code false}.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns all tasks whose descriptions contain the keyword, in list order.
     *
     * @param keyword text to search for
     * @return matching tasks
     */
    public List<Task> find(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Search keyword cannot be empty.");
        }
        return tasks.stream()
                .filter(task -> task.descriptionContains(keyword))
                .toList();
    }

    /**
     * Sorts the tasks alphabetically by description, ignoring letter case.
     */
    public void sortAlphabetically() {
        tasks.sort(ALPHABETICAL_ORDER);
    }

    /**
     * Sorts the tasks in reverse alphabetical order by description, ignoring letter case.
     */
    public void sortReverseAlphabetically() {
        tasks.sort(ALPHABETICAL_ORDER.reversed());
    }

    /**
     * Groups tasks by type in Deadline, Event, and Todo order.
     */
    public void sortByType() {
        tasks.sort(TASK_TYPE_ORDER);
    }

    /**
     * Returns an iterator that does not support modifying the task list.
     *
     * @return Read-only iterator over the tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
