/**
 * Represents a task that must be completed by a given time string.
 */
public class Deadline extends Task {
    /** Shown when either the description or the /by time is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "A deadline needs both a description and a /by time.";

    private final String by;

    /**
     * Creates a deadline with a description and its required completion time.
     *
     * @param description text describing the deadline
     * @param by the deadline time, stored exactly as entered
     * @throws BibiException if the description or deadline time is blank
     */
    public Deadline(String description, String by) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.by = requireTaskText(by, MISSING_FIELD_MESSAGE);
    }

    @Override
    protected String getTypeCode() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }

    /**
     * Returns the deadline time as the single extra save-file field.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + by;
    }
}
