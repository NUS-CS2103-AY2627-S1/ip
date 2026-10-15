package bucket;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

/** A task spanning a start and end date, e.g. "project meeting (from: Oct 15 2019 to: Oct 16 2019)". */
public class Event extends Task {
    /**
     * How the dates are shown to the user; the save file uses plain yyyy-mm-dd instead.
     *
     * The locale is pinned for the same reason as in Deadline: left to the machine,
     * the month name changes with the operating system's language setting.
     */
    private static final DateTimeFormatter DISPLAY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private LocalDate from;
    private LocalDate to;

    /**
     * Creates an event.
     *
     * @param name Description the user typed.
     * @param from Date the event starts.
     * @param to Date the event ends.
     */
    public Event(String name, LocalDate from, LocalDate to) {
        super(name);
        this.from = from;
        this.to = to;
    }

    @Override
    public String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the start date rather than the end date. An event is looked up by
     * when it begins, so ordering by start is what puts the next one to attend
     * at the top.
     *
     * @return Date the event starts.
     */
    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(this.from);
    }

    /**
     * Returns the save-file line with both dates appended in yyyy-mm-dd form,
     * which is what LocalDate.parse reads back.
     *
     * @return Save-file form of this event.
     */
    @Override
    public String toSaveString() {
        return super.toSaveString() + " | " + this.from + " | " + this.to;
    }

    /**
     * Returns the display form with both dates in MMM dd yyyy form.
     *
     * @return Display form of this event.
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.format(DISPLAY)
                + " to: " + this.to.format(DISPLAY) + ")";
    }
}
