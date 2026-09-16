package zucc.task;

import java.time.LocalDate;

import zucc.ZuccException;

/**
 * Represents a task that has no date or time attached to it.
 */
public class Todo extends Task {
    /** Short identifier used when displaying and storing to-dos. */
    static final String TYPE_CODE = "T";

    /** Error used when a to-do is missing its required description. */
    private static final String INVALID_TODO_ERROR =
            "Zucc needs more data: give that todo a description.";

    /**
     * Creates an incomplete to-do with the given description.
     *
     * @param description description of the task.
     * @throws ZuccException if the description is blank.
     */
    public Todo(String description) throws ZuccException {
        this(description, Priority.NONE);
    }

    /**
     * Creates an incomplete to-do with the given description and priority.
     *
     * @param description description of the task.
     * @param priority importance assigned to the task.
     * @throws ZuccException if the description or priority is invalid.
     */
    public Todo(String description, Priority priority) throws ZuccException {
        super(requireNonBlank(description, INVALID_TODO_ERROR), priority);
    }

    /**
     * Reconstructs a to-do from decoded storage fields.
     * The field-count check runs before the superclass constructor so indexing is safe.
     *
     * @param fields decoded type, status, priority, and description.
     * @throws ZuccException if the fields do not describe a valid to-do.
     */
    Todo(String[] fields) throws ZuccException {
        if (fields == null || fields.length != 4) {
            throw new ZuccException("Invalid stored to-do.");
        }
        super(fields[3], fields[1], fields[2]);
    }

    /**
     * Reports that a to-do does not occur on a specific date.
     *
     * @param date date to check.
     * @return always {@code false}.
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Supplies the plain type field needed to store this to-do.
     *
     * @return type code for this to-do.
     */
    @Override
    protected String[] getStorageFields() {
        return new String[] {TYPE_CODE};
    }

    /**
     * Formats this task with the type marker used for to-dos.
     *
     * @return the task in {@code [T][status] description} format.
     */
    @Override
    public String toString() {
        return "[" + TYPE_CODE + "]" + super.toString();
    }
}
