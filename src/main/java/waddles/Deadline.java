package waddles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a deadline task that must be completed by a specific date.
 * {@link Parser} rejects any /by date that isn't valid yyyy-mm-dd before
 * a Deadline is ever constructed from user input, so byDate below is only
 * ever null when a Deadline is built directly (e.g. from stored data) with
 * text that doesn't parse as a date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    // Not final: recur() advances by/byDate in place to the next occurrence.
    private String by;
    private LocalDate byDate;
    private Recurrence recurrence = Recurrence.NONE;

    public Deadline(String description, String by) {
        super(description);
        assert by != null && !by.isEmpty() : "Deadline 'by' text should never be null/empty; "
                + "callers must validate user input before constructing a Deadline";
        this.by = by;
        this.byDate = parseDate(by);
    }

    /**
     * Returns whether this deadline's date was understood as a structured
     * yyyy-mm-dd date, which is required for recurrence to be able to
     * compute a next occurrence.
     *
     * @return True if the date was parsed successfully.
     */
    public boolean hasStructuredDate() {
        return byDate != null;
    }

    /**
     * Sets how often this deadline repeats.
     *
     * @param recurrence The recurrence period; use {@link Recurrence#NONE} for a one-off deadline.
     */
    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    @Override
    public boolean isRecurring() {
        return recurrence != Recurrence.NONE;
    }

    @Override
    public void recur() {
        if (!isRecurring()) {
            throw new UnsupportedOperationException("This deadline does not recur");
        }
        byDate = recurrence.advance(byDate);
        by = byDate.toString();
        markAsNotDone();
    }

    /**
     * Attempts to parse the given text as a date in yyyy-mm-dd format.
     *
     * @param text The text to parse.
     * @return The parsed date, or null if the text is not a valid date.
     */
    private static LocalDate parseDate(String text) {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public String getBy() {
        return by;
    }

    @Override
    public Task copy() {
        Deadline copy = new Deadline(getDescription(), by);
        copy.setRecurrence(recurrence);
        if (isDone()) {
            copy.markAsDone();
        }
        return copy;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return byDate != null && byDate.equals(date);
    }

    @Override
    public String toString() {
        String displayBy = (byDate != null) ? byDate.format(OUTPUT_FORMAT) : by;
        String recurrenceSuffix = isRecurring() ? " (every " + recurrence.displayLabel() + ")" : "";
        return "[D]" + super.toString() + " (by: " + displayBy + ")" + recurrenceSuffix;
    }
}