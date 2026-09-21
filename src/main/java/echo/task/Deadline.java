package echo.task;

import java.time.LocalDateTime;
import java.util.Objects;

import echo.utils.DateTimeUtility;


/**
 * Represents a task that needs to be done before a specific date-time.
 */
public class Deadline extends Task {
    public static final TaskType TYPE = TaskType.DEADLINE;
    private final LocalDateTime by;

    /**
     * Creates an incomplete deadline with the given description and due date.
     *
     * @param description text that describes the deadline
     * @param by date-time by which the deadline must be completed
     */
    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = Objects.requireNonNull(by, "A deadline date-time must not be null.");
    }

    /**
     * Creates {@link String} representation of the {@link Deadline} instance
     * @return String representation of deadline
     */
    @Override
    public String toString() {
        return "[" + TYPE.toString() + "]" + super.toString()
                + " (by: " + by.format(DateTimeUtility.DISPLAY_FORMAT) + ")";
    }


    /**
     * Creates save {@link String} representation of the {@link Deadline} instance
     * @return Save string representation of deadline
     */
    @Override
    public String toSaveFormat() {
        return TYPE.toString() + " | " + super.toSaveFormat() + " | " + by;
    }

    /**
     * Reconstructs a deadline from its save-format fields (type letter,
     * completion date-time, description, due date).
     *
     * @param fields the split save-file line for this deadline
     * @return the reconstructed deadline
     */
    public static Deadline fromSaveFormat(String[] fields) {
        if (fields.length != 4 || fields[3].isEmpty()) {
            throw new IllegalArgumentException("A saved deadline must have exactly 4 non-empty fields.");
        }
        return new Deadline(fields[2], LocalDateTime.parse(fields[3]));
    }

    @Override
    public TaskType getTaskType() {
        return TYPE;
    }

    @Override
    boolean hasSameSchedule(Task other) {
        return by.equals(((Deadline) other).by);
    }
}

