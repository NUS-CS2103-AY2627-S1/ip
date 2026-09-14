package morgan.task;

import java.time.LocalDate;

/**
 * Represents a genetic task in the Morgan application.
 * Serves as a base class for specific task types like ToDo, Deadline and Event.
 */
public class Task {
    protected String name;
    protected boolean isDone;

    /**
     * Constructs a Task instance with specified name.
     *
     * @param name The textual description of the task.
     */
    public Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Checks if a task happens on the date.
     *
     * @param date A date entered by the user.
     * @return True if the task is happened on that dates, otherwise false.
     */
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    /**
     * Marks the task as done.
     */
    public void mark() {
        this.isDone = true;
    }

    /**
     * Marks the task as undone.
     */
    public void unmark() {
        this.isDone = false;
    }

    /**
     * Checks if the task is done.
     *
     * @return True if the task is done, otherwise false.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Gets the name of the task.
     *
     * @return The name of the task.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String mark = isDone ? "X" : " ";
        return String.format("[%s] %s", mark, name);
    }
}
