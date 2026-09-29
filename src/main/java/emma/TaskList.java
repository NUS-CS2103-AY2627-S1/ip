package emma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * The tasks Emma is tracking, held in memory only.
 */
public class TaskList {

    private final ArrayList<Task> tasks = new ArrayList<>();

    /**
     * Creates a list holding the given tasks.
     *
     * @param initialTasks the tasks to start with.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks.addAll(initialTasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to store.
     */
    public void add(Task task) {
        assert task != null : "the task list should never be asked to hold a null task";
        tasks.add(task);
    }

    /**
     * Checks whether the list already holds a task matching the given one, by
     * walking the list and comparing each task in turn.
     *
     * @param task the task to look for.
     * @return true if a duplicate of it is already stored.
     */
    public boolean contains(Task task) {
        for (Task stored : tasks) {
            if (stored.isDuplicateOf(task)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rejects a task number that names no task, so that every method taking one
     * refuses a bad number in the same way and with the same words.
     *
     * @param taskNumber the 1-based task number.
     * @throws IndexOutOfBoundsException if no task has that number
     */
    private void requireValidTaskNumber(int taskNumber) {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new IndexOutOfBoundsException("No task numbered " + taskNumber);
        }
    }

    /**
     * Returns the numbered task.
     *
     * @param taskNumber the 1-based task number.
     * @return the matching task.
     * @throws IndexOutOfBoundsException if no task has that number
     */
    public Task get(int taskNumber) {
        requireValidTaskNumber(taskNumber);
        return tasks.get(taskNumber - 1);
    }

    /**
     * Puts a task back at a given position, so that a delete can be undone.
     *
     * @param taskNumber the 1-based position to insert at.
     * @param task the task to put back.
     * @throws IndexOutOfBoundsException if the position is past one place after the end
     */
    public void insert(int taskNumber, Task task) {
        if (taskNumber < 1 || taskNumber > tasks.size() + 1) {
            throw new IndexOutOfBoundsException("No position numbered " + taskNumber);
        }
        tasks.add(taskNumber - 1, task);
    }

    /**
     * Returns how many tasks are stored.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Marks the numbered task as done or not done.
     *
     * @param taskNumber the 1-based task number.
     * @param isDone true for "mark", false for "unmark".
     * @return the task that was changed.
     * @throws IndexOutOfBoundsException if no task has that number
     */
    public Task applyMark(int taskNumber, boolean isDone) {
        Task task = get(taskNumber);
        task.setDone(isDone);
        return task;
    }

    /**
     * Removes the task from the list.
     *
     * @param taskNumber the 1-based task number.
     * @return the task that was removed.
     * @throws IndexOutOfBoundsException if no task has that number
     */
    public Task delete(int taskNumber) {
        requireValidTaskNumber(taskNumber);
        return tasks.remove(taskNumber - 1);
    }

    /**
     * Returns the tasks in order, for saving. The list cannot be modified
     * through this view; use add, applyMark and delete instead.
     *
     * @return a read-only view of the stored tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Checks whether any tasks are stored.
     *
     * @return true if there are no tasks.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Builds a 1-based numbered list of every task, one per line.
     *
     * @return the tasks, one per line; empty if there are none.
     */
    public String format() {
        return format(task -> true);
    }

    /**
     * Builds a numbered list of the matching tasks, keeping each task's number from
     * the full list so that it can still be marked or deleted by that number.
     *
     * @param matches the test a task must pass to be listed.
     * @return the matching tasks, one per line; empty if none match.
     */
    public String format(Predicate<Task> matches) {
        StringBuilder list = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            if (!matches.test(tasks.get(i))) {
                continue;
            }
            if (list.length() > 0) {
                list.append("\n");
            }
            list.append(i + 1).append(". ").append(tasks.get(i));
        }
        return list.toString();
    }
}
