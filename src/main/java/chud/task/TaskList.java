package chud.task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * The list of tasks the chatbot is tracking. Wraps the underlying storage collection so callers
 * work through named task-list operations (add/get/remove/size) instead of reaching into a raw
 * {@code ArrayList} directly.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list backed by the given tasks, e.g. ones just loaded from disk. */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /** Appends the given task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns the task at the given 0-based index. */
    public Task remove(int index) {
        // TaskList does no user-facing validation itself -- every index it's called with is
        // expected to already have been checked by the caller (e.g. Parser.parseTaskIndex), so
        // this documents that expectation rather than duplicating the bounds check.
        assert index >= 0 && index < tasks.size() : "index must be a valid, already-validated position";
        return tasks.remove(index);
    }

    /** Returns the task at the given 0-based index. */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "index must be a valid, already-validated position";
        return tasks.get(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }

    /** Returns a sequential Stream over the tasks in this list, e.g. for filtering by keyword or date. */
    public Stream<Task> stream() {
        return tasks.stream();
    }

    /**
     * Reorders the tasks in this list in place according to the given comparator, e.g. for the
     * {@code list /sort} option. The sort is stable, so tasks that compare equal keep their
     * existing relative order.
     */
    public void sort(Comparator<Task> comparator) {
        tasks.sort(comparator);
    }
}
