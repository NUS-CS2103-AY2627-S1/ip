package am.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Maintains the ordered collection of tasks used by the application.
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
     * Replaces a task in place without changing the list's size or order.
     *
     * @param taskIndex zero-based task index
     * @param replacement task to put at the requested index
     * @return the previous task
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Task replaceTask(int taskIndex, Task replacement) {
        assert replacement != null : "A task list must not contain null tasks";
        return tasks.set(taskIndex, replacement);
    }

    /**
     * Appends a task to the end of the list.
     *
     * @param task task to add
     */
    public void addTask(Task task) {
        assert task != null : "A task list must not contain null tasks";
        tasks.add(task);
    }

    /**
     * Marks the task at an index as complete.
     *
     * @param taskIndex zero-based task index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void markTask(int taskIndex) {
        tasks.get(taskIndex).mark();
    }

    /**
     * Marks the task at an index as incomplete.
     *
     * @param taskIndex zero-based task index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void unmarkTask(int taskIndex) {
        tasks.get(taskIndex).unmark();
    }

    /**
     * Removes the task at an index.
     *
     * @param taskIndex zero-based task index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public void deleteTask(int taskIndex) {
        tasks.remove(taskIndex);
    }

    /**
     * Returns the task at an index.
     *
     * @param taskIndex zero-based task index
     * @return task at the requested index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public Task getTask(int taskIndex) {
        return tasks.get(taskIndex);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return task count
     */
    public int getLength() {
        return tasks.size();
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to find in task descriptions
     * @return matching tasks in their original order, or an empty string
     */
    public String getMatchingTask(String keyword) {
        String normalizedKeyword = keyword.trim().toLowerCase();
        return formatTasks(index -> tasks.get(index).getName().toLowerCase().contains(normalizedKeyword));
    }

    /**
     * Formats tasks that ended before a specified time.
     *
     * @param datetime time used to identify past tasks
     * @return numbered display text for past tasks
     */
    public String getPastTasks(LocalDateTime datetime) {
        return formatTasks(index -> tasks.get(index).isPast(datetime));
    }

    /**
     * Formats every task in list order with one-based display numbering.
     *
     * @return numbered display text for all tasks
     */
    @Override
    public String toString() {
        return formatTasks(index -> true);
    }

    /**
     * Formats selected tasks while preserving their original list numbers.
     *
     * @param shouldInclude predicate selecting zero-based task indexes
     * @return numbered task descriptions separated by newlines, or an empty string
     */
    private String formatTasks(IntPredicate shouldInclude) {
        return IntStream.range(0, tasks.size())
                .filter(shouldInclude)
                .mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining("\n"));
    }
}
