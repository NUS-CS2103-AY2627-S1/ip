package nubish.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import nubish.utils.NubishException;

/**
 * Represents a task that happens between a start and end date and time.
 */
public class Event extends Task {
    private static final DateTimeFormatter SAVE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter PRINT_FORMAT = DateTimeFormatter.ofPattern("HHmm MMM d yyyy");

    private LocalDateTime from;
    private LocalDateTime to;

    /**
     * Creates an incomplete printEvent task with the given description and time range.
     *
     * @param description description of the printEvent task
     * @param from start date and time in {@code dd/MM/yyyy HHmm} format
     * @param to end date and time in {@code dd/MM/yyyy HHmm} format
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = convertToDateTime(from);
        this.to = convertToDateTime(to);
        validateDateRange();
    }

    /**
     * Creates an printEvent task with the given completion status, description, and time range.
     *
     * @param isDone whether the printEvent task has been completed
     * @param description description of the printEvent task
     * @param from start date and time in {@code dd/MM/yyyy HHmm} format
     * @param to end date and time in {@code dd/MM/yyyy HHmm} format
     */
    public Event(boolean isDone, String description, String from, String to) {
        super(isDone, description);
        this.from = convertToDateTime(from);
        this.to = convertToDateTime(to);
        validateDateRange();
    }

    /**
     * Converts a saved printEvent date-time string into a {@link LocalDateTime}.
     *
     * @param dateTimeString date and time in {@code dd/MM/yyyy HHmm} format
     * @return parsed date and time
     */
    public LocalDateTime convertToDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, SAVE_FORMAT);
    }

    private void validateDateRange() {
        if (!this.from.isBefore(this.to)) {
            throw new NubishException("Hrmmm... Start times must be earlier than end times.");
        }
    }

    /**
     * Returns the user-facing representation of this printEvent task.
     *
     * @return formatted printEvent string with type, status, description, and time range
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), this.from.format(PRINT_FORMAT),
                this.to.format(PRINT_FORMAT));
    }
    /**
     * Returns the storage representation of this printEvent task.
     *
     * @return formatted printEvent string suitable for saving to disk
     */
    @Override
    public String saveString() {
        return String.format("E | %s | %s | %s", super.saveString(), this.from.format(SAVE_FORMAT),
                this.to.format(SAVE_FORMAT));
    }
}
