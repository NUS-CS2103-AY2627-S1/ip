package monday.task;

import java.time.LocalDate;
import java.time.LocalTime;

import monday.util.DateTimeFormat;

/**
 * Represents a task that must be completed by a specific date and optional time.
 */
public class Deadline extends Task {
    private final LocalDate deadlineDate;
    private final LocalTime deadlineTime;

    /**
     * Creates an incomplete deadline task with the given details.
     */
    public Deadline(String description, LocalDate deadlineDate, LocalTime deadlineTime) {
        this(description, false, deadlineDate, deadlineTime, "");
    }

    /**
     * Creates an incomplete deadline task with the given details and notes.
     */
    public Deadline(String description, LocalDate deadlineDate, LocalTime deadlineTime, String notes) {
        this(description, false, deadlineDate, deadlineTime, notes);
    }

    /**
     * Creates a deadline task with the given details and completion status.
     */
    public Deadline(String description, boolean isDone, LocalDate deadlineDate, LocalTime deadlineTime) {
        this(description, isDone, deadlineDate, deadlineTime, "");
    }

    /**
     * Creates a deadline task with the given details, completion status, and notes.
     */
    public Deadline(String description, boolean isDone, LocalDate deadlineDate, LocalTime deadlineTime,
            String notes) {
        super(description, isDone, notes);
        this.deadlineDate = deadlineDate;
        this.deadlineTime = deadlineTime;
    }

    public LocalDate getDeadlineDate() {
        return deadlineDate;
    }

    public LocalTime getDeadlineTime() {
        return deadlineTime;
    }

    @Override
    public String toString() {
        String deadline = deadlineDate.format(DateTimeFormat.DISPLAY_DATE.getFormatter());

        if (deadlineTime != null) {
            deadline += " " + deadlineTime.format(DateTimeFormat.DISPLAY_TIME.getFormatter());
        }

        return "[D]" + formatTask() + " (by: " + deadline + ")" + formatNotes();
    }
}
