package duke;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * A task that must be completed by a specified date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /**
     * Creates a deadline with its description and completion date.
     *
     * @param description Task text.
     * @param by Completion date.
     */
    public Deadline(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /**
     * Returns the date by which this task should be completed.
     *
     * @return The completion date.
     */
    public LocalDate getBy() {
        return by;
    }

    @Override
    protected String getDetails() {
        return " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }
}
