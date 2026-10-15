package gnaix;

import gnaix.task.Task;

/**
 * Holds a task together with the one-based number shown to users.
 */
public class TaskDisplay {
    private final int number;
    private final Task task;

    /**
     * Creates task display data for the GUI.
     *
     * @param number One-based task number.
     * @param task Task to display.
     */
    public TaskDisplay(int number, Task task) {
        this.number = number;
        this.task = task;
    }

    public int getNumber() {
        return number;
    }

    public Task getTask() {
        return task;
    }
}
