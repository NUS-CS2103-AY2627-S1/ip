package gary.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the tasks in the chatbot.
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
     * Creates a task list containing the given tasks.
     *
     * @param tasks Initial tasks to store.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "Initial task list should not be null";
        assert tasks.stream().noneMatch(task -> task == null) : "Initial task list should not contain null tasks";

        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given one-based position.
     *
     * @param taskNumber One-based task number.
     * @return Task at the requested position.
     */
    public Task getTask(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "Task number should refer to an existing task";

        return tasks.get(taskNumber - 1);
    }

    /**
     * Returns an immutable snapshot of all tasks.
     *
     * @return Snapshot of the stored tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";

        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the given one-based position.
     *
     * @param taskNumber One-based task number.
     * @return Deleted task.
     */
    public Task delete(int taskNumber) {
        assert isValidTaskNumber(taskNumber) : "Task number should refer to an existing task";

        return tasks.remove(taskNumber - 1);
    }

    /**
     * Marks and returns the task at the given one-based position as done.
     *
     * @param taskNumber One-based task number.
     * @return Task that was marked as done.
     */
    public Task markAsDone(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsDone();
        return task;
    }

    /**
     * Marks and returns the task at the given one-based position as not done.
     *
     * @param taskNumber One-based task number.
     * @return Task that was marked as not done.
     */
    public Task markAsNotDone(int taskNumber) {
        Task task = getTask(taskNumber);
        task.markAsNotDone();
        return task;
    }

    private boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }
}
