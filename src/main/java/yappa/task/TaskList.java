package yappa.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import yappa.exception.YappaException;

/**
 * Stores tasks and provides operations for managing them.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing a defensive copy of the loaded tasks.
     *
     * @param tasks Tasks to copy into the list.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
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
     * Marks the specified task as complete.
     *
     * @param taskIndex Zero-based task index.
     * @return Task that was marked.
     * @throws YappaException If the index does not identify a task.
     */
    public Task mark(int taskIndex) throws YappaException {
        validateIndex(taskIndex);
        Task markedTask = tasks.get(taskIndex);
        markedTask.markAsDone();
        return markedTask;
    }

    /**
     * Unmarks the specified task as complete.
     *
     * @param taskIndex Zero-based task index.
     * @return Task that was unmarked.
     * @throws YappaException If the index does not identify a task.
     */
    public Task unmark(int taskIndex) throws YappaException {
        validateIndex(taskIndex);
        Task unmarkedTask = tasks.get(taskIndex);
        unmarkedTask.markAsUndone();
        return unmarkedTask;
    }

    /**
     * Appends a task to the task list after validating that it is non-null and not
     * a duplicate.
     *
     * @param task Task instance to be added to the list.
     * @throws NullPointerException If the provided task is {@code null}.
     * @throws YappaException       If an identical task already exists in the list.
     */
    public void add(Task task) throws YappaException {
        Objects.requireNonNull(task, "Task cannot be null.");

        if (containsDuplicate(task)) {
            throw new YappaException(
                    "This task already exists.");
        }

        tasks.add(task);
    }

    /**
     * Removes and returns the task.
     *
     * @param taskIndex Zero-based task index.
     * @return Removed task.
     * @throws YappaException If the index does not identify a task.
     */
    public Task remove(int taskIndex) throws YappaException {
        validateIndex(taskIndex);
        return tasks.remove(taskIndex);
    }

    /**
     * Finds tasks whose descriptions contain the specified search query.
     *
     * @param searchQuery Keyword or text to search for in task descriptions.
     * @return A list of tasks with descriptions containing the search query.
     */
    public TaskList find(String searchQuery) {
        return new TaskList(
                tasks.stream()
                        .filter(task -> task.getDescription().contains(searchQuery))
                        .toList());
    }

    /**
     * Ensures that an index identifies an existing task.
     *
     * @param index Zero-based task index.
     * @throws YappaException If the index is outside the task list.
     */
    private void validateIndex(int index) throws YappaException {
        if (index < 0 || index >= tasks.size()) {
            throw new YappaException("Task number " + (index + 1) + " does not exist!");
        }
    }

    /**
     * Checks whether a task with the same type and case-insensitive description
     * already exists in the list.
     *
     * @param newTask Task instance to check for duplicates against existing tasks.
     * @return {@code true} if an identical task type with matching description
     *         exists, {@code false} otherwise.
     */
    public boolean containsDuplicate(Task newTask) {
        return tasks.contains(newTask);
    }

    /**
     * Clears all tasks from the task list.
     */
    public void clear() {
        tasks.clear();
    }

    /**
     * Returns an iterator over tasks in display order.
     *
     * @return Task iterator.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /**
     * Sorts tasks alphabetically by description, ignoring case.
     */
    public void sort(SortField field, SortOrder order) {
        Comparator<Task> comparator;

        if (field == SortField.DESCRIPTION) {
            comparator = Comparator.comparing(
                    Task::getDescription,
                    String.CASE_INSENSITIVE_ORDER);
        } else {
            Comparator<LocalDateTime> dateComparator = order == SortOrder.ASCENDING
                    ? Comparator.naturalOrder()
                    : Comparator.reverseOrder();

            comparator = Comparator.comparing(
                    task -> task.getDateTime().orElse(null),
                    Comparator.nullsLast(dateComparator));
        }

        if (field == SortField.DESCRIPTION
                && order == SortOrder.DESCENDING) {
            comparator = comparator.reversed();
        }

        tasks.sort(comparator);
    }

    /**
     * Returns a string representation of the task list.
     *
     * @return Display representation of the task list.
     */
    @Override
    public String toString() {
        if (tasks.isEmpty()) {
            return "No tasks";
        }

        StringBuilder taskList = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            taskList
                    .append("\t")
                    .append(i + 1)
                    .append(".")
                    .append(tasks.get(i))
                    .append(System.lineSeparator());
        }

        return taskList.toString();
    }
}
