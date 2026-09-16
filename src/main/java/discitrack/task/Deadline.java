package discitrack.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a task that must be done by a specific date.
 */
public class Deadline extends Task {
    private LocalDate time;

    /**
     * Creates a deadline task with the given activity and due date.
     *
     * @param activity the activity represented by the deadline.
     * @param time the due date of the deadline.
     */
    public Deadline(String activity, LocalDate time) {
        super(activity);
        this.time = time;
    }

    /**
     * Returns the due date of this deadline.
     *
     * @return the due date.
     */
    public LocalDate getTime() {
        return this.time;
    }

    /**
     * Returns the display text for this deadline task.
     *
     * @return the deadline task formatted for display.
     */
    @Override
    public String toString() {
        String date = DateTimeFormatter.ofPattern("MMM dd yyyy").format(time);
        return String.format("[D] %s (by: %s)", formatTaskBody(), date) + formatTags();
    }
}
