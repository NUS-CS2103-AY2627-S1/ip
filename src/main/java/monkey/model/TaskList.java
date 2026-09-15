package monkey.model;

import java.util.ArrayList;
import java.util.List;

/** Owns Monkey's tasks and provides the operations used by the application. */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /** Creates a task list containing the supplied tasks. */
    public TaskList(List<Task> tasks) {
        // The storage layer returns a list, so a null list indicates a broken caller contract.
        assert tasks != null : "Task list source must not be null";
        this.tasks = new ArrayList<>(tasks);
    }

    /** Returns whether this list contains no tasks. */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at the given zero-based index. */
    public Task get(int index) {
        assertValidIndex(index);
        return tasks.get(index);
    }

    /** Adds a task to this list. */
    public void add(Task task) {
        // A null entry cannot be displayed, marked, deleted, or persisted as a task.
        assert task != null : "Task list must not contain null tasks";
        tasks.add(task);
    }

    /** Returns whether an equivalent task is already present. */
    public boolean containsEquivalent(Task candidate) {
        return tasks.stream().anyMatch(task -> task.hasSameDetails(candidate));
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task remove(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /** Marks the task at the given zero-based index as done. */
    public void mark(int index) {
        assertValidIndex(index);
        tasks.get(index).markAsDone();
    }

    /** Marks the task at the given zero-based index as not done. */
    public void unmark(int index) {
        assertValidIndex(index);
        tasks.get(index).markAsNotDone();
    }

    /** Removes and returns the task at the given zero-based index. */
    public Task delete(int index) {
        assertValidIndex(index);
        return tasks.remove(index);
    }

    /** Returns a snapshot suitable for persistence. */
    public List<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /** Verifies the index precondition shared by task-list operations. */
    private void assertValidIndex(int index) {
        // Command classes validate user input before calling these zero-based operations.
        assert index >= 0 && index < tasks.size() : "Task index must refer to an existing task";
    }
}
