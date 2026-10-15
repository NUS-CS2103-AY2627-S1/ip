package duke;

import java.util.ArrayList;
import java.util.List;

/** Owns the in-memory collection of tasks and its domain operations. */
public class TaskList {
    private static final int MAX_TASKS = 100;

    /** Mutable task collection owned by this list. */
    private final List<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Returns the tasks for display or persistence. */
    public List<Task> getTasks() {
        return tasks;
    }

    /** Adds a task when capacity permits. */
    public boolean add(Task task) {
        if (tasks.size() >= MAX_TASKS) {
            return false;
        }
        tasks.add(task);
        return true;
    }

    /** Removes and returns the task at a one-based position. */
    public Task remove(int number) {
        assert contains(number) : "Task number must be valid before removal";
        return tasks.remove(number - 1);
    }

    /** Returns the task at a one-based position. */
    public Task get(int number) {
        assert contains(number) : "Task number must be valid before lookup";
        return tasks.get(number - 1);
    }

    /** Returns whether a one-based task number is valid. */
    public boolean contains(int number) {
        return number >= 1 && number <= tasks.size();
    }

    /** Returns whether an equivalent task is already stored. */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> task.getClass().equals(candidate.getClass())
                && task.getDisplayText().equalsIgnoreCase(candidate.getDisplayText()));
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }
}
