package nubish.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter SAVE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter PRINT_FORMAT = DateTimeFormatter.ofPattern("HHmm MMM d yyyy");

    private LocalDateTime deadline;

    /**
     * Creates an incomplete printDeadline task with the given description and printDeadline.
     *
     * @param description description of the printDeadline task
     * @param deadline printDeadline in {@code dd/MM/yyyy HHmm} format
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = convertToDateTime(deadline);
    }

    /**
     * Creates a printDeadline task with the given completion status, description, and printDeadline.
     *
     * @param isDone whether the printDeadline task has been completed
     * @param description description of the printDeadline task
     * @param deadline printDeadline in {@code dd/MM/yyyy HHmm} format
     */
    public Deadline(boolean isDone, String description, String deadline) {
        super(isDone, description);
        this.deadline = convertToDateTime(deadline);
    }

    /**
     * Returns deadline of task
     */
    public LocalDateTime getDeadline() {
        return this.deadline;
    }

    /**
     * Converts a saved printDeadline string into a {@link LocalDateTime}.
     *
     * @param dateTimeString date and time in {@code dd/MM/yyyy HHmm} format
     * @return parsed date and time
     */
    private LocalDateTime convertToDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, SAVE_FORMAT);
    }

    /**
     * Returns the user-facing representation of this printDeadline task.
     *
     * @return formatted printDeadline string with type, status, description, and printDeadline
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.deadline.format(PRINT_FORMAT));
    }

    /**
     * Returns the storage representation of this printDeadline task.
     *
     * @return formatted printDeadline string suitable for saving to disk
     */
    @Override
    public String saveString() {
        return String.format("D | %s | %s", super.saveString(), this.deadline.format(SAVE_FORMAT));
    }
}
