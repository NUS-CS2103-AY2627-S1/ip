package dukey.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dukey.exception.DukeyException;

/**
 * Represents a task that must be completed by a specific date/time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    protected LocalDateTime by;

    /**
     * Creates a deadline task with the given description and due date/time.
     *
     * @param description description of the deadline task.
     * @param by date/time by which the task should be completed.
     * @throws DukeyException if the due date/time is in the past.
     */
    public Deadline(String description, String by) throws DukeyException {
        super(description);
        this.by = LocalDateTime.parse(by, STORAGE_DATE_TIME_FORMAT);

        if (this.by.isBefore(LocalDateTime.now())) {
            throw new DukeyException("Deadline date/time cannot be in the past.");
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatForDisplay(this.by) + ")";
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return this.by.toLocalDate().equals(date);
    }

    @Override
    public String toFileString() {
        return String.format("D | %d | %s | %s |%s", this.isDone ? 1 : 0, this.description,
                this.by.format(STORAGE_DATE_TIME_FORMAT), getDoneAtStorageText());
    }

    private String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(OUTPUT_FORMAT)
                .replace("AM", "am")
                .replace("PM", "pm");
    }
}
