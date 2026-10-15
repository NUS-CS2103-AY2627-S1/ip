package charlie.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import charlie.command.UpdateField;
import charlie.exception.CharlieException;

/**
 * Represents a task that must be completed by a specific calendar date.
 */
public class Deadline extends Task {
    /** Date by which this task must be completed. */
    protected LocalDate deadline;

    /**
     * Creates a deadline task with its description, status, and due date.
     *
     * @param description Description of the task.
     * @param isDone Whether the task is completed.
     * @param deadline Date by which the task must be completed.
     * @throws CharlieException If the description or deadline is invalid.
     */
    public Deadline(String description, boolean isDone, LocalDate deadline) {
        super(description, isDone);
        if (deadline == null) {
            throw new CharlieException("Deadline cannot be empty.");
        }
        this.deadline = deadline;
    }

    /**
     * Creates a copy of this deadline with the requested completion status.
     *
     * @param isDone Completion status for the copy.
     * @return Copied deadline with the requested status.
     */
    @Override
    public Task copyWithStatus(boolean isDone) {
        return new Deadline(description, isDone, deadline);
    }

    /**
     * Returns whether another deadline has the same description and due date.
     *
     * @param other Task to compare with this deadline.
     * @return True when both deadlines contain the same identifying details.
     */
    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other)
                && deadline.equals(((Deadline) other).deadline);
    }

    /**
     * Returns the display representation of this deadline.
     *
     * @return Deadline type, completion status, description, and due date.
     */
    @Override
    public String toString() {
        String formattedDeadline = this.deadline.format(
                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return "[D]" + super.toString() + " (by: " + formattedDeadline + ")";
    }

    /**
     * Converts this deadline into the format used in the save file.
     *
     * @return Serialized deadline data.
     */
    @Override
    public String saveFileFormat() {
        String status = isDone ? "Done" : "Not done";
        return "D" + " | " + status + " | " + this.description + " | " + this.deadline;
    }

    /**
     * Creates a copy of this deadline with the requested field replaced.
     *
     * @param updateField Field to replace.
     * @param newValue Replacement value for the field.
     * @return Updated copy of this deadline.
     * @throws CharlieException If the field is unsupported or the replacement value is invalid.
     */
    @Override
    public Task createUpdatedTask(UpdateField updateField, String newValue) {
        return switch (updateField) {
            case DESCRIPTION -> createTaskWithDescription(newValue);
            case DEADLINE -> createTaskWithDeadline(newValue);
            case FROM, TO -> throw new CharlieException(
                    "A deadline does not have from or to fields.");
        };
    }

    /**
     * Creates a copy of this deadline with a replacement description.
     *
     * @param newValue Replacement description.
     * @return Updated deadline copy.
     */
    private Task createTaskWithDescription(String newValue) {
        return new Deadline(newValue, this.isDone, this.deadline);
    }

    /**
     * Creates a copy of this deadline with a replacement due date.
     *
     * @param newValue Replacement date in {@code yyyy-MM-dd} format.
     * @return Updated deadline copy.
     * @throws CharlieException If the replacement is not a valid date.
     */
    private Task createTaskWithDeadline(String newValue) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
        try {
            LocalDate newDeadline = LocalDate.parse(newValue, dateTimeFormatter);
            return new Deadline(this.description, this.isDone, newDeadline);
        } catch (DateTimeParseException e) {
            throw new CharlieException(
                    "Deadline date must use the yyyy-MM-dd format.");
        }
    }

}
