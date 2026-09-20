package edith.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

/** Owns Edith's tasks and the operations on the task collection. */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private long nextInsertionOrder;

    /** Adds a task to the list. */
    public void add(Task task) {
        add(task, nextInsertionOrder);
    }

    /**
     * Adds a task with a previously saved insertion order.
     *
     * @param task task to add
     * @param insertionOrder permanent order assigned when the task was first added
     */
    public void add(Task task, long insertionOrder) {
        assert task != null : "Task list must not contain null tasks";
        assert insertionOrder >= 0 : "Insertion order must not be negative";
        task.setInsertionOrder(insertionOrder);
        tasks.add(task);
        nextInsertionOrder = Math.max(nextInsertionOrder, insertionOrder + 1);
    }

    /**
     * Inserts a task at a zero-based index without changing its saved insertion order.
     *
     * @param index zero-based location at which to insert the task
     * @param task task to insert
     */
    public void add(int index, Task task) {
        assert task != null : "Task list must not contain null tasks";
        tasks.add(index, task);
    }

    /**
     * Returns whether this list already contains a task with the same details.
     *
     * @param task task to check
     * @return true if an identical task is already in the list
     */
    public boolean hasDuplicate(Task task) {
        return tasks.stream().anyMatch(existingTask -> existingTask.hasSameDetails(task));
    }

    /**
     * Returns an immutable snapshot of the current display order.
     *
     * @return immutable task list in its current display order
     */
    public List<Task> getTasksInCurrentOrder() {
        return List.copyOf(tasks);
    }

    /**
     * Restores a previously captured display order.
     *
     * @param previousOrder order to restore
     */
    public void restoreOrder(List<Task> previousOrder) {
        assert previousOrder != null : "Previous task order must not be null";
        tasks.clear();
        tasks.addAll(previousOrder);
    }

    /** Returns a task by zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns a task by zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Sorts the tasks in the given order.
     *
     * @param sortOrder order in which to arrange the tasks
     */
    public void sort(SortOrder sortOrder) {
        assert sortOrder != null : "Sort order must not be null";
        Comparator<Task> alphabeticalComparator =
                Comparator.comparing(Task::getDescription, String.CASE_INSENSITIVE_ORDER);
        Comparator<Task> comparator = switch (sortOrder) {
            case ALPHABETICAL -> alphabeticalComparator;
            case ALPHABETICAL_DESCENDING -> alphabeticalComparator.reversed();
            case DATE -> Comparator.comparing(
                    Task::getDateTimeForSorting,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case DATE_DESCENDING -> Comparator.comparing(
                    Task::getDateTimeForSorting,
                    Comparator.nullsLast(Comparator.reverseOrder()));
            case STATUS -> Comparator.comparing(Task::isDone);
            case ADDED -> Comparator.comparingLong(Task::getInsertionOrder);
            case ADDED_DESCENDING -> Comparator.comparingLong(Task::getInsertionOrder).reversed();
        };

        tasks.sort(comparator);
    }

    /**
     * Returns the zero-based indices of tasks whose descriptions contain the keyword.
     *
     * @param keyword text to search for
     * @return indices of matching tasks in their original order
     */
    public List<Integer> findMatchingIndices(String keyword) {
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).matchesDescription(keyword))
                .boxed()
                .toList();
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
}
