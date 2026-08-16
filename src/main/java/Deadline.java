/**
 * Represents a task that must be completed by a given time string.
 */
public class Deadline extends Task {
    private final String by;

    /**
     * Creates a deadline with a description and its required completion time.
     *
     * @param description text describing the deadline
     * @param by the deadline time, stored exactly as entered
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeCode() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }
}
