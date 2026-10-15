package skynet;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a deadline with a description and due by date.
 */
public class Deadline extends Task {
    private final LocalDateTime by;

    /**
     * Creates a deadline task.
     *
     * @param description the description of the deadline
     * @param by the date and time by which the task should be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        if (by == null) {
            throw new IllegalArgumentException("Deadline date and time are required.");
        }
        this.by = by;
    }

    /**
     * Gets the due date and time of the deadline.
     *
     * @return due date and time as a LocalDateTime
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Gets the copy of the task.
     *
     * @return the copied task
     */
    @Override
    public Task copy() {
        Deadline copy = new Deadline(getDescription(), getBy());
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    /**
     * Returns a string representation of the deadline.
     *
     * @return the deadline represented as a string
     */
    @Override
    public String toString() {
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern("hh:mm", Locale.ENGLISH);

        String period = by.getHour() < 12 ? "am" : "pm";

        String formattedDateTime = by.format(dateFormatter)
                + ", "
                + by.format(timeFormatter)
                + " "
                + period;

        return "[D][" + getStatusIcon() + "] " + getDescription()
                + " (by: " + formattedDateTime + ")";
    }

}
