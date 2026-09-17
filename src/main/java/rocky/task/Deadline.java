package rocky.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import rocky.RockyException;

/** A task with a single due date, e.g. "return book (by: Dec 01 2019)". */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy");

    private LocalDate date;

    /**
     * Creates a deadline, initially not marked done.
     *
     * @param description what the task is, e.g. "return book".
     * @param date the due date, expected in ISO format (yyyy-mm-dd, e.g. 2019-10-15).
     * @throws RockyException if {@code date} isn't in ISO format.
     */
    public Deadline(String description, String date) throws RockyException {
        super(description);
        try {
            this.date = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new RockyException(
                    "Please give the date as yyyy-mm-dd, e.g. 2019-10-15.");
        }
    }

    /**
     * Returns the due date in ISO format (yyyy-mm-dd), used when saving to disk.
     *
     * @return the due date as an ISO-8601 string, e.g. "2019-12-01".
     */
    public String getDate() {
        return this.date.toString();
    }

    /** Returns the "[D]" type icon, the checkbox/description, and the formatted due date. */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + this.date.format(OUTPUT_FORMAT) + ")";
    }
}
