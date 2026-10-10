package jasper.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import jasper.JasperException;

/**
 * Represents a collection of tasks and provides operations to manage them.
 */
public class TaskList implements Iterable<Task> {
    /** Internal list storing the tasks */
    private final List<Task> tasks;

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks Initial list of tasks.
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Constructs an empty TaskList with a default initial capacity.
     */
    public TaskList() {
        this(new ArrayList<>(100));
    }

    /**
     * Checks if the given index is valid within the specified task list.
     *
     * @param start 0-based starting index of range to validate.
     * @param stop 0-based last index of range to validate.
     * @param tasks List of tasks to check against.
     * @throws JasperException If the index is out of bounds.
     */
    private static void checkRange(List<Task> tasks, int start, int stop) throws JasperException {
        if (start < 0 || stop >= tasks.size()) {
            throw new JasperException("Task index out of range!");
        }
        if (start > stop) {
            throw new JasperException("Start index must be at most stop index!");
        }
    }

    /**
     * Returns whether the task list is empty.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the number of tasks in the list.
     */
    public int getCount() {
        return tasks.size();
    }

    /**
     * Adds a new task to the list.
     *
     * @param task Task to be added.
     * @throws JasperException If matching task has already been added.
     */
    public void add(Task task) throws JasperException {
        for (int i = 0; i < tasks.size(); ++i) {
            Task t = tasks.get(i);
            if (task.equals(t)) {
                throw new JasperException("Task already present in list at index " + (i + 1) + "!");
            }
        }
        tasks.add(task);
    }

    /**
     * Deletes the tasks in the specified inclusive range of indices.
     *
     * @param start 0-based index of first task to delete.
     * @param stop 0-based index of last task to delete.
     * @return Formatted string of deleted tasks.
     * @throws JasperException If the indices are out of bounds.
     */
    public String delete(int start, int stop) throws JasperException {
        checkRange(tasks, start, stop);
        StringBuilder sb = new StringBuilder();
        Task[] removed = new Task[stop - start + 1];
        for (int i = removed.length - 1; i >= 0; --i) {
            removed[i] = tasks.remove(start + i);
        }
        for (int i = 0; i < removed.length; ++i) {
            sb.append('\n').append(start + i + 1).append(". ").append(removed[i]);
        }
        return sb.toString();
    }

    /**
     * Marks the tasks in the specified inclusive range of indices as completed.
     *
     * @param start 0-based index of first task to mark.
     * @param stop 0-based index of last task to mark.
     * @return Formatted string of marked tasks.
     * @throws JasperException If the indices are out of bounds.
     */
    public String mark(int start, int stop) throws JasperException {
        checkRange(tasks, start, stop);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= stop; ++i) {
            Task t = tasks.get(i);
            t.markDone();
            sb.append('\n').append(i + 1).append(". ").append(t);
        }
        return sb.toString();
    }

    /**
     * Marks the task at the specified index as not completed.
     *
     * @param start 0-based index of first task to unmark.
     * @param stop 0-based index of last task to unmark.
     * @return Formatted string of unmark tasks.
     * @throws JasperException If the indices are out of bounds.
     */
    public String unmark(int start, int stop) throws JasperException {
        checkRange(tasks, start, stop);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i <= stop; ++i) {
            Task t = tasks.get(i);
            t.markUndone();
            sb.append('\n').append(i + 1).append(". ").append(t);
        }
        return sb.toString();
    }

    /**
     * Returns a formatted string containing all tasks that match the given search phrase.
     *
     * @param phrase Search phrase to match against task descriptions.
     * @return Formatted string of all matching tasks.
     */
    public String find(String phrase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); ++i) {
            Task t = tasks.get(i);
            if (t.getDescription().contains(phrase)) {
                sb.append(i + 1).append(". ").append(t).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Returns a sequential stream over the tasks in this list.
     */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.size(); ++i) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append('\n');
        }
        return sb.toString();
    }
}
