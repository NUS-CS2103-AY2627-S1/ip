package ff15.task;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import ff15.FF15Exception;

/**
 * A span of dates the user asked about with the {@code on} command. How precise
 * the span is comes from how much of the date was typed: {@code 2019-12-02} is a
 * single day, {@code 2019-12} a whole month, and {@code 2019} a whole year.
 * Both ends of the span are included.
 */
public class DateRange {
    /** How a whole month is shown back to the user, e.g. {@code Dec 2019}. */
    private static final DateTimeFormatter MONTH_DISPLAY = DateTimeFormatter.ofPattern("MMM yyyy");

    private final LocalDate start;
    private final LocalDate end;
    private final String label;

    private DateRange(LocalDate start, LocalDate end, String label) {
        // includes() and overlaps() both read the span as start-then-end, and quietly
        // match nothing at all if the two are the wrong way round.
        assert !start.isAfter(end) : "a date range must not end before it starts: " + start + " to " + end;
        this.start = start;
        this.end = end;
        this.label = label;
    }

    /**
     * Builds a span from what the user typed after {@code on}. The number of
     * dash-separated parts decides whether it names a day, a month, or a year.
     *
     * @throws FF15Exception if {@code text} is not a day, month, or year.
     */
    public static DateRange parse(String text) throws FF15Exception {
        String[] parts = text.split("-");
        try {
            if (parts.length == 3) { // yyyy-mm-dd: one day
                LocalDate day = LocalDate.parse(text);
                return new DateRange(day, day, TaskTime.formatDate(day));
            }
            if (parts.length == 2) { // yyyy-mm: first to last day of that month
                YearMonth month = YearMonth.parse(text);
                return new DateRange(month.atDay(1), month.atEndOfMonth(), month.format(MONTH_DISPLAY));
            }
            if (parts.length == 1) { // yyyy: first to last day of that year
                Year year = Year.parse(text);
                return new DateRange(year.atDay(1), year.atMonth(12).atEndOfMonth(), year.toString());
            }
        } catch (DateTimeParseException e) {
            throw new FF15Exception(rejectionMessage(text));
        }
        throw new FF15Exception(rejectionMessage(text));
    }

    private static String rejectionMessage(String text) {
        return "'" + text + "' isn't a date, month, or year. I know dates. "
                + "Try: on 2019-12-02, on 2019-12, or on 2019";
    }

    /** Returns whether {@code date} falls inside this span. */
    public boolean includes(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Returns whether the span running from {@code from} to {@code to} shares at
     * least one day with this one, i.e. whether they overlap at any point.
     */
    public boolean overlaps(LocalDate from, LocalDate to) {
        return !from.isAfter(end) && !to.isBefore(start);
    }

    /**
     * Returns how this span is named in output, e.g. {@code Dec 02 2019},
     * {@code Dec 2019}, or {@code 2019}.
     */
    public String getLabel() {
        return label;
    }
}
