package serangooner.task;

import java.time.LocalDate;
import java.util.Optional;

import serangooner.SerangoonerException;

/**
 * Represents a task that must be completed by a given date or time.
 */
public class Deadline extends Task {
    /** Code identifying a deadline in the save file. */
    public static final String SAVE_CODE = "D";
    private static final int SAVE_FIELD_COUNT = 4;
    private static final int SAVE_INDEX_DEADLINE = 3;

    private final TaskDateTime deadline;

    /**
     * Constructs a deadline from a description and an unparsed date.
     *
     * @param description Text describing what the task involves.
     * @param deadline Date, and optionally time, by which the task must be completed.
     * @throws SerangoonerException If the description carries the save delimiter,
     *         or the date is not in an accepted format.
     */
    public Deadline(String description, String deadline) {
        super(description);
        this.deadline = TaskDateTime.parse(deadline);
    }

    /**
     * Returns the deadline encoded by the given fields of a saved line.
     *
     * @param fields Fields that one line of the save file was split into.
     * @return Deadline the line describes, or nothing if it cannot be read.
     */
    public static Optional<Task> parseSaveFields(String[] fields) {
        return buildFromSaveFields(fields, SAVE_FIELD_COUNT,
                validFields -> new Deadline(validFields[SAVE_INDEX_DESCRIPTION],
                        validFields[SAVE_INDEX_DEADLINE]));
    }

    @Override
    public String getTypeName() {
        return "deadline";
    }

    @Override
    public boolean isWithin(LocalDate start, LocalDate end) {
        return isOverlapping(deadline, deadline, start, end);
    }

    /**
     * {@inheritDoc}
     * A deadline must also fall due at the same time.
     */
    @Override
    public boolean isDuplicateOf(Task other) {
        return other instanceof Deadline otherDeadline
                && super.isDuplicateOf(other)
                && deadline.equals(otherDeadline.deadline);
    }

    @Override
    public String toSaveFormat() {
        return SAVE_CODE + SAVE_DELIMITER + super.toSaveFormat()
                + SAVE_DELIMITER + deadline.toSaveFormat();
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + deadline + ")";
    }
}
