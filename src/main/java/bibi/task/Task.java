package bibi.task;

import java.time.LocalDate;

import bibi.BibiException;

/**
 * Represents information shared by every kind of task.
 *
 * <p>Subclasses provide their own type code and any date or time details.
 */
public abstract class Task {
    /** Separator between fields of a saved task, so it may not appear in task text. */
    public static final String FIELD_SEPARATOR = "|";

    private final String description;
    private boolean complete;

    /**
     * Creates an incomplete task with the supplied description.
     *
     * @param description text describing the task
     * @throws BibiException if the description is blank
     */
    protected Task(String description) throws BibiException {
        this.description = requireTaskText(description, "A task needs a description.");
        complete = false;
    }

    /**
     * Checks a piece of task text before it is stored.
     *
     * <p>The separator is rejected as well as blank text, because text containing
     * it would be split into the wrong fields when the save file is read back.
     *
     * @param text text supplied for the description or a date or time
     * @param errorMessage explanation to show when the text is missing
     * @return the supplied text when it is valid
     * @throws BibiException if the text is blank or contains the field separator
     */
    protected static String requireTaskText(String text, String errorMessage)
            throws BibiException {
        if (text == null || text.isBlank()) {
            throw new BibiException(errorMessage);
        }
        if (text.contains(FIELD_SEPARATOR)) {
            throw new BibiException("Task text cannot contain '" + FIELD_SEPARATOR
                    + "' because that character separates the fields in the save file.");
        }
        return text;
    }

    /**
     * Marks this task as complete.
     */
    public void markComplete() {
        complete = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void markIncomplete() {
        complete = false;
    }

    /**
     * Reports whether this task falls on the supplied date.
     *
     * <p>Task types that carry no date, such as a ToDo, are never on a
     * particular date, so the default answer is {@code false}.
     *
     * @param queryDate the date being asked about
     * @return {@code true} when this task belongs to that date
     */
    public boolean occursOn(LocalDate queryDate) {
        return false;
    }

    /**
     * Returns the single-letter code used to display this task type.
     *
     * @return the type code
     */
    protected abstract String getTypeCode();

    /**
     * Returns any type-specific information that follows the description.
     *
     * @return displayable details, including any leading spacing
     */
    protected abstract String getDetails();

    /**
     * Returns the type-specific fields appended to this task's save-file line.
     *
     * @return extra fields, each preceded by the field separator, or an empty
     *     string when the task type stores no date or time
     */
    protected abstract String getSaveFields();

    /**
     * Returns this task encoded as a single save-file line.
     *
     * <p>The completion flag is written as {@code 1} or {@code 0} rather than the
     * display form so that the file stays easy to read and to parse back.
     *
     * @return the encoded task, for example {@code D | 0 | return book | Sunday}
     */
    public String toFileFormat() {
        return getTypeCode()
                + " " + FIELD_SEPARATOR + " " + (complete ? "1" : "0")
                + " " + FIELD_SEPARATOR + " " + description
                + getSaveFields();
    }

    /**
     * Returns a display form containing the task type, status, and details.
     *
     * @return the formatted task
     */
    @Override
    public String toString() {
        return "[" + getTypeCode() + "]"
                + (complete ? "[X] " : "[ ] ")
                + description
                + getDetails();
    }
}
