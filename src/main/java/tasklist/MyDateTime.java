package tasklist;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a date, or a date with time, along with display and
 * file-storage formatting for either case.
 */
public class MyDateTime {
    private static final DateTimeFormatter INPUT_DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter INPUT_D = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DT = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");
    private static final DateTimeFormatter DISPLAY_D = DateTimeFormatter.ofPattern("MMM dd yyyy");

    private final LocalDateTime dateTime;
    private final LocalDate date;
    private final boolean hasTime;

    /**
     * Constructs a {@code MyDateTime} representing a date and time.
     *
     * @param dateTime the date and time value.
     */
    public MyDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        this.date = null;
        this.hasTime = true;
    }

    /**
     * Constructs a {@code MyDateTime} representing a date with no time component.
     *
     * @param date the date value.
     */
    public MyDateTime(LocalDate date) {
        this.date = date;
        this.dateTime = null;
        this.hasTime = false;
    }

    /**
     * Returns this date/time formatted for user-facing display.
     *
     * @return the display-formatted string.
     */
    public String toDisplayString() {
        return hasTime ? dateTime.format(DISPLAY_DT) : date.format(DISPLAY_D);
    }

    /**
     * Returns this date/time formatted for file storage, round-trippable
     * back into a {@code MyDateTime} via {@code DateTimeParser}.
     *
     * @return the file-formatted string.
     */
    public String toFileString() {
        return hasTime ? dateTime.format(INPUT_DT) : date.format(INPUT_D);
    }

    /**
     * Returns whether this date/time includes a time component.
     *
     * @return {@code true} if a time is set, {@code false} if date-only.
     */
    public boolean hasTime() {
        return hasTime;
    }
}
