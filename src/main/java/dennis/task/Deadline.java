package dennis.task;

import java.time.LocalDate;
import java.util.Objects;

import dennis.DennisException;

/**
 * A task that must be done by a single date, shown as
 * {@code [D][ ] ... (by: Dec 01 2019)}.
 */
public class Deadline extends Task {
    /** The date the task is due. Stored as a real date, not free text. */
    private final LocalDate by;

    /**
     * Creates a deadline with the given description and due date.
     *
     * @param description the task text
     * @param by          the due date in {@code yyyy-MM-dd} form
     * @throws DennisException if the description is blank or contains
     *                         {@code '|'}, or {@code by} is blank or not a
     *                         valid {@code yyyy-MM-dd} date
     */
    public Deadline(String description, String by)
            throws DennisException {
        super(validateDescription(description));
        this.by = validateBy(by);
    }

    /**
     * Checks the description and returns it unchanged when acceptable.
     *
     * @throws DennisException if it is blank or contains a {@code '|'}
     */
    private static String validateDescription(String description)
            throws DennisException {
        if (description.isBlank()) {
            throw new DennisException(
                    "I'm sorry, deadline must contain a task.");
        }

        return rejectSeparator(description, "A task description");
    }

    /**
     * Checks the due-date text and parses it into a {@link LocalDate}.
     *
     * @throws DennisException if {@code by} is blank or not a valid
     *                         {@code yyyy-MM-dd} date
     */
    private static LocalDate validateBy(String by)
            throws DennisException {
        if (by.isBlank()) {
            throw new DennisException(
                    "deadline must contain a date and time.");
        }

        // parseDate enforces the yyyy-MM-dd format; a LocalDate can never
        // contain the save-file separator, so rejectSeparator is not needed.
        return parseDate(by, "A deadline date");
    }

    @Override
    public String toFileFormat() {
        // by.toString() is ISO yyyy-MM-dd, the same form parseDate accepts.
        return "D | " + getStatusNumber() + " | " + description + " | " + by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    /**
     * Two deadlines are equal when they have the same description and due
     * date; completion status does not count, so this is what
     * {@link TaskList#add} uses to reject an exact duplicate.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Deadline other)) {
            return false;
        }
        return description.equals(other.description) && by.equals(other.by);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Deadline.class, description, by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + formatDate(by) + ")";
    }
}
