package heisenberg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a specific date and time. */
public class Deadline extends Task {
    private final LocalDateTime deadlineDateTime;

    public Deadline(String description, LocalDateTime deadlineDateTime) {
        super(description);
        this.deadlineDateTime = deadlineDateTime;
    }

    public LocalDateTime getDeadlineDateTime() {
        return deadlineDateTime;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + deadlineDateTime.format(
                        DateTimeFormatter.ofPattern("MMM dd yyyy HH:mm", Locale.ENGLISH)) + ")";
    }
}
