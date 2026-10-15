package whimsybot.task;

import java.util.List;

/** Stores and manages the tasks currently known to Whimsy Bot. */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final Task[] tasks;
    private int taskCount;

    /** Creates a task list containing the supplied saved tasks, up to its capacity.
     *
     * @param savedTasks tasks loaded from storage
     */
    public TaskList(List<Task> savedTasks) {
        // Storage is expected to provide a non-null collection of tasks.
        assert savedTasks != null : "Saved tasks must not be null";
        tasks = new Task[MAX_TASKS];
        taskCount = Math.min(savedTasks.size(), MAX_TASKS);
        for (int i = 0; i < taskCount; i++) {
            tasks[i] = savedTasks.get(i);
            // Every occupied slot must contain a task so later operations can use it safely.
            assert tasks[i] != null : "An occupied task slot must not be null";
        }
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return taskCount;
    }

    /** Returns whether another task can be added to this list. */
    public boolean canAdd() {
        return taskCount < MAX_TASKS;
    }

    /** Returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the task at the specified index
     */
    public Task get(int index) {
        // Callers validate task numbers before converting them to zero-based indices.
        assert isValidIndex(index) : "Task index must refer to an existing task";
        return tasks[index];
    }

    /** Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        // The command layer checks capacity before adding, so the next slot must exist.
        assert canAdd() : "A task cannot be added to a full task list";
        assert task != null : "A task list must not contain null tasks";
        tasks[taskCount] = task;
        taskCount++;
    }

    /** Removes and returns the task at a zero-based index.
     *
     * @param index the zero-based task index
     * @return the removed task
     */
    public Task delete(int index) {
        // Deletion is only requested after the command layer validates the task number.
        assert isValidIndex(index) : "Task index must refer to an existing task";
        Task removedTask = tasks[index];
        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }
        tasks[taskCount - 1] = null;
        taskCount--;
        return removedTask;
    }

    /** Marks the task at the given zero-based index as done.
     *
     * @param index the zero-based task index
     */
    public void mark(int index) {
        // A valid index implies that the referenced slot is occupied.
        assert isValidIndex(index) : "Task index must refer to an existing task";
        tasks[index].markAsDone();
    }

    /** Marks the task at the given zero-based index as not done.
     *
     * @param index the zero-based task index
     */
    public void unmark(int index) {
        // A valid index implies that the referenced slot is occupied.
        assert isValidIndex(index) : "Task index must refer to an existing task";
        tasks[index].unmarkAsDone();
    }

    /** Returns the tasks as an array and count pair for storage. */
    public Task[] toArray() {
        return tasks;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < taskCount && tasks[index] != null;
    }
}
