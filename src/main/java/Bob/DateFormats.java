package bob;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Map;

/** Provides the date formats used by task display strings. */
final class DateFormats {
    private static final Map<Long, String> DISPLAY_MONTHS = Map.ofEntries(
            Map.entry((long) Month.JANUARY.getValue(), "Jan"),
            Map.entry((long) Month.FEBRUARY.getValue(), "Feb"),
            Map.entry((long) Month.MARCH.getValue(), "Mar"),
            Map.entry((long) Month.APRIL.getValue(), "Apr"),
            Map.entry((long) Month.MAY.getValue(), "May"),
            Map.entry((long) Month.JUNE.getValue(), "Jun"),
            Map.entry((long) Month.JULY.getValue(), "Jul"),
            Map.entry((long) Month.AUGUST.getValue(), "Aug"),
            Map.entry((long) Month.SEPTEMBER.getValue(), "Sept"),
            Map.entry((long) Month.OCTOBER.getValue(), "Oct"),
            Map.entry((long) Month.NOVEMBER.getValue(), "Nov"),
            Map.entry((long) Month.DECEMBER.getValue(), "Dec"));

    static final DateTimeFormatter DISPLAY_DATE = new DateTimeFormatterBuilder()
            .appendText(ChronoField.MONTH_OF_YEAR, DISPLAY_MONTHS)
            .appendPattern(" dd yyyy")
            .toFormatter();

    private DateFormats() {
    }
}