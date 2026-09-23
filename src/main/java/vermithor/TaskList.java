package vermithor;

import java.util.ArrayList;
import java.util.List;

/** Owns the collection of tasks and provides operations on that collection. */
public class TaskList {
    private final List<Task> tasks;

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /** Creates an empty task list. */
    public TaskList() {
        this(List.of());
    }

    /** Adds a task. */
    public void add(Task task) {
        tasks.add(task);
    }

    /** Removes and returns a task using its one-based number. */
    public Task remove(int taskNumber) {
        return tasks.remove(taskNumber - 1);
    }

    /** Returns the task using its one-based number. */
    public Task get(int taskNumber) {
        return tasks.get(taskNumber - 1);
    }

    /** Returns the number of tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view for saving and displaying tasks. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
