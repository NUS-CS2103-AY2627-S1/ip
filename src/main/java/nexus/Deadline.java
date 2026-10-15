package nexus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** A task that must be completed before a specified date or time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate by;

    /** Creates a deadline task with its description and due date. */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.DEADLINE;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
