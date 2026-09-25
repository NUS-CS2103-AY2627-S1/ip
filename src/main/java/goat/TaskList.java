package goat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Owns the application's task collection and task-index operations.
 */
public class TaskList {
    private static final Comparator<Task> CHRONOLOGICAL_ORDER = Comparator
            .comparing((Task task) -> task.getSchedulingDate().orElse(LocalDate.MAX))
            .thenComparing(task -> task.description, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(task -> task.description);

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks initial tasks copied into the new list
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the current number of tasks.
     *
     * @return number of tasks
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index
     * @return task at the index
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "Task index should be within the list";
        return tasks.get(index);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Inserts a task at a zero-based index.
     *
     * @param index zero-based insertion index
     * @param task task to insert
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns a task at a zero-based index.
     *
     * @param index zero-based task index
     * @return removed task
     */
    public Task delete(int index) {
        assert index >= 0 && index < tasks.size() : "Task index should be within the list";
        return tasks.remove(index);
    }

    /**
     * Returns an immutable snapshot for display or storage.
     *
     * @return immutable task snapshot
     */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword.
     *
     * @param keyword case-sensitive text to match
     * @return matching tasks in their original order
     */
    public TaskList find(String keyword) {
        List<Task> matches = tasks.stream()
                .filter(task -> task.description.contains(keyword))
                .toList();
        return new TaskList(matches);
    }

    /**
     * Returns a new task list ordered by scheduling date and then description.
     * Dated tasks come first; undated tasks follow in alphabetical order.
     *
     * @return sorted task-list copy
     */
    public TaskList sortedChronologically() {
        return new TaskList(tasks.stream().sorted(CHRONOLOGICAL_ORDER).toList());
    }

    /**
     * Replaces this list with a snapshot of another task list.
     *
     * @param replacement task list whose order and contents should be adopted
     */
    public void replaceWith(TaskList replacement) {
        assert replacement != null : "Replacement task list should not be null";
        tasks.clear();
        tasks.addAll(replacement.asList());
    }

    /**
     * Parses and validates a one-based task number entered by the user.
     *
     * @param argument task-number text
     * @param commandWord command used in error guidance
     * @return corresponding zero-based index
     * @throws GoatException if the task number is missing, invalid, or out of range
     */
    public int parseIndex(String argument, String commandWord) throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException("Please specify a task number. Try: " + commandWord
                    + " <task number>");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new GoatException("The task number must be a positive whole number.");
        }

        if (tasks.isEmpty()) {
            throw new GoatException("There are no tasks in the list yet.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new GoatException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + tasks.size() + ".");
        }
        return taskNumber - 1;
    }
}
