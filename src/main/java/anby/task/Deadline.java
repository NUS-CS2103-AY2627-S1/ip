package anby.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private LocalDate date;

    /**
     * Creates a deadline task with the given name and due date.
     *
     * @param name deadline description
     * @param date due date in yyyy-MM-dd format
     */
    public Deadline(String name, String date) {
        super(name);
        this.date = LocalDate.parse(date);
    }

    /**
     * Converts this deadline into the format used in the data file.
     *
     * @return file storage representation of this deadline
     */
    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + this.date;
    }

    /**
     * Returns the user-facing representation of this deadline.
     *
     * @return display string for this deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: " + this.date.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ")";
    }
}
