package otaku.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that must be completed by a given time. */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate by;

    /** Creates an incomplete deadline task. */
    public Deadline(String description, LocalDate by) {
        super(description);
        assert by != null : "A deadline date must be parsed before construction";

        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /** Returns the deadline date. */
    public LocalDate getBy() {
        return by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE) + ")";
    }
}
