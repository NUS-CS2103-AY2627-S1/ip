package verity.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that is due on a specific date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate dueDate;

    /**
     * Creates an incomplete deadline with the specified description and due date.
     *
     * @param description Description of the deadline.
     * @param dueDate Date on which the deadline is due.
     */
    public Deadline(String description, LocalDate dueDate) {
        assert dueDate != null : "Deadline date must not be null.";

        super(description);
        this.dueDate = dueDate;
    }

    public LocalDate getBy() {
        return dueDate;
    }

    @Override
    public String getStatus() {
        return "[D]" + super.getStatus()
                + " (by: " + dueDate.format(DISPLAY_DATE_FORMAT) + ")";
    }

    /**
     * Returns a line representing this deadline in the data file.
     *
     * @return Tab-separated deadline data.
     */
    @Override
    public String serialize() {
        return appendClientMetadata(String.join(
                "\t", "D", getStorageStatus(), getDescription(), dueDate.toString()));
    }

    /**
     * Returns whether this deadline is due on the specified date.
     *
     * @param date Date to check.
     * @return True if the deadline is due on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDate.equals(date);
    }
}
