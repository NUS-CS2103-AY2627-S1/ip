package echo.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A single to-do item tracked by Echo. Holds a description, a task type
 * (TODO/DEADLINE/EVENT) and whether the task has been marked as done.
 */
public class Task {
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    /**
     * Field separator used when rendering a task to its save-file line.
     * Storage#parseLine splits on this same constant when reading it back,
     * so the writer and reader can never disagree on the separator.
     */
    public static final String FILE_FORMAT_SEPARATOR = " | ";

    private final String description;
    private final TaskType type;
    private boolean isDone;

    /**
     * Creates a Task with the given description and type, initially not
     * done.
     *
     * @param description What needs to be done.
     * @param type Whether this is a TODO/DEADLINE/EVENT.
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /** Returns "X" if this task is done, or a blank space otherwise. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns this task's display line, e.g. "[T][ ] read book". */
    @Override
    public String toString() {
        return "[" + type.getTag() + "][" + getStatusIcon() + "] " + description;
    }

    /**
     * Renders this task as a single line for the save file, e.g.
     * {@code T | 1 | read book}. Subclasses append their own extra fields
     * after calling this via super, mirroring the toString() pattern.
     */
    public String toFileFormat() {
        return type.getTag() + FILE_FORMAT_SEPARATOR + (isDone ? "1" : "0")
                + FILE_FORMAT_SEPARATOR + description;
    }

    /**
     * Returns whether this task occurs on the given date. A plain Task
     * (Todo) is never date-specific; Deadline/Event override this.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given
     * keyword (case-insensitive).
     */
    public boolean matchesKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns whether this task should be treated as a duplicate of the
     * given task: the same concrete task type and the same description,
     * ignoring case and surrounding whitespace. Deadline and Event
     * override this to also require their date(s) to match.
     *
     * @param other The task to compare against.
     * @return True if the two tasks represent the same thing to a user.
     */
    public boolean isDuplicateOf(Task other) {
        return this.getClass() == other.getClass()
                && this.description.trim().equalsIgnoreCase(other.description.trim());
    }
}
