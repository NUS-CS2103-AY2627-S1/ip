package jarvis.classes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Represents a task that must be completed by a specified deadline. */
public class Deadline extends Task {
    /** Deadline format for toString() to follow. */
    private static final String DATE_TIME_FORMATTER = "MM dd yyyy HH:mm";
    private static final String DATE_FORMATTER = "MM dd yyyy";
    /** Stores the deadline text. */
    private LocalDateTime deadline;

    /**
     * Creates a deadline task.
     *
     * @param taskName Description of the task.
     * @param deadline Deadline text.
     */
    public Deadline(String taskName, LocalDateTime deadline) {
        super(taskName);
        this.deadline = deadline;
    }


    /**
     * Formats and returns the deadline task in its display format.
     *
     * @return Formatted deadline task.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter;

        if (deadline.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        } else {
            formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMATTER);
        }

        String dateAndTime = String.format(" (by: %s)", deadline.format(formatter));
        return "[D]" + super.toString() + dateAndTime;
    }
}
