package luigibot.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu, h:mm a", Locale.ENGLISH);

    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline with the given description and due date-time.
     *
     * @param description description of the deadline.
     * @param by date and time by which the deadline must be completed, in yyyy-MM-dd HHmm format.
     * @throws DateTimeParseException if {@code by} is not a valid date and time in the required format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDateTime.parse(by, INPUT_FORMAT);
    }

    /**
     * Returns the deadline type icon, task details, and due time.
     *
     * @return display form of this deadline.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + this.by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the text format used by the save file.
     *
     * @return save-file representation of this deadline.
     */
    @Override
    public String toFileString() {
        return getFileString("D") + " | " + this.by.format(INPUT_FORMAT);
    }

    /**
     * Returns whether this deadline is due on the given date.
     *
     * @param date date to check.
     * @return true when the deadline is due on the given date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.toLocalDate().equals(date);
    }
}
