package alice.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description The task description.
     * @param by The deadline date.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline date. */
    public LocalDate getBy() {
        return by;
    }

    /** Creates the first deadline occurrence scheduled after the supplied date and time. */
    @Override
    public Task createNextOccurrence(LocalDateTime currentDateTime) {
        LocalDate nextDate = advanceDate(by);
        while (!nextDate.isAfter(currentDateTime.toLocalDate())) {
            nextDate = advanceDate(nextDate);
        }
        Deadline nextDeadline = new Deadline(getDescription(), nextDate);
        nextDeadline.setRecurrence(getRecurrence());
        return nextDeadline;
    }

    /** Advances the supplied date by this task's recurrence interval. */
    private LocalDate advanceDate(LocalDate date) {
        return switch (getRecurrence()) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> date.plusMonths(1);
            case YEARLY -> date.plusYears(1);
        };
    }

    /**
     * Returns this deadline task in Alice's display format.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        String recurrenceText = isRecurring() ? " (repeats: " + getRecurrence().getDisplayName() + ")" : "";
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")" + recurrenceText;
    }
}
