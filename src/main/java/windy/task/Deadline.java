package windy.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import windy.exception.InvalidInputFormatException;
import windy.storage.TaskDataFormat;
import windy.storage.TaskDataFormat.TaskType;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate deadline;

    /**
     * Creates a task that must be completed by a deadline.
     *
     * @param description the task description
     * @param isDone whether the task has been completed
     * @param deadline the deadline in {@code yyyy-M-d} format
     * @throws InvalidInputFormatException if the deadline is not a valid date
     */
    public Deadline(String description, boolean isDone, String deadline) throws InvalidInputFormatException {
        super(description, isDone);
        try {
            this.deadline = TaskDateParser.parse(deadline);
        } catch (DateTimeParseException exception) {
            throw new InvalidInputFormatException(TaskDateParser.INVALID_DATE_MESSAGE);
        }
    }

    /** Copies a task whose dates have already been validated. */
    private Deadline(Deadline source) {
        super(source.getDescription(), source.isDone());
        this.deadline = source.deadline;
    }

    /** {@inheritDoc} */
    @Override
    public Task copy() {
        return new Deadline(this);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[D][" + this.getStatusIcon() + "] " + this.getDescription()
                + " (by: " + this.deadline.format(OUTPUT_FORMATTER) + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toDataString() {
        return TaskDataFormat.formatRecord(
                TaskType.DEADLINE, this.isDone(), this.getDescription(), this.deadline.toString());
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOnDate(LocalDate date) {
        return !date.isAfter(this.deadline) && !isDone();
    }
}
