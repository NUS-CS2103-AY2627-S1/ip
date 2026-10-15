package bot.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Manages the collection of tasks used by the chatbot.
 */
public class TaskList implements Iterable<Task> {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    private TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds one or more tasks to the end of the list in the given order.
     *
     * @param tasksToAdd tasks to add.
     */
    public void add(Task... tasksToAdd) {
        assert tasksToAdd != null : "Tasks to add must not be null";
        for (Task task : tasksToAdd) {
            assert task != null : "Task list must not contain null tasks";
        }

        Collections.addAll(tasks, tasksToAdd);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the index
     */
    public Task getTask(int index) {
        assert index >= 0 && index < tasks.size() : "Task index must be within the list bounds";

        return tasks.get(index);
    }

    /**
     * Deletes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return deleted task
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at a zero-based index as done.
     *
     * @param index zero-based task index.
     * @return task that was marked
     */
    public Task mark(int index) {
        Task task = getTask(index);
        task.mark();
        return task;
    }

    /**
     * Marks the task at a zero-based index as not done.
     *
     * @param index zero-based task index.
     * @return task that was unmarked
     */
    public Task unmark(int index) {
        Task task = getTask(index);
        task.unmark();
        return task;
    }

    /**
     * Sorts dated tasks chronologically and places undated tasks afterward.
     * Tasks with the same sort key keep their original relative order.
     */
    public void sortChronologically() {
        Comparator<Task> byDateTime = Comparator.comparing(
                task -> task.getChronologicalSortKey().orElse(LocalDateTime.MAX));
        tasks.sort(byDateTime);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether the list already contains a task with the same identifying details.
     * Completion status is ignored when comparing tasks.
     *
     * @param task task to compare against the stored tasks.
     * @return {@code true} if a matching task already exists
     */
    public boolean containsSameTask(Task task) {
        assert task != null : "Task being compared must not be null";
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Returns tasks whose descriptions contain the given keyword.
     * Matching is case-insensitive and preserves the original task order.
     *
     * @param keyword keyword to search for.
     * @return new task list containing matching tasks
     */
    public TaskList find(String keyword) {
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
        return new TaskList(matchingTasks);
    }

    /**
     * Returns a read-only iterator over the tasks for operations such as saving.
     *
     * @return iterator over tasks in list order
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
