package duke;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** A task that reminds the user at a specified date and time. */
public class Reminder extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    private final LocalDateTime remindAt;

    /** Creates a reminder task. */
    public Reminder(String description, LocalDateTime remindAt) {
        super(description, TaskType.REMINDER);
        this.remindAt = remindAt;
    }

    /** Returns the reminder task type icon. */
    @Override
    public String getTypeIcon() {
        return "R";
    }

    /** Returns the description and formatted reminder time. */
    @Override
    public String getDisplayText() {
        return getDescription() + " (at: " + remindAt.format(DISPLAY_FORMAT) + ")";
    }

    /** Serializes this reminder for persistent storage. */
    @Override
    public String toSaveString() {
        return "R | " + getSaveStatus() + " | " + getDescription() + " | " + remindAt;
    }
}
