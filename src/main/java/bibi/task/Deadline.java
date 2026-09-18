package bibi.task;

import java.time.LocalDate;
import java.util.Optional;

import bibi.BibiException;

/**
 * Represents a task that must be completed by a given date, and optionally a
 * time of day.
 */
public class Deadline extends Task {
    /** Shown when either the description or the /by time is missing. */
    private static final String MISSING_FIELD_MESSAGE =
            "A deadline needs both a description and a /by time.";

    private final TaskDateTime dueTime;

    /**
     * Creates a deadline with a description and its required completion time.
     *
     * @param description text describing the deadline
     * @param dueTimeText the deadline date, in one of the accepted date formats
     * @throws BibiException if the description is blank or the date cannot be read
     */
    public Deadline(String description, String dueTimeText) throws BibiException {
        super(requireTaskText(description, MISSING_FIELD_MESSAGE));
        this.dueTime = TaskDateTime.parse(requireTaskText(dueTimeText, MISSING_FIELD_MESSAGE));
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
        return " (by: " + dueTime + ")";
    }

    /**
     * Returns the deadline date as the single extra save-file field.
     */
    @Override
    protected String getSaveFields() {
        return " " + FIELD_SEPARATOR + " " + dueTime.toStorageString();
    }

    /**
     * Returns the moment this deadline is due, which is what it sorts by.
     */
    @Override
    public Optional<TaskDateTime> getScheduledTime() {
        return Optional.of(dueTime);
    }

    /**
     * Reports whether this deadline falls due on the supplied date.
     */
    @Override
    public boolean occursOn(LocalDate queryDate) {
        return dueTime.isOn(queryDate);
    }

    /**
     * Returns the confirmation that divides the labor: Bibi keeps the date, the
     * user does the work.
     */
    @Override
    public String describeAddition() {
        return "Added. Remembering it is my job. Doing it is still yours:";
    }
}
