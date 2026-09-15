package choo.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import choo.exception.ChooException;

/**
 * Owns the task collection and its one-based position operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(Task... tasks) {
        this(Arrays.asList(tasks));
    }

    /**
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Source task list must not be null";
        this.tasks = new ArrayList<>(tasks);
        assert !this.tasks.contains(null) : "Task list must not contain null tasks";
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add must not be null";
        this.tasks.add(task);
    }

    /**
     * Inserts a task at a one-based position.
     *
     * @param taskNumber one-based insertion position.
     * @param task task to insert.
     * @throws ChooException if the insertion position is outside the list.
     */
    public void add(int taskNumber, Task task) throws ChooException {
        assert task != null : "Task to insert must not be null";
        if (taskNumber < 1 || taskNumber > this.tasks.size() + 1) {
            throw invalidPosition(taskNumber);
        }
        this.tasks.add(taskNumber - 1, task);
    }

    /**
     * Returns the task at a one-based position.
     *
     * @param taskNumber one-based task number.
     * @return selected task.
     * @throws ChooException if the task number is outside the list.
     */
    public Task get(int taskNumber) throws ChooException {
        validateTaskNumber(taskNumber);
        return this.tasks.get(taskNumber - 1);
    }

    /**
     * Removes and returns the task at a one-based position.
     *
     * @param taskNumber one-based task number.
     * @return removed task.
     * @throws ChooException if the task number is outside the list.
     */
    public Task remove(int taskNumber) throws ChooException {
        validateTaskNumber(taskNumber);
        return this.tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the number of tasks.
     *
     * @return task count.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns an unmodifiable snapshot of the tasks.
     *
     * @return task snapshot.
     */
    public List<Task> asList() {
        return List.copyOf(this.tasks);
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     *
     * @param keyword Case-sensitive keyword to search for.
     * @return Matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Returns a new task list with deadlines ordered chronologically first.
     *
     * <p>Tasks without a deadline retain their relative order after all deadlines.
     * Deadlines with the same due date and time also retain their relative order.</p>
     *
     * @return New task list sorted by deadline.
     */
    public TaskList sortedByDeadline() {
        List<Task> sortedTasks = this.tasks.stream()
                .sorted(Comparator.comparing(TaskList::getDeadlineSortKey))
                .toList();
        return new TaskList(sortedTasks);
    }

    private static LocalDateTime getDeadlineSortKey(Task task) {
        return task instanceof Deadline deadline
                ? deadline.getDueDateTime() : LocalDateTime.MAX;
    }

    /**
     * Validates that a one-based task number identifies an existing task.
     *
     * @param taskNumber One-based task number to validate.
     * @throws ChooException If the task number is outside the list.
     */
    private void validateTaskNumber(int taskNumber) throws ChooException {
        if (taskNumber < 1 || taskNumber > this.tasks.size()) {
            throw invalidPosition(taskNumber);
        }
    }

    private static ChooException invalidPosition(int taskNumber) {
        return new ChooException("Task number " + taskNumber + " is outside the list.");
    }
}
