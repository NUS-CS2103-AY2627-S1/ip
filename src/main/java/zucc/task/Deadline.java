package zucc.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import zucc.ZuccException;

/**
 * Represents a task that must be completed by a specific date and time.
 */
public class Deadline extends Task {
    /** Short identifier used when displaying and storing deadlines. */
    static final String TYPE_CODE = "D";

    /** Error used when a deadline is missing any of its required values. */
    private static final String INVALID_DEADLINE_ERROR =
            "Zucc needs more data: add a deadline description "
                    + "followed by /by and a due date.";

    /** Date and time by which the task should be completed. */
    private final LocalDateTime dueDateTime;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description description of the task.
     * @param dueDateTimeText due date and time in {@code d/M/yyyy HHmm} format.
     * @throws ZuccException if the description is blank or the due date is invalid.
     */
    public Deadline(String description, String dueDateTimeText) throws ZuccException {
        this(description, dueDateTimeText, Priority.NONE);
    }

    /**
     * Creates an incomplete deadline with the given description, due date, and priority.
     *
     * @param description description of the task.
     * @param dueDateTimeText due date and time in {@code d/M/yyyy HHmm} format.
     * @param priority importance assigned to the task.
     * @throws ZuccException if a supplied value is invalid.
     */
    public Deadline(String description, String dueDateTimeText, Priority priority)
            throws ZuccException {
        super(requireNonBlank(description, INVALID_DEADLINE_ERROR), priority);
        this.dueDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(dueDateTimeText, INVALID_DEADLINE_ERROR));
    }

    /**
     * Reconstructs a deadline from decoded storage fields.
     *
     * @param fields decoded type, status, priority, description, and deadline.
     * @throws ZuccException if the fields do not describe a valid deadline.
     */
    Deadline(String[] fields) throws ZuccException {
        if (fields == null || fields.length != 5) {
            throw new ZuccException("Invalid stored deadline.");
        }
        super(fields[3], fields[1], fields[2]);
        this.dueDateTime = TaskDateTimeFormat.parse(
                requireNonBlank(fields[4], INVALID_DEADLINE_ERROR));
    }

    /**
     * Reports whether this deadline is due on a given date.
     *
     * @param date date to check.
     * @return {@code true} if the deadline is due on the date.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.toLocalDate().equals(date);
    }

    /**
     * Supplies the fields needed to store this deadline.
     *
     * @return type code followed by the deadline.
     */
    @Override
    protected String[] getStorageFields() {
        return new String[] {TYPE_CODE, TaskDateTimeFormat.formatForStorage(dueDateTime)};
    }

    /**
     * Formats this task with its type marker and deadline.
     *
     * @return deadline with a user-friendly date and time.
     */
    @Override
    public String toString() {
        return "[" + TYPE_CODE + "]" + super.toString()
                + " (by: " + TaskDateTimeFormat.formatForDisplay(dueDateTime) + ")";
    }
}
