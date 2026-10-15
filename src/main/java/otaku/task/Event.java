package otaku.task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents a task that occurs between a start and end time. */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate from;
    private final LocalDate to;

    /** Creates an incomplete event task. */
    public Event(String description, LocalDate from, LocalDate to) {
        super(description);
        assert from != null : "An event start date must be parsed before construction";
        assert to != null : "An event end date must be parsed before construction";
        assert to.isAfter(from) : "An event must end after it starts";

        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /** Returns the event's start date. */
    public LocalDate getFrom() {
        return from;
    }

    /** Returns the event's end date. */
    public LocalDate getTo() {
        return to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from.format(DISPLAY_DATE)
                + " to: " + to.format(DISPLAY_DATE) + ")";
    }
}
