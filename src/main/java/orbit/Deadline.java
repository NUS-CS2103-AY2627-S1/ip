package orbit;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private final LocalDate by;

    /**
     * Creates a deadline with an ISO-formatted date.
     *
     * @param description Task description.
     * @param by Deadline date in {@code yyyy-mm-dd} format.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by);
    }

    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    @Override
    public boolean isDuplicateOf(Task other) {
        return other instanceof Deadline deadline && super.isDuplicateOf(other) && by.equals(deadline.by);
    }

    @Override
    public String toDataString() {
        return super.toDataString() + " | " + by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
