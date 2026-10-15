package samantha.model;

import java.time.LocalDate;

import samantha.exception.InputException;
import samantha.exception.TaskValidationException;

/** A task that must be completed by a specified date or deadline value. */
public class Deadline extends Task {

    private final DateTimeValue deadline;

    /**
     * Creates a deadline task by parsing the supplied deadline value.
     *
     * @param name task description
     * @param deadline raw value after {@code /by}
     * @throws TaskValidationException if the task description is blank
     * @throws InputException if the deadline value is invalid
     */
    public Deadline(String name, String deadline)
            throws TaskValidationException, InputException {
        super(name, "deadline");
        this.deadline = DateTimeValue.parse(deadline);
    }

    /**
     * Returns the display marker for a deadline task.
     *
     * @return the deadline marker
     */
    @Override
    public String getType() {
        return "[D]";
    }

    /**
     * Returns whether this deadline falls on the supplied date.
     *
     * @param date date to compare with the deadline
     * @return {@code true} when the deadline is on {@code date}
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return deadline.getDate().equals(date);
    }

    /**
     * Returns this deadline in the user-facing task format.
     *
     * @return formatted deadline task
     */
    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), this.deadline);
    }

    /**
     * Returns this deadline as one record for the task data file.
     *
     * @return serialized deadline task
     */
    @Override
    public String toFileString() {
        return String.format("D | %d | %s | %s", getCompletionStatusCode(), getTaskName(),
                deadline.toStorageString());
    }
}
