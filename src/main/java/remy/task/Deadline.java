package remy.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {

    /** Format used when displaying date-only deadlines. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    /** Format used when displaying deadlines that include a time. */
    private static final DateTimeFormatter DATE_TIME_DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu HH:mm", Locale.ENGLISH);

    /** Date-only deadline, when the deadline has no time. */
    private final LocalDate byDate;

    /** Date-time deadline, when the deadline includes a time. */
    private final LocalDateTime byDateTime;

    /**
     * Creates a deadline with a date-only endpoint.
     *
     * @param description Description supplied by the user.
     * @param byDate Date on which the task is due.
     */
    public Deadline(String description, LocalDate byDate) {
        this(description, byDate, null);
    }

    /**
     * Creates a deadline with a date-time endpoint.
     *
     * @param description Description supplied by the user.
     * @param byDateTime Date and time at which the task is due.
     */
    public Deadline(String description, LocalDateTime byDateTime) {
        this(description, null, byDateTime);
    }

    /**
     * Creates a deadline with its available endpoint representation.
     *
     * @param description Description supplied by the user.
     * @param byDate Date on which the task is due, if known.
     * @param byDateTime Date and time at which the task is due, if known.
     */
    public Deadline(String description, LocalDate byDate, LocalDateTime byDateTime) {
        super(description);
        assert (byDate == null) != (byDateTime == null)
                : "Deadline must have exactly one endpoint representation";

        this.byDate = byDate;
        this.byDateTime = byDateTime;
    }

    /**
     * Returns whether another task is a deadline with the same description and endpoint.
     *
     * @param otherTask task to compare with
     * @return true when both deadlines have the same identifying details
     */
    @Override
    public boolean hasSameDetailsAs(Task otherTask) {
        if (!(otherTask instanceof Deadline otherDeadline)) {
            return false;
        }
        return super.hasSameDetailsAs(otherDeadline)
                && Objects.equals(byDate, otherDeadline.byDate)
                && Objects.equals(byDateTime, otherDeadline.byDateTime);
    }

    /**
     * Returns this deadline's due endpoint as a date-time for chronological sorting.
     *
     * @return due date-time, with a date-only endpoint represented as the start of its day
     */
    @Override
    Optional<LocalDateTime> getChronologicalDateTime() {
        if (byDateTime != null) {
            return Optional.of(byDateTime);
        }
        return Optional.of(byDate.atStartOfDay());
    }

    /**
     * Returns the string representation of the Deadline task
     *
     * @return A string representing the task
     */
    @Override
    public String toString() {
        String display = "[D]" + super.toString() + " (by: ";

        if (byDateTime != null) {
            display += byDateTime.format(DATE_TIME_DISPLAY_FORMATTER);
        } else if (byDate != null) {
            display += byDate.format(DATE_DISPLAY_FORMATTER);
        }

        return display + ')';
    }
}
