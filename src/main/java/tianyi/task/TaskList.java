package tianyi.task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Stores tasks and provides operations for updating and querying them.
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
     * Creates a task list containing a defensive copy of the supplied tasks.
     *
     * @param tasks Initial tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Reports whether this list contains no tasks.
     *
     * @return {@code true} if the list is empty, otherwise {@code false}.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the number of incomplete tasks in this list.
     *
     * @return Incomplete task count.
     */
    public int countIncompleteTasks() {
        return (int) tasks.stream().filter(task -> !task.isDone()).count();
    }

    /**
     * Returns the number of incomplete dated tasks occurring on the specified date.
     *
     * @param time Date used to select deadlines and events.
     * @return Incomplete matching task count.
     */
    public int countIncompleteTasks(TaskTime time) {
        return (int) listTasks(time).stream()
                .filter(indexedTask -> !indexedTask.getTask().isDone())
                .count();
    }

    /**
     * Returns an unmodifiable snapshot of the current tasks.
     *
     * @return Copy of the current task sequence.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Adds a task to this list.
     *
     * @param task Task to add.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns a task.
     *
     * @param index Zero-based index of the task to delete.
     * @return Removed task.
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks a task as completed and returns it.
     *
     * @param index Zero-based index of the task to mark.
     * @return Updated task.
     */
    public Task markTask(int index) {
        Task task = tasks.get(index);
        task.markAsDone();

        return task;
    }

    /**
     * Marks a task as incomplete and returns it.
     *
     * @param index Zero-based index of the task to unmark.
     * @return Updated task.
     */
    public Task unmarkTask(int index) {
        Task task = tasks.get(index);
        task.unmarkAsDone();

        return task;
    }

    /**
     * Returns every task with its original list number.
     *
     * @return Numbered tasks, or an empty list when no tasks exist.
     */
    public List<IndexedTask> listTasks() {
        return getIndexedTasks();
    }

    /**
     * Returns dated tasks that occur on the specified date.
     *
     * @param time Date used to select deadlines and events.
     * @return Numbered matching tasks, or an empty list when none match.
     */
    public List<IndexedTask> listTasks(TaskTime time) {
        return getIndexedTasks().stream()
                .filter(indexedTask -> indexedTask.getTask().isOccurringOn(time))
                .toList();
    }

    /**
     * Returns tasks whose descriptions contain the specified keyword.
     *
     * @param keyword Keyword used to select tasks.
     * @return Numbered matching tasks, or an empty list when none match.
     */
    public List<IndexedTask> listTasks(String keyword) {
        return getIndexedTasks().stream()
                .filter(indexedTask -> indexedTask.getTask().matchesKeyword(keyword))
                .toList();
    }

    /**
     * Returns all tasks paired with their current one-based task numbers.
     *
     * @return Numbered tasks in their current list order.
     */
    private List<IndexedTask> getIndexedTasks() {
        return IntStream.range(0, tasks.size())
                .mapToObj(index -> new IndexedTask(index + 1, tasks.get(index)))
                .toList();
    }
}
