package margit.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Represents a task with a description and completion status. */
public class Task {
    protected static final String SAVE_FIELD_SEPARATOR = " | ";
    private static final String TODO_TYPE = "T";
    private static final String DONE_MARKER = "1";
    private static final String NOT_DONE_MARKER = "0";

    private final String description;
    private boolean isDone;

    /** Creates an incomplete task with the given description. */
    public Task(String description) {
        this.description = description;
    }

    /** Marks this task as complete, returning whether its status changed. */
    public boolean mark() {
        if (isDone) {
            return false;
        }
        isDone = true;
        return true;
    }

    /** Marks this task as incomplete, returning whether its status changed. */
    public boolean unmark() {
        if (!isDone) {
            return false;
        }
        isDone = false;
        return true;
    }

    /** Returns the completion icon displayed with this task. */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /** Returns the representation used when this task is saved. */
    public String toSaveFormat() {
        return TODO_TYPE + SAVE_FIELD_SEPARATOR + getSaveStatusAndDescription();
    }

    /** Returns the save data shared by every task type, excluding its type marker. */
    protected String getSaveStatusAndDescription() {
        String statusMarker = isDone ? DONE_MARKER : NOT_DONE_MARKER;
        return statusMarker + SAVE_FIELD_SEPARATOR + description;
    }

    /** Returns whether this task's description contains {@code keyword}. */
    public boolean hasKeyword(String keyword) {
        return description.contains(keyword);
    }

    /** Returns whether this task falls on {@code date}. */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /** Returns the time used to sort this task, or {@code null} when it is not scheduled. */
    public LocalDateTime getScheduledDateTime() {
        return null;
    }

    /** Returns the user-facing status and description of this task. */
    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
