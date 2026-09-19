package nudge.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nudge.exception.NudgeException;

/**
 * Stores tasks and provides operations for managing them.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the specified tasks.
     *
     * @param tasks initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        assert hasNoNullTasks(tasks) : "Initial task list should not contain null tasks";
        this.tasks = new ArrayList<>(tasks);
    }

    private static boolean hasNoNullTasks(List<Task> tasks) {
        for (Task task : tasks) {
            if (task == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        tasks.add(task);
    }

    /**
     * Inserts a task at the specified position.
     *
     * @param taskIndex zero-based position at which to insert the task.
     * @param task task to insert.
     */
    public void add(int taskIndex, Task task) {
        assert task != null : "Task to insert should not be null";
        tasks.add(taskIndex, task);
    }

    /**
     * Deletes and returns the task at the specified position.
     *
     * @param taskIndex zero-based index of the task to delete.
     * @return deleted task.
     * @throws NudgeException if the index is outside the task list.
     */
    public Task delete(int taskIndex) throws NudgeException {
        validateIndex(taskIndex);
        return tasks.remove(taskIndex);
    }

    /**
     * Returns the task at the specified position.
     *
     * @param taskIndex zero-based index of the task to return.
     * @return task at the specified position.
     * @throws NudgeException if the index is outside the task list.
     */
    public Task get(int taskIndex) throws NudgeException {
        validateIndex(taskIndex);
        return tasks.get(taskIndex);
    }

    /**
     * Marks the task at the specified position as done.
     *
     * @param taskIndex zero-based index of the task to mark.
     * @throws NudgeException if the index is outside the task list.
     */
    public void mark(int taskIndex) throws NudgeException {
        get(taskIndex).markAsDone();
    }

    /**
     * Marks the task at the specified position as not done.
     *
     * @param taskIndex zero-based index of the task to unmark.
     * @throws NudgeException if the index is outside the task list.
     */
    public void unmark(int taskIndex) throws NudgeException {
        get(taskIndex).markAsNotDone();
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return number of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns a read-only view of the stored tasks.
     *
     * @return unmodifiable task list view.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword keyword to find in task descriptions.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Sorts deadlines by ascending due date while leaving other task types in place.
     * Deadlines with the same due date retain their relative order.
     */
    public void sortDeadlinesByDate() {
        ArrayList<Deadline> sortedDeadlines = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Deadline deadline) {
                sortedDeadlines.add(deadline);
            }
        }
        sortedDeadlines.sort(Comparator.comparing(Deadline::getDueDate));

        int sortedDeadlineIndex = 0;
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            if (tasks.get(taskIndex) instanceof Deadline) {
                tasks.set(taskIndex, sortedDeadlines.get(sortedDeadlineIndex));
                sortedDeadlineIndex++;
            }
        }
    }

    /**
     * Ensures that an index identifies an existing task.
     *
     * @param taskIndex zero-based index to validate.
     * @throws NudgeException if the index is outside the task list.
     */
    private void validateIndex(int taskIndex) throws NudgeException {
        if (tasks.isEmpty()) {
            throw new NudgeException("There are no tasks in your list yet.");
        }
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            String validRangeMessage = tasks.size() == 1
                    ? "Choose task number 1."
                    : "Choose a task number from 1 to " + tasks.size() + ".";
            throw new NudgeException(validRangeMessage);
        }
    }
}
