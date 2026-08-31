import java.util.ArrayList;
import java.util.List;

/**
 * Stores the tasks managed by Bibi and provides safe indexed access to them.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to store
     */
    public void add(Task task) {
        tasks.add(task);
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
     * Reports whether this list contains no tasks.
     *
     * @return {@code true} when there are no stored tasks
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the matching task
     * @throws BibiException if the task number does not exist
     */
    public Task get(int taskNumber) throws BibiException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns a task using the one-based number displayed to the user.
     *
     * @param taskNumber one-based task number
     * @return the removed task
     * @throws BibiException if the task number does not exist
     */
    public Task remove(int taskNumber) throws BibiException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Converts and validates a displayed one-based task number.
     *
     * @param taskNumber one-based task number
     * @return the matching zero-based list index
     * @throws BibiException if the task number does not exist
     */
    private int toIndex(int taskNumber) throws BibiException {
        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new BibiException("That task number does not exist.");
        }
        return taskIndex;
    }
}
