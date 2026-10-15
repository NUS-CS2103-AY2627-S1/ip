package wally;

import java.util.ArrayList;

/**
 * Stores tasks.
 */
public class Tasklist {
    private final ArrayList<Task> tasks;

    /**
     * Initialises the ArrayList that stores Tasks.
     */
    public Tasklist() {
        tasks = new ArrayList<>();
    }

    /**
     * Returns the task at the specified one-based position.
     *
     * @param pos One-based task position.
     * @return Task at the specified position.
     */
    public Task getTask(int pos) {
        assert isValidTaskPosition(pos) : "Task positions must be within the current task list";
        return tasks.get(pos - 1);
    }

    /**
     * Returns the number of tasks in this list.
     *
     * @return Number of tasks.
     */
    public int getSize() {
        return tasks.size();
    }

    /**
     * Adds a task to this list.
     *
     * @param task Task to add.
     * @throws DuplicateTaskException If a description matches ignoring case and surrounding spaces.
     */
    public void addTask(Task task) throws DuplicateTaskException {
        assert task != null : "A task list must not contain null tasks";
        for (Task existing : tasks) {
            if (existing.getDescription().trim().equalsIgnoreCase(task.getDescription().trim())) {
                throw new DuplicateTaskException();
            }
        }
        tasks.add(task);
    }

    /**
     * Removes the task at the specified one-based position.
     *
     * @param pos One-based task position.
     */
    public void removeTask(int pos) {
        assert isValidTaskPosition(pos) : "Task positions must be within the current task list";
        tasks.remove(pos - 1);
    }

    /**
     * Checks whether a one-based task position refers to a task in this list.
     *
     * @param pos One-based task position.
     * @return Whether the position is valid for the current list.
     */
    private boolean isValidTaskPosition(int pos) {
        return pos >= 1 && pos <= tasks.size();
    }
}
