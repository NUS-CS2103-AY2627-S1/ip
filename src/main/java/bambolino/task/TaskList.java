package bambolino.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages the tasks currently stored by Bambolino.
 */
public class TaskList {
    /** The tasks in their display order. */
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(Collection<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Returns the task at the supplied zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Returns the most recently added task. */
    public Task getLast() {
        assert !tasks.isEmpty() : "cannot get the last task from an empty list";
        return tasks.getLast();
    }

    /** Removes and returns the task at the supplied zero-based index. */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Returns whether the list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns a read-only snapshot for persistence and presentation. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
