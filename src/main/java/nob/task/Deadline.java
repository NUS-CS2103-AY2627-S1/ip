package nob.task;

import java.time.LocalDateTime;

import nob.util.DateTimeUtil;

/**
 * Represents a task that should be completed by a specified time.
 */
public class Deadline extends Task {
    /** The parsed deadline date and time supplied by the user, when it can be parsed. */
    private final LocalDateTime byDateTime;

    /** Original deadline text used for display fallback when the value is not a recognised date/time. */
    private final String byText;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Text describing the task.
     * @param by The date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        assert by != null : "Deadline times must not be null";
        assert !by.isBlank() : "Deadline times must not be blank after parsing";
        this.byDateTime = DateTimeUtil.parseDateTime(by).orElse(null);
        this.byText = by == null ? "" : by.trim();
    }

    /**
     * Returns this deadline in the format used when displaying a task list.
     *
     * @return the deadline, task description, and completion status
     */
    @Override
    public String toString() {
        String deadline = byDateTime == null ? byText : DateTimeUtil.formatDisplay(byDateTime);
        return "[D: " + deadline + "] " + super.toString();
    }
}
