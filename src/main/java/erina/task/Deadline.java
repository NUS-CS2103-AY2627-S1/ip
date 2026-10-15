package erina.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * A task that must be finished by a stated date, shown as
 * {@code [D][ ] return book (by: Oct 15 2019)}.
 */
public class Deadline extends Task {
    /**
     * How the due date is shown to the user, for example {@code Oct 15 2019}.
     *
     * <p>The locale is fixed to English because month abbreviations otherwise
     * follow the computer's settings: English (Singapore), for one, shortens
     * September to {@code Sept}, so the same task would look different on
     * different machines.
     */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * When the task is due.
     *
     * <p>Kept as a real date rather than the text the user typed, so it can
     * be reformatted for display and, later, compared and sorted.
     */
    private final LocalDate by;

    /**
     * Creates a deadline that is not yet done.
     *
     * @param description what the user wants to do
     * @param by          the date it is due
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "a deadline needs a due date";
        this.by = by;
    }

    @Override
    public String getTypeCode() {
        return "D";
    }

    @Override
    protected List<String> getExtraSaveFields() {
        // LocalDate.toString gives the ISO form (2019-10-15), the same form
        // the user types, so the file stays easy to read and to parse back.
        return List.of(by.toString());
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_FORMAT) + ")";
    }
}
