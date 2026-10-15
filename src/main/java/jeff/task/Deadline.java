package jeff.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * A task that needs to be done by a specific date,
 * e.g. "submit report by 2019-10-15".
 */
public class Deadline extends Task {
    private static final DateTimeFormatter PRINT_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");

    protected LocalDate by;

    /**
     * Creates a new, not-done deadline with the given description and due date.
     *
     * @param description what the task is; must not be null or blank.
     * @param by the due date, in "yyyy-mm-dd" format; must not be null, blank, or wrongly formatted.
     * @throws IllegalArgumentException if description or by is invalid.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = parseDate(by);
    }

    /**
     * Parses a "yyyy-mm-dd" date string, wrapping a blank or wrongly
     * formatted value as an {@link IllegalArgumentException} so callers can
     * handle it the same way as any other invalid task field.
     */
    private static LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) {
            throw new IllegalArgumentException("A deadline needs a /by date.");
        }
        try {
            return LocalDate.parse(date.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Please give the /by date in yyyy-mm-dd format, e.g. 2019-12-02.");
        }
    }

    /** Returns this deadline's status icon, description, and by date, prefixed with "[D]". */
    @Override
    public String toString() {
        // by is set exactly once, in the constructor, and parseDate() either returns a
        // valid LocalDate or throws before this object finishes constructing. So by should
        // never be null while this object exists; if it were, that would mean this
        // invariant was broken (e.g. by future code adding a way to clear it), not that
        // the user typed something invalid.
        assert by != null : "by should never be null on a fully constructed Deadline";
        return "[D]" + super.toString() + " (by: " + by.format(PRINT_FORMAT) + ")";
    }

    /** Returns this deadline's due date, since that's what a deadline should be sorted by. */
    @Override
    public Optional<LocalDate> getChronologicalDate() {
        return Optional.of(by);
    }

    /** Returns this deadline's data encoded for {@link jeff.storage.Storage}, prefixed with "D". */
    @Override
    public String toSaveFormat() {
        return "D | " + super.toSaveFormat() + " | " + by;
    }
}
