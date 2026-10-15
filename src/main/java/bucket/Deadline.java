package bucket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/** A task that must be done before a given date, e.g. "return book (by: Oct 15 2019)". */
public class Deadline extends Task {
    /**
     * How the date is shown to the user; the save file uses plain yyyy-mm-dd instead.
     *
     * The locale is pinned rather than left to the machine. Without it the month name
     * follows whatever language the operating system is set to, so the same task reads
     * "Oct 15 2019" here and "10月 15 2019" on a Chinese install.
     */
    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private LocalDate by;

    /**
     * Creates a deadline.
     *
     * @param name Description the user typed.
     * @param by Date the task is due.
     */
    public Deadline(String name, LocalDate by) {
        super(name);
        this.by = by;
    }

    @Override
    public String getTypeIcon() {
        return "D";
    }

    /**
     * Returns the due date, which is the only date a deadline has and the one
     * the user is tracking.
     *
     * @return Date the task is due.
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(this.by);
    }

    /**
     * Returns the save-file line with the due date appended in yyyy-mm-dd form,
     * which is what LocalDate.parse reads back.
     *
     * @return Save-file form of this deadline.
     */
    @Override
    public String toSaveString() {
        return super.toSaveString() + " | " + this.by;
    }

    /**
     * Returns the display form with the due date in MMM dd yyyy form.
     *
     * @return Display form of this deadline.
     */
    @Override
    public String toString() {
        return super.toString() + " (by: " + this.by.format(DISPLAY) + ")";
    }
}
