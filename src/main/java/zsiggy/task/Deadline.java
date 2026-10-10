package zsiggy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that must be completed by a specific date.
 */
public class Deadline extends Task {
    private final LocalDate deadline;

    /**
     * Creates a deadline task.
     *
     * @param description The description of the task.
     * @param deadline The deadline in yyyy-MM-dd format.
     */
    public Deadline(String description, String deadline) {
        super(description);

        this.deadline = LocalDate.parse(
                deadline,
                DateTimeFormatter.ISO_LOCAL_DATE
        );
    }

    /**
     * Returns the deadline in storage format.
     *
     * @return The deadline in yyyy-MM-dd format.
     */
    public String getDeadline() {
        return this.deadline.format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
        );
    }

    /**
     * Returns the formatted textual representation of the deadline.
     *
     * @return The formatted deadline task.
     */
    @Override
    public String toString() {
        return "[D]" + super.toString()
                + " (by: "
                + this.deadline.format(
                DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH)
        )
                + ")";
    }
}
