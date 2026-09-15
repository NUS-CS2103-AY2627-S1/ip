package ekud.task;

import java.time.LocalDate;

/**
 * A task that must be done by a given date or date-time.
 */
public class Deadline extends Task {
    private final TaskDateTime by;

    /**
     * Creates a deadline with the given description and due date/time.
     *
     * @param description text describing the deadline
     * @param by          due date or date-time
     */
    public Deadline(String description, TaskDateTime by) {
        super(description);
        // Parser parses /by before constructing; a missing date is a programming error.
        assert by != null : "Deadline due date/time must be provided";
        this.by = by;
    }

    /**
     * Returns the due date or date-time.
     *
     * @return the {@code /by} value
     */
    public TaskDateTime getBy() {
        return by;
    }

    /**
     * Returns {@code D}, the type icon for deadlines.
     *
     * @return {@code D}
     */
    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns whether this deadline is due on {@code date}.
     *
     * @param date the calendar date to test
     * @return {@code true} if the due date is that day
     */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.occursOn(date);
    }

    /**
     * Returns the due date/time in {@code (by: ...)} form.
     *
     * @return the deadline suffix
     */
    @Override
    protected String getExtraDetails() {
        return " (by: " + by.toDisplayString() + ")";
    }

    /**
     * Returns {@code deadline DESCRIPTION /by WHEN} using the canonical date form.
     *
     * @return the add-command text for this deadline
     */
    @Override
    public String toCommandString() {
        return "deadline " + getDescription() + " /by " + by.toSaveString();
    }
}
