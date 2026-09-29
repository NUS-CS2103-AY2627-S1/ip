package xue.model;

import xue.XueException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Stores and manages the tasks known to Xue. */
public class TaskList {
    /** Maximum number of tasks accepted by the application. */
    private static final int MAX_TASKS = 100;
    /** The mutable collection owned by this task list. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing saved tasks. */
    public TaskList(List<Task> savedTasks) {
        assert savedTasks != null;
        // Loading more than the application limit would violate the invariant enforced by add().
        assert savedTasks.size() <= MAX_TASKS;
        this.tasks = new ArrayList<>(savedTasks);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns tasks whose descriptions contain the keyword, ignoring letter case. */
    public List<Task> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
                .toList();
    }

    /** Adds a task, enforcing Xue's task limit. */
    public void add(Task task) {
        if (tasks.size() == MAX_TASKS) {
            throw new XueException("Your task list is full. I refuse to carry any more of your tasks!");
        }
        // A null task would make display, search, and storage fail later and less clearly.
        assert task != null;
        tasks.add(task);
    }

    /** Removes and returns a task at a zero-based index. */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /** Returns a copy for storage without exposing internal state. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /** Replaces the current tasks with copies of a previously captured valid state. */
    public void replaceWith(List<Task> replacement) {
        assert replacement != null;
        if (replacement.size() > MAX_TASKS) {
            throw new XueException("Your task list is full. I refuse to carry any more of your tasks!");
        }
        tasks.clear();
        for (Task task : replacement) {
            assert task != null;
            tasks.add(new Task(task));
        }
    }
}
