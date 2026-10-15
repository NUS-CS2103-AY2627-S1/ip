package kiwi.task;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A contiguous free period on one work day, returned by a {@code free} search.
 */
public class FreeSlot {
    /** Start of the work day used when looking for free slots. */
    public static final LocalTime WORK_DAY_START = LocalTime.of(8, 0);

    /** Length of the work day in hours ({@code 08:00-18:00}). */
    public static final int WORK_DAY_HOURS = 10;

    /** How many days ahead a {@code free} search will look. */
    public static final int SEARCH_DAYS = 365;

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;

    /**
     * Creates a free slot on {@code date} from {@code start} to {@code end}.
     *
     * @param date calendar day of the slot.
     * @param start start time on that day.
     * @param end end time on that day.
     */
    public FreeSlot(LocalDate date, LocalTime start, LocalTime end) {
        assert date != null : "Finder always supplies a calendar date";
        assert start != null : "Finder always supplies a start time";
        assert end != null : "Finder always supplies an end time";
        assert !end.isBefore(start) : "A free slot's end is not before its start";
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    /**
     * Returns this slot for display, e.g. {@code Dec 04 2019, 08:00-12:00}.
     *
     * @return formatted date and time range.
     */
    public String toDisplayString() {
        return KiwiDate.format(date) + ", " + start.format(TIME_FORMAT) + "-" + end.format(TIME_FORMAT);
    }
}
