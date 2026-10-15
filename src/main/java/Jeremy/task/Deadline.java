package jeremy.task;

/** A task that needs to be done before a specific date/time. */
public class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        assert by != null && !by.isBlank() : "Deadline must have a due date/time";
        this.by = by;
    }

    public String getBy() {
        return by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
