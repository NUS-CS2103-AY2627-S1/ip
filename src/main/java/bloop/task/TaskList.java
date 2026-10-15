package bloop.task;

import java.util.ArrayList;
import java.util.List;

/** Manages the collection of tasks in the current session. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this(new ArrayList<>());
    }

    /** Creates a task list from previously loaded tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Adds a task at a zero-based index. */
    public void add(int index, Task task) {
        tasks.add(index, task);
    }

    /** Returns the task at a zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Removes and returns the task at a zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Removes and returns the last task. */
    public Task removeLast() {
        return tasks.remove(tasks.size() - 1);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a defensive copy of the tasks in their current order. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }
}
