package bibi.task;

import bibi.BibiException;
import java.time.LocalDate;

/**
 * Represents a task that must be completed by a given date, and optionally a
 * time of day.
 */
public class Deadline extends Task {
    /** Shown when either the description or the /by time is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "A deadline needs both a description and a /by time.";

    private final TaskDateTime by;

    /**
     * Creates a deadline with a description and its required completion time.
     *
     * @param description text describing the deadline
     * @param by the deadline date, in one of the accepted date formats
     * @throws BibiException if the description is blank or the date cannot be read
     */
    public Deadline(String description, String by) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.by = TaskDateTime.parse(requireTaskText(by, MISSING_FIELD_MESSAGE));
    }

    /**
     * Returns the code D, which marks a deadline in the list and in the save
     * file.
     */
    @Override
    protected String getTypeCode() {
        return "D";
    }

    /**
     * Returns the due date in brackets, for display after the description.
     */
    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }

    /**
     * Returns the deadline date as the single extra save-file field.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + by.toStorageString();
    }

    /**
     * Reports whether this deadline falls due on the supplied date.
     */
    @Override
    public boolean occursOn(LocalDate queryDate) {
        return by.isOn(queryDate);
    }
}
