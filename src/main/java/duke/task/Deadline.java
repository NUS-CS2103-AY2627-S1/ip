package duke.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import duke.exception.DukeException;

/**
 * Represents a task with a deadline.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy, h:mm a");

    private final LocalDateTime deadlineDate;

    /**
     * Constructs a Deadline task with the given description and date string.
     *
     * @param name     The description of the deadline task.
     * @param deadline The deadline date/time string (yyyy-MM-dd or yyyy-MM-dd HHmm).
     * @throws DukeException If the date/time string format is invalid.
     */
    public Deadline(String name, String deadline) throws DukeException {
        super(name);
        this.deadlineDate = parseDateTime(deadline);
    }

    /**
     * Constructs a Deadline task with the given description, deadline date/time object, and status.
     *
     * @param name         The description of the deadline task.
     * @param deadlineDate The deadline LocalDateTime object.
     * @param isDone       Whether the task is completed.
     */
    Deadline(String name, LocalDateTime deadlineDate, boolean isDone) {
        super(name);
        this.deadlineDate = deadlineDate;
        if (isDone) {
            markAsDone();
        }
    }

    /**
     * Parses the date string into a LocalDateTime object.
     *
     * @param input The raw date string.
     * @return The parsed LocalDateTime.
     * @throws DukeException If the date cannot be parsed.
     */
    private LocalDateTime parseDateTime(String input) throws DukeException {
        try {
            if (input.contains("T")) {
                return LocalDateTime.parse(input);
            } else if (input.contains(" ")) {
                return LocalDateTime.parse(input, INPUT_FORMAT);
            } else {
                return LocalDate.parse(input).atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            throw new DukeException(
                    "Invalid date format. Please use yyyy-MM-dd or yyyy-MM-dd HHmm (e.g., 2019-10-15 1800).");
        }
    }

    /**
     * Formats the Deadline for saving to a file.
     *
     * @return A pipe-separated string representing the Deadline's state.
     */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + deadlineDate;
    }

    /**
     * Returns the task type icon for a deadline.
     *
     * @return The string "D".
     */
    @Override
    public String getTaskIcon() {
        return "D";
    }

    /**
     * Returns a string representation of the deadline task.
     *
     * @return The formatted deadline task string.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + deadlineDate.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Creates and returns a copy of this Deadline task.
     *
     * @return A copy of this Deadline task.
     */
    @Override
    public Deadline copy() {
        return new Deadline(getName(), this.deadlineDate, isDone());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Deadline otherDeadline = (Deadline) other;
        return getName().equals(otherDeadline.getName()) && deadlineDate.equals(otherDeadline.deadlineDate);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getName(), deadlineDate);
    }
}
