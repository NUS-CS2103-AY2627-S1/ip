package bott.task;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task with a description, a type, and a done/not-done status.
 */
public class Task {

    /** Format used to display a date to the user, e.g. "Oct 15 2019". */
    protected static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates a new task with the given description and type. The task
     * starts out not done.
     *
     * @param description Description of the task.
     * @param type Type of the task.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the icon representing this task's status: "X" if done, or a
     * blank space if not done.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns this task's textual representation, e.g. "[T][X] read book".
     */
    @Override
    public String toString() {
        return "[" + type.getIcon() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns this task's representation for saving to file, e.g. "T | 1 | read book".
     */
    public String toFileFormat() {
        return type.getIcon() + " | " + (isDone ? "1" : "0") + " | " + description;
    }

    /**
     * Returns whether this task's description contains the given keyword, ignoring case.
     *
     * @param keyword Keyword to search for.
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }
}
