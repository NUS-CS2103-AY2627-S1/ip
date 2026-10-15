package food.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import food.exception.FoodInputException;
import food.exception.FoodStorageException;

/**
 * The tasks the user is keeping, together with the operations that change them.
 *
 * <p>Pulled out of Foodbot so that exactly one class knows how many tasks exist. That makes this
 * the only place that can answer "is 99 a real task number?", so the bounds check lives here
 * instead of being repeated by every caller that wants to reach into the list.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty list, e.g. when there is nothing saved yet. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a list holding tasks that were loaded elsewhere, e.g. read back from the save file.
     *
     * @param tasks the tasks to start with; copied, so later changes to the caller's list do not
     *              silently change ours.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns how many tasks are in the list, e.g. for the "now you have N tasks" message.
     *
     * @return the number of tasks currently held.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add; no index check is needed because appending is always valid.
     * @throws FoodInputException if an identical task is already on the list.
     */
    public void add(Task task) throws FoodInputException {
        boolean isDuplicate = this.tasks.stream().anyMatch(existing -> existing.isSameAs(task));
        if (isDuplicate) {
            throw new FoodInputException("That order is already on the board, chef.");
        }
        this.tasks.add(task);
    }

    /**
     * Returns the tasks whose descriptions contain the given keyword, ignoring case.
     *
     * <p>Only the description is searched, so "find 2026" does not match a deadline that happens
     * to fall in 2026.
     *
     * @param keyword the text to search for.
     * @return the matching tasks in list order, empty if none match.
     */
    public List<Task> find(String keyword) {
        return this.tasks.stream()
                .filter(task -> task.hasKeyword(keyword))
                .toList();
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index  0-based position of the task.
     * @param action the command word, used only to word the error message.
     * @return the task that was removed.
     * @throws FoodInputException if no task sits at that index.
     */
    public Task delete(int index, String action) throws FoodInputException {
        this.checkIndex(index, action);
        return this.tasks.remove(index);
    }

    /**
     * Returns the task at the given index, leaving it in the list.
     *
     * @param index  0-based position of the task.
     * @param action the command word, used only to word the error message.
     * @return the task at that index.
     * @throws FoodInputException if no task sits at that index.
     */
    public Task get(int index, String action) throws FoodInputException {
        this.checkIndex(index, action);
        return this.tasks.get(index);
    }

    /**
     * Returns a read-only view of the tasks, for code that only needs to walk over them, such as
     * printing the list or writing it to disk. Read-only so that handing the list out cannot
     * become a second way of modifying it.
     *
     * @return an unmodifiable view of the tasks, in list order.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Returns a copy of the list that a later {@link #restore} can bring back, so that a command
     * can be undone.
     *
     * <p>The copy is made as save-format text rather than as Task objects. Tasks are mutable, so a
     * list of the same objects would not be a backup at all: marking a task done would change the
     * "backup" too. Text is immutable, and {@link Task#fromSaveFormat} already knows how to
     * rebuild a task from it.
     *
     * @return one save-format line per task, in list order.
     */
    public List<String> snapshot() {
        return this.tasks.stream()
                .map(Task::toSaveFormat)
                .toList();
    }

    /**
     * Replaces every task with the ones rebuilt from an earlier {@link #snapshot}.
     *
     * @param snapshot the lines returned by {@link #snapshot}.
     * @throws FoodStorageException if a line cannot be rebuilt, which cannot happen for a snapshot
     *                              this class produced.
     */
    public void restore(List<String> snapshot) throws FoodStorageException {
        this.tasks.clear();
        for (String line : snapshot) {
            this.tasks.add(Task.fromSaveFormat(line));
        }
    }

    /**
     * The bounds are tested with an if rather than by catching IndexOutOfBoundsException:
     * exceptions are for the unexpected, and a mistyped task number is entirely expected.
     */
    private void checkIndex(int index, String action) throws FoodInputException {
        if (index < 0 || index >= this.tasks.size()) {
            throw new FoodInputException(
                    String.format("There's no order with that number to %s, chef", action));
        }
    }
}
