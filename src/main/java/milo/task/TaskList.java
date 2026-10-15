package milo.task;

import java.util.ArrayList;
import java.util.List;

/** Owns Milo's tasks and provides basic collection operations. */
public class TaskList {
    /** Mutable collection of tasks in their display order. */
    private final ArrayList<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>(100);
    }

    /** Creates a task list initialized with a copy of the supplied tasks. */
    public TaskList(List<Task> tasks) {
        // Callers must supply a collection, using an empty list when there are no tasks.
        assert tasks != null : "The initial task collection must not be null";
        this.tasks = new ArrayList<>(tasks);
        // Every entry must support task operations and immutable snapshot creation.
        assert this.tasks.stream().allMatch(task -> task != null) : "Task lists must not contain null entries";
    }

    /** Adds a task to the end of this list. */
    public void add(Task task) {
        // Preserve the same non-null entry invariant when the collection grows.
        assert task != null : "Only non-null tasks may be added";
        tasks.add(task);
    }

    /** Returns the task at the zero-based index. */
    public Task get(int index) {
        return tasks.get(index);
    }

    /** Replaces a task without changing its position or the list size. */
    public void set(int index, Task task) {
        assert task != null : "Only non-null tasks may be stored";
        tasks.set(index, task);
    }

    /** Removes the task at the zero-based index. */
    public void remove(int index) {
        tasks.remove(index);
    }

    /** Removes all tasks from this list. */
    public void clear() {
        tasks.clear();
    }

    /** Returns the number of tasks in this list. */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks whose descriptions contain the keyword, ignoring case.
     *
     * @param keyword text to search for
     * @return matching tasks in their original order
     */
    public TaskList find(String keyword) {
        TaskList searchResults = new TaskList();

        for (Task task : this.tasks) {
            if (task.containsKeyword(keyword)) {
                searchResults.add(task);
            }
        }

        return searchResults;
    }

    /** Returns an immutable snapshot of the tasks for reading. */
    public List<Task> asList() {
        return List.copyOf(tasks);
    }
}
