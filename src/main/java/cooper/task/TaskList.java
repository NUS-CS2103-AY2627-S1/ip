package cooper.task;

import java.util.ArrayList;
import java.util.List;

import cooper.exception.CooperException;

/**
 * Owns the application's task collection and its list operations.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /** Creates a task list containing tasks previously loaded from storage. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a one-based task number.
     *
     * @throws CooperException If the task number is outside the list.
     */
    public Task get(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at a one-based task number.
     *
     * @throws CooperException If the task number is outside the list.
     */
    public Task delete(int taskNumber) {
        validateTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /** Returns an immutable snapshot suitable for display or saving. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }

    /**
     * Returns a snapshot of tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching tasks in their original list order.
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.containsKeyword(keyword))
                .toList();
    }

    /** Ensures a one-based task number refers to a task currently in the list. */
    private void validateTaskNumber(int taskNumber) {
        if (taskNumber <= 0 || taskNumber > tasks.size()) {
            throw new CooperException("I couldn't find a task with that index :(");
        }
    }
}
