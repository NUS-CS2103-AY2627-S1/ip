package zsiggy;

import java.util.Arrays;

import zsiggy.task.Task;

/**
 * Stores and manages the tasks in Zsiggy.
 */
public class TaskList {
    private Task[] tasks;
    private int taskCount;

    /**
     * Creates an empty task list with an initial capacity of 100 tasks.
     */
    public TaskList() {
        this.tasks = new Task[100];
        this.taskCount = 0;
    }

    /**
     * Adds a task to the task list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        if (taskCount == tasks.length) {
            tasks = Arrays.copyOf(tasks, tasks.length * 2);
        }

        tasks[taskCount] = task;
        taskCount++;
    }

    /**
     * Deletes the task at the specified index.
     *
     * @param index the zero-based index of the task to delete
     * @return the task that was deleted
     */
    public Task delete(int index) {
        assert isValidIndex(index) : "Index to delete should be valid";

        Task deletedTask = tasks[index];

        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }

        taskCount--;
        tasks[taskCount] = null;

        return deletedTask;
    }

    /**
     * Marks the task at the specified index as done.
     *
     * @param index the zero-based index of the task
     */
    public void mark(int index) {
        assert isValidIndex(index) : "Index to mark should be valid";

        tasks[index].mark();
    }

    /**
     * Marks the task at the specified index as not done.
     *
     * @param index the zero-based index of the task
     */
    public void unmark(int index) {
        assert isValidIndex(index) : "Index to unmark should be valid";

        tasks[index].unmark();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index the zero-based index of the task
     * @return the task at the given index
     */
    public Task get(int index) {
        assert isValidIndex(index) : "Index to get should be valid";

        return tasks[index];
    }

    /**
     * Returns the array containing the tasks.
     *
     * @return the task array
     */
    public Task[] getTasks() {
        return tasks;
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return the number of tasks
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Checks whether the given index refers to an existing task.
     *
     * @param index the zero-based index to check
     * @return true if the index is valid, false otherwise
     */
    public boolean isValidIndex(int index) {
        return index >= 0 && index < taskCount;
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword The keyword to search for.
     * @return An array containing the matching tasks.
     */
    public Task[] find(String keyword) {
        return Arrays.stream(tasks, 0, taskCount)
                .filter(task -> task.getDescription().contains(keyword))
                .toArray(Task[]::new);
    }

    /**
     * Copies tasks and completion states so a failed save can be rolled back.
     */
    public TaskList copy() {
        TaskList copy = new TaskList();
        for (int i = 0; i < taskCount; i++) {
            copy.add(tasks[i].copy());
        }
        return copy;
    }
}
