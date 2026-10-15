package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /** The user-provided deadline text. */
    private final LocalDateTime by;

    /** Creates a deadline task. */
    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the deadline task type icon. */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /** Returns the description and formatted deadline. */
    @Override
    public String getDisplayText() {
        return getDescription() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }

    /** Serializes this deadline for persistent storage. */
    @Override
    public String toSaveString() {
        return "D | " + getSaveStatus() + " | " + getDescription() + " | " + by;
    }
}
