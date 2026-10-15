package minty.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that must be completed by a given date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates an incomplete deadline.
     *
     * @param description description of the task.
     * @param by date by which the task should be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline in the format used by the save file.
     *
     * @return serialized deadline.
     */
    @Override
    public String toDataString() {
        return "D | " + (isDone ? "1" : "0") + " | " + escapeDataField(description)
                + " | " + by;
    }

    /**
     * Checks whether this deadline is due on the specified date.
     *
     * @param date date to check.
     * @return {@code true} if this deadline is due on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public Optional<LocalDate> getReminderDate() {
        return Optional.of(by);
    }

    /**
     * Returns the deadline in Minty's display format.
     *
     * @return task status and description followed by the due date.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
