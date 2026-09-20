package nexus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** A task to be completed by a stated time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd uuuu");
    private final LocalDate by;

    /**
     * Creates a deadline task.
     *
     * @param description the task description.
     * @param by the deadline text.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = LocalDate.parse(by);
    }

    /** Returns the deadline text. */
    public String getBy() {
        return by.toString();
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
