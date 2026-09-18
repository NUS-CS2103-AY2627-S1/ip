package sandrone.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specified time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu h:mma", Locale.US);

    protected LocalDateTime by;

    /**
     * Creates a deadline with its description and due date and time.
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns whether this deadline is due on the given date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    /**
     * Returns this deadline in the format used for display.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /**
     * Returns this deadline in the format used in the save file.
     */
    @Override
    public String toFileFormat() {
        return "D | " + super.toFileFormat() + " | " + by.format(DISPLAY_FORMAT);
    }
}
