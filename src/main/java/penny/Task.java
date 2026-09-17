package penny;

import java.time.LocalDateTime;

/**
 * Parent class for all tasks.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String icon = "";

    protected Task(String description) {
        this.description = description;
        this.isDone = false;
        this.icon = "";
    }

    /**
     * Mark a task as done.
     *
     * @throws PennyException if the task is already marked as done.
     */
    public void markAsDone() throws PennyException {
        if (isDone) {
            throw new PennyException("Task is already marked as done");
        }
        isDone = true;
        assert isDone : "Task should be marked done after markAsDone()";
    }

    /**
     * Mark a task as undone.
     *
     * @throws PennyException if the task is already marked as undone.
     */
    public void unmarkAsDone() throws PennyException {
        if (!isDone) {
            throw new PennyException("Task is already marked as undone");
        }
        isDone = false;
        assert !isDone : "Task should be marked undone after unmarkAsDone()";
    }

    /**
     * Returns boolean if the task is marked as done.
     *
     * @return boolean if the task is marked as done.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the task's description.
     *
     * @return the task's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns boolean if the task is due on dateTime.
     *
     * @param dateTime the date to check if the task is due on.
     * @return boolean if the task is due on dateTime.
     */
    public boolean isDueOn(LocalDateTime dateTime) {
        return false;
    }

    /**
     * Returns boolean on whether the task toString contains keyword.
     *
     * @param keyword string keyword to check for.
     * @return boolean on whether the task toString contains keyword.
     */
    public boolean hasKeyword(String keyword) {
        return toString().contains(keyword);
    }

    /**
     * Returns a string representation of the task.
     *
     * @return a string representation of the task.
     */
    @Override
    public String toString() {
        String doneIcon = isDone ? "X" : " ";
        return "[" + icon + "][" + doneIcon + "] " + description;
    }
}
