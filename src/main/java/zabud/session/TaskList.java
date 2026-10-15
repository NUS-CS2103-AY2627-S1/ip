package zabud.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import zabud.tasks.Task;

/**
 * Stores the tasks entered during one Zabud session.
 */
public class TaskList {
    /**
     * The maximum number of tasks in one session.
     */
    private static final int MAX_TASKS = 100;

    /**
     * Tasks stored in entry order.
     */
    private final Task[] tasks = new Task[MAX_TASKS];

    /**
     * Number of occupied elements in {@link #tasks}.
     */
    private int taskCount;

    /**
     * Called after a mutation so the owning session can persist the list.
     */
    private Runnable changeListener = () -> { };

    /**
     * Creates an empty task list.
     */
    public TaskList() {
    }

    /**
     * Sets the callback invoked after a task list mutation.
     *
     * @param listener callback to invoke, or {@code null} to disable callbacks.
     */
    public void setChangeListener(Runnable listener) {
        changeListener = listener == null ? () -> { } : listener;
    }

    /**
     * Adds a task while restoring persisted state without triggering a save.
     *
     * @param task task being restored.
     * @return whether the task was added.
     */
    public boolean addLoaded(Task task) {
        return addInternal(task, false);
    }

    /**
     * Adds a task when there is remaining capacity.
     *
     * @param task the task to add.
     * @return whether the task was added.
     */
    public boolean add(Task task) {
        return addInternal(task, true);
    }

    private boolean addInternal(Task task, boolean shouldNotify) {
        if (taskCount == MAX_TASKS) {
            return false;
        }
        tasks[taskCount++] = task;
        if (shouldNotify) {
            changeListener.run();
        }
        return true;
    }

    /**
     * Returns the task at a one-based number, or {@code null} when invalid.
     *
     * @param taskNumber the one-based task number.
     * @return the requested task, or {@code null} if it does not exist.
     */
    public Task get(int taskNumber) {
        if (taskNumber < 1 || taskNumber > taskCount) {
            return null;
        }
        return tasks[taskNumber - 1];
    }

    /**
     * Removes and returns the task at a one-based number.
     * Tasks after the removed task are shifted forward to preserve their order.
     *
     * @param taskNumber the one-based task number.
     * @return the removed task, or {@code null} if the number is invalid.
     */
    public Task remove(int taskNumber) {
        Task task = get(taskNumber);
        if (task == null) {
            return null;
        }

        int taskIndex = taskNumber - 1;
        for (int i = taskIndex; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[--taskCount] = null;
        changeListener.run();
        return task;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of tasks.
     */
    public int size() {
        return taskCount;
    }

    /**
     * Returns tasks whose descriptions contain the supplied text.
     * Matching is case-insensitive, supports partial words, and preserves entry order.
     *
     * @param keyword text to search for in each task description.
     * @return matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Task> matchingTasks = new ArrayList<>();
        for (int index = 0; index < taskCount; index++) {
            if (tasks[index].getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matchingTasks.add(tasks[index]);
            }
        }
        return List.copyOf(matchingTasks);
    }

    /**
     * Prints all tasks in their entry order.
     */
    public void printTasks() {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < taskCount; i++) {
            System.out.println(" " + (i + 1) + "." + tasks[i]);
        }
    }
}
