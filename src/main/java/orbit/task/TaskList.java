package orbit.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import orbit.exception.OrbitException;

/**
 * Owns Orbit's ordered collection of tasks and its index validation.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Creates a task list containing the supplied tasks in order.
     *
     * @param tasks initial tasks
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Returns a read-only view of the stored tasks.
     *
     * @return stored tasks in display order
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns the number of stored tasks.
     *
     * @return task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns a task by its one-based display number.
     *
     * @param taskNumber one-based task number
     * @return selected task
     * @throws OrbitException if the task number is out of range
     */
    public Task get(int taskNumber) throws OrbitException {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Appends a task.
     *
     * @param task task to append
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes a task by its one-based display number.
     *
     * @param taskNumber one-based task number
     * @return removed task
     * @throws OrbitException if the task number is out of range
     */
    public Task delete(int taskNumber) throws OrbitException {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Restores a task at its former one-based display number.
     *
     * @param taskNumber one-based task number
     * @param task task to restore
     */
    public void restore(int taskNumber, Task task) {
        assert task != null : "A restored task should not be null";
        assert taskNumber >= 1 && taskNumber <= tasks.size() + 1
                : "A restored task should return to a valid position";
        tasks.add(taskNumber - 1, task);
    }

    /**
     * Finds tasks whose descriptions contain the given keyword.
     *
     * @param keyword text to find in task descriptions
     * @return read-only matching tasks in their original order
     */
    public List<Task> find(String keyword) {
        return tasks.stream()
                .filter(task -> task.getDescription().contains(keyword))
                .toList();
    }

    /**
     * Sorts dated tasks chronologically while keeping undated tasks last.
     * Tasks with the same date retain their relative order.
     */
    public void sortChronologically() {
        Comparator<Task> chronologicalOrder = Comparator.comparing(
                TaskList::getSortDate, Comparator.nullsLast(Comparator.naturalOrder()));
        tasks.sort(chronologicalOrder);
    }

    /**
     * Restores a previously captured ordering of this task list.
     *
     * @param previousOrder tasks in their order before a failed change
     */
    public void restoreOrder(List<Task> previousOrder) {
        assert previousOrder != null : "A previous task order should not be null";
        assert previousOrder.size() == tasks.size()
                : "A restored order should contain the same number of tasks";
        tasks.clear();
        tasks.addAll(previousOrder);
    }

    private int toIndex(int taskNumber) throws OrbitException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new OrbitException("Task number " + taskNumber + " is out of range.");
        }
        return taskNumber - 1;
    }

    private static LocalDate getSortDate(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }
}
