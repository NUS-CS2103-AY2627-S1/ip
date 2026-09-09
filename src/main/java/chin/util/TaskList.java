package chin.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import chin.task.Deadline;
import chin.task.Task;

/**
 * Ordered, mutable list of {@link Task} entries with lookup and search helpers.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list initialised from {@code initial}. */
    public TaskList(List<Task> initial) {
        assert initial != null : "initial task list must not be null";
        this.tasks = new ArrayList<>(initial);
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        assert index >= 0 && index < tasks.size() : "index " + index + " out of range";
        return tasks.get(index);
    }

    /** Appends a task to the end of the list. */
    public void add(Task task) {
        assert task != null : "task must not be null";
        tasks.add(task);
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        assert index >= 0 && index < tasks.size() : "index " + index + " out of range";
        return tasks.remove(index);
    }

    /** Returns the backing list as a read-through view for callers like storage. */
    public List<Task> asList() {
        return tasks;
    }

    /**
     * Sorts tasks in place so that {@link Deadline} tasks come first (earliest
     * date first), followed by all other task types in their original order.
     */
    public void sortByDeadline() {
        Comparator<Task> byDeadline = Comparator.comparing(
                task -> task instanceof Deadline ? ((Deadline) task).getBy() : LocalDate.MAX);
        tasks.sort(byDeadline);
    }

    /** Returns tasks whose string form contains {@code keyword} (case-insensitive). */
    public List<Task> find(String keyword) {
        assert keyword != null : "keyword must not be null";
        String lowerKeyword = keyword.toLowerCase();
        return tasks.stream()
                .filter(task -> task.toString().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
}
