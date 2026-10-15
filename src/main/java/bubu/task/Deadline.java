package bubu.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final String DATE_TIME_PATTERN = "MMM dd yyyy, h:mma";
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.ENGLISH);
    private static final String TO_STRING_FORMAT = "[D]%s (by: %s)";
    /** Date and time by which the task is due. */
    private LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param description task description.
     * @param deadline date and time by which the task is due.
     */
    public Deadline(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
    }

    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    @Override
    public Optional<LocalDateTime> getReminderTime() {
        return Optional.of(deadline);
    }

    @Override
    public String toString() {
        return String.format(TO_STRING_FORMAT, super.toString(), this.deadline.format(DISPLAY_FORMAT));
    }
}
