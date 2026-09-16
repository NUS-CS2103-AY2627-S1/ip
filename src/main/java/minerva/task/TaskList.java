package minerva.task;

import java.util.ArrayList;

/**
 * Stores the tasks managed by Minerva and provides basic task operations.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list backed by the specified task collection.
     * Basically a copy of task list for deque purposes
     * @param tasks existing tasks to store
     */
    public TaskList(ArrayList<Task> tasks) {
        assert tasks != null : "Task list must not be null";
        this.tasks = tasks;
    }

    /**
     * Creates an independent copy of the specified task list.
     *
     * @param other task list to copy
     */
    public TaskList(TaskList other) {
        assert other != null : "Task list to copy must not be null";
        this.tasks = new ArrayList<>();
        for (Task task : other.tasks) {
            Task copy;
            if (task instanceof Deadline deadline) {
                copy = new Deadline(deadline.description, deadline.by.toString());
            } else if (task instanceof Event event) {
                copy = new Event(event.description, event.from.toString(), event.to);
            } else if (task instanceof ToDo) {
                copy = new ToDo(task.description);
            } else {
                copy = new Task(task.description);
            }
            if (task.isDone) {
                copy.markDone();
            }
            this.tasks.add(copy);
        }
    }

    /**
     * Returns the collection containing the tasks.
     *
     * @return collection containing the tasks
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return number of tasks in the list
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Returns the task at the specified index.
     *
     * @param index index of the task to return
     * @return task at the specified index
     */
    public Task get(int index) {
        validateIndex(index);
        return tasks.get(index);
    }

    /**
     * Adds a task to the list.
     *
     * @param task task to add
     */
    public void add(Task task) {
        assert task != null : "Task list cannot contain null tasks";
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified index.
     *
     * @param index index of the task to remove
     * @return removed task
     */
    public Task delete(int index) {
        validateIndex(index);
        return tasks.remove(index);
    }

    /**
     * Marks the task at the specified index as complete.
     *
     * @param index index of the task to mark
     */
    public void mark(int index) {
        validateIndex(index);
        tasks.get(index).markDone();
    }
    /**
     * Marks the task at the specified index as incomplete.
     *
     * @param index index of the task to unmark
     */
    public void unmark(int index) {
        validateIndex(index);
        tasks.get(index).markUnDone();
    }
    /**
     * Checks that a task index refers to an existing task.
     *
     * @param index index to validate
     */
    private void validateIndex(int index) {
        if (index < 0 || index >= tasks.size()) {
            throw new IndexOutOfBoundsException("Task index must be within the task list");
        }
    }
}
