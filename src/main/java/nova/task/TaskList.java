package nova.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Owns Nova's in-memory task collection and its list operations.
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
     * Creates a task list containing the supplied tasks in their current order.
     *
     * @param initialTasks tasks loaded when Nova starts.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return task at the index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns an immutable snapshot of the tasks in list order.
     *
     * @return current tasks.
     */
    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Inserts a task at a specific position.
     * This supports restoring a deletion when saving fails.
     *
     * @param index zero-based insertion index.
     * @param task task to insert.
     */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @return removed task.
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Replaces and returns the task at a zero-based index.
     *
     * @param index zero-based task index.
     * @param task replacement task.
     * @return task previously stored at the index.
     */
    public Task set(int index, Task task) {
        return tasks.set(index, task);
    }

    /**
     * Marks a task as completed.
     *
     * @param index zero-based task index.
     */
    public void mark(int index) {
        tasks.get(index).markAsDone();
    }

    /**
     * Marks a task as not completed.
     *
     * @param index zero-based task index.
     */
    public void unmark(int index) {
        tasks.get(index).markAsNotDone();
    }

    /**
     * Finds scheduled tasks occurring on a date and retains their original task numbers.
     *
     * @param date date to search.
     * @return matching tasks with their one-based task numbers.
     */
    public List<NumberedTask> findTasksOn(LocalDate date) {
        return findTasksMatching(task -> task.occursOn(date));
    }

    /**
     * Finds tasks whose descriptions contain a keyword and retains their original task numbers.
     *
     * @param keyword keyword to find, ignoring letter case.
     * @return matching tasks with their one-based task numbers.
     */
    public List<NumberedTask> findTasksByDescription(String keyword) {
        return findTasksMatching(task -> task.hasDescriptionContaining(keyword));
    }

    /**
     * Finds tasks satisfying a condition while retaining their original task numbers.
     *
     * @param condition condition used to select tasks.
     * @return matching tasks with their one-based task numbers.
     */
    private List<NumberedTask> findTasksMatching(Predicate<Task> condition) {
        return IntStream.range(0, tasks.size())
                .filter(index -> condition.test(tasks.get(index)))
                .mapToObj(index -> new NumberedTask(index + 1, tasks.get(index)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Couples a task with its one-based number in the complete task list.
     *
     * @param number task number shown to the user.
     * @param task matching task.
     */
    public record NumberedTask(int number, Task task) {
        /**
         * Validates the internal one-based task reference.
         */
        public NumberedTask {
            assert number > 0 : "Displayed task number must be positive";
            assert task != null : "Numbered task must reference a task";
        }
    }
}
