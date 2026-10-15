package hse.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specified date and time. */
public class Deadline extends Task {
    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    public static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu h:mm a", Locale.ENGLISH);
    private LocalDateTime deadline;

    /** Creates a deadline with the supplied description and due date-time. */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    /** Returns whether this deadline falls on the supplied date. */
    public boolean isOn(LocalDate date) {
        return deadline.toLocalDate().equals(date);
    }

    @Override
    LocalDateTime getSortDateTime() {
        return deadline;
    }

    /** Returns this deadline in the standard deadline display format. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline.format(DATE_TIME_FORMAT) + ")";
    }
}
