package friday.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the ordered task collection and its operations, without doing console or file I/O.
 * All task numbers are one-based, matching the numbers shown to the user.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(List.of());
    }

    /**
     * Copies the initial list so external membership changes do not affect this collection.
     * The task objects themselves are shared, so their completion status is not copied.
     *
     * @param initialTasks tasks to include, in their original order.
     */
    public TaskList(List<Task> initialTasks) {
        tasks = new ArrayList<>(initialTasks);
    }

    /**
     * Returns the number of tasks, including completed tasks.
     *
     * @return the current task count.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Checks whether a one-based task number refers to an existing task.
     *
     * @param taskNumber number shown to the user.
     * @return {@code true} if the number is between one and the task count, inclusive.
     */
    public boolean isValidTaskNumber(int taskNumber) {
        return taskNumber >= 1 && taskNumber <= tasks.size();
    }

    /**
     * Appends a task without changing its completion status.
     *
     * @param task task to place at the end of the list.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given one-based number.
     *
     * @param taskNumber number shown to the user.
     * @return the task object held by this list, not a copy.
     * @throws IndexOutOfBoundsException if the task number does not exist.
     */
    public Task get(int taskNumber) {
        return tasks.get(toIndex(taskNumber));
    }

    /**
     * Removes and returns a task; later tasks move up by one number.
     *
     * @param taskNumber one-based number of the task to remove.
     * @return the removed task.
     * @throws IndexOutOfBoundsException if the task number does not exist.
     */
    public Task delete(int taskNumber) {
        return tasks.remove(toIndex(taskNumber));
    }

    /**
     * Marks a task as done and reports whether its status changed.
     *
     * @param taskNumber one-based number of the task to mark.
     * @return {@code true} if the status changed and needs saving.
     * @throws IndexOutOfBoundsException if the task number does not exist.
     */
    public boolean mark(int taskNumber) {
        Task task = get(taskNumber);
        if (task.isDone()) {
            return false;
        }
        task.markAsDone();
        return true;
    }

    /**
     * Marks a task as not done and reports whether its status changed.
     *
     * @param taskNumber one-based number of the task to unmark.
     * @return {@code true} if the status changed and needs saving.
     * @throws IndexOutOfBoundsException if the task number does not exist.
     */
    public boolean unmark(int taskNumber) {
        Task task = get(taskNumber);
        if (!task.isDone()) {
            return false;
        }
        task.unmarkAsDone();
        return true;
    }

    /**
     * Finds tasks occurring on a date without changing the collection.
     * Completed tasks are included when their date matches.
     *
     * @param date calendar date to search for.
     * @return matching tasks' original one-based list numbers, in list order.
     */
    public List<Integer> findTaskNumbersOn(LocalDate date) {
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                matches.add(i + 1);
            }
        }
        return matches;
    }

    /**
     * Finds descriptions containing the keyword using a case-sensitive, literal substring match.
     * Includes completed tasks without changing their order or completion status.
     *
     * @param keyword nonblank search text, already trimmed by the parser.
     * @return matching tasks' original one-based numbers, in list order.
     * @throws IllegalArgumentException if the keyword is blank.
     */
    public List<Integer> findTaskNumbersContaining(String keyword) {
        if (keyword.isBlank()) {
            throw new IllegalArgumentException("The search keyword cannot be blank.");
        }
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getDescription().contains(keyword)) {
                matches.add(i + 1);
            }
        }
        return matches;
    }

    /**
     * Returns an unmodifiable copy of the current list for storage.
     * Membership and order are copied; the mutable task objects themselves are shared.
     *
     * @return an unmodifiable snapshot of the task references in list order.
     */
    public List<Task> toList() {
        return List.copyOf(tasks);
    }

    /**
     * Validates a user-facing task number before converting it to an internal list index.
     */
    private int toIndex(int taskNumber) {
        if (!isValidTaskNumber(taskNumber)) {
            throw new IndexOutOfBoundsException("Invalid task number: " + taskNumber);
        }
        int index = taskNumber - 1;
        assert index >= 0 && index < tasks.size()
                : "A valid one-based task number must map to an existing zero-based index.";
        return index;
    }
}
