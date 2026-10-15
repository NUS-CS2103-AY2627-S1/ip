package lebron;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Manages the collection of tasks stored by the chatbot.
 */
public class TaskList {

    private final Task[] tasks;
    private int taskCount;

    /**
     * Creates an empty task list with space for up to 100 tasks.
     */
    public TaskList() {
        this.tasks = new Task[100];
        this.taskCount = 0;
    }

    /**
     * Adds a task to the end of the task list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task to add should not be null";
        assert taskCount < tasks.length : "Task list should not exceed capacity";

        tasks[taskCount] = task;
        taskCount++;
    }

    /**
     * Returns the task at the specified zero-based index.
     *
     * @param index zero-based index of the task
     * @return task at the specified index
     */
    public Task get(int index) {
        assert index >= 0 && index < taskCount
                : "Task index should be within the task list";

        return tasks[index];
    }

    /**
     * Returns the number of tasks currently stored.
     *
     * @return number of tasks in the list
     */
    public int size() {
        return taskCount;
    }

    /**
     * Marks the specified task as done.
     *
     * @param taskNumber one-based task number
     * @return task that was marked as done
     */
    public Task mark(int taskNumber) {
        assert taskNumber >= 1 && taskNumber <= taskCount
                : "Task number should be valid";

        Task task = tasks[taskNumber - 1];
        task.markAsDone();
        return task;
    }

    /**
     * Deletes the specified task from the task list.
     * Remaining tasks are shifted forward to close the gap.
     *
     * @param taskNumber one-based task number
     * @return task that was deleted
     */
    public Task delete(int taskNumber) {
        assert taskNumber >= 1 && taskNumber <= taskCount
                : "Task number should be valid";

        int index = taskNumber - 1;
        Task deletedTask = tasks[index];

        for (int i = index; i < taskCount - 1; i++) {
            tasks[i] = tasks[i + 1];
        }

        tasks[taskCount - 1] = null;
        taskCount--;

        return deletedTask;
    }

    /**
     * Finds all tasks whose descriptions contain any of the given keywords.
     *
     * @param keywords keywords to search for
     * @return task list containing all matching tasks
     */
    public TaskList find(String... keywords) {
        assert keywords != null : "Keywords should not be null";

        TaskList matchingTasks = new TaskList();

        Arrays.stream(tasks, 0, taskCount)
                .filter(task -> Arrays.stream(keywords)
                        .anyMatch(keyword -> task.description.toLowerCase()
                                .contains(keyword.toLowerCase())))
                .forEach(matchingTasks::add);

        return matchingTasks;
    }

    /**
     * Sorts tasks alphabetically by their descriptions.
     */
    public void sort() {
        Arrays.sort(
                tasks,
                0,
                taskCount,
                Comparator.comparing(
                        task -> task.description.toLowerCase()
                )
        );
    }
}



