package sylveon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a specified date. */
public class Deadline extends Task {
    private LocalDate by;

    /** Creates a deadline task. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /** Returns the deadline date. */
    public LocalDate getBy() {
        return by;
    }

    /** Returns the display representation of this deadline task. */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy");
        return "[D]" + super.toString()
                + " (by: " + by.format(formatter) + ")";
    }

}
