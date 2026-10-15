package sevensix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Stores tasks and provides the operations used to manage their order.
 */
public class TaskList implements Iterable<Task> {
    /** The tasks in the order in which they were added. */
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the supplied tasks.
     *
     * @param tasks the initial tasks.
     */
    public TaskList(List<Task> tasks) {
        assert tasks != null : "A task list is built either from storage or from an empty list";
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of this list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        assert task != null : "A null task must never enter the list, as every task is displayed and saved";
        tasks.add(task);
    }

    /** Checks whether an equivalent task already exists, regardless of its status.
     *
     * @param candidate the task to check.
     * @return true if its type, description, and date details are already present.
     */
    public boolean hasDuplicate(Task candidate) {
        return tasks.stream().anyMatch(task -> TaskValidation.hasSameDetails(task, candidate));
    }

    /** Restores all tasks from an independently copied snapshot.
     *
     * @param replacementTasks the tasks that become the new task list.
     */
    public void replaceWith(List<Task> replacementTasks) {
        tasks.clear();
        tasks.addAll(replacementTasks);
    }

    /**
     * Returns the task at a zero-based index.
     *
     * @param index the task index.
     * @return the task at the index.
     */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size()
                : "The caller must reject an out-of-range task number before reading a task";
        return tasks.get(index);
    }

    /** Resolves a user-facing task number with a recoverable error for missing tasks.
     *
     * @param taskNumber the one-based task number.
     * @return the task at that position.
     * @throws SevenSixException if the number is outside the list.
     */
    public Task getByNumber(int taskNumber) throws SevenSixException {
        if (taskNumber < 1 || taskNumber > size()) {
            throw new SevenSixException(Ui.TASK_NOT_FOUND);
        }
        return get(taskNumber - 1);
    }

    /**
     * Removes and returns the task at a zero-based index.
     *
     * @param index the task index.
     * @return the removed task.
     */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size()
                : "The caller must reject an out-of-range task number before removing a task";
        return tasks.remove(index);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return the task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns tasks whose descriptions contain the supplied keyword, ignoring letter case.
     *
     * @param keyword the text to search for.
     * @return a new list containing the matching tasks in their original order.
     */
    public List<Task> find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "A blank keyword must be rejected before searching";
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .toList();
    }

    /**
     * Returns an iterator over the tasks in list order.
     *
     * @return an iterator over the tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
