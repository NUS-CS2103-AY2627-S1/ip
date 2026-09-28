package kotha.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that can only be done after a specified time or task. */
public class Constraint extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu hh:mma");
    private final String after;
    private final LocalDateTime afterDate;

    /** Creates an incomplete constraint task with the supplied trigger. */
    public Constraint(String description, String after) {
        super(description);
        this.after = after;
        this.afterDate = null;
    }

    /** Creates an incomplete constraint task with the supplied date trigger. */
    public Constraint(String description, LocalDateTime afterDate) {
        super(description);
        this.after = null;
        this.afterDate = afterDate;
    }

    /** Returns the trigger text for persistent storage. */
    public String getAfter() {
        return after;
    }

    /** Returns the date-time trigger, or null when this is a text-triggered constraint. */
    public LocalDateTime getAfterDate() {
        return afterDate;
    }

    @Override
    public String toString() {
        String trigger = afterDate == null ? after : afterDate.format(DISPLAY_FORMAT);
        return "[C]" + super.toString() + " (after: " + trigger + ")";
    }
}
