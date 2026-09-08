package crack.task;

import java.time.LocalDate;

/**
 * A single thing the user wants to get done.
 * Holds a description and whether it is finished. Subclasses add any dates.
 */
public class Task {
    /** What the user wants to get done. */
    protected final String description;

    /** Whether the task has been finished. */
    protected boolean isDone;

    /**
     * Creates a task that starts off not done.
     *
     * @param description What the user wants to get done.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks this task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task lands on the given day.
     * A plain task carries no date, so it never does.
     *
     * @param date The day being asked about.
     * @return True if this task falls on that day.
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given keyword.
     * The match is case sensitive.
     *
     * @param keyword The text being searched for.
     * @return True if the description contains it.
     */
    public boolean matches(String keyword) {
        return description.contains(keyword);
    }

    /**
     * Returns this task written the way the save file stores it.
     */
    public String toSaveFormat() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns "X" when the task is done and a blank otherwise.
     */
    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
