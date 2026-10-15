package ben;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * A task that needs to be done before a specific date (and, optionally, a
 * time).
 * <p>
 * Level-8: the "/by" value is parsed into a {@link LocalDate} (plus an
 * optional {@link LocalTime}) instead of being kept as free text, so the
 * date can be re-formatted for display and, later, compared or sorted.
 * Accepted input: an ISO date {@code 2019-12-02}, or day/month/year
 * {@code 2/12/2019}, each optionally followed by a 24-hour {@code HHmm}
 * time, e.g. {@code 2019-12-02 1800}.
 */
class Deadline extends Task {
    /**
     * Date formats accepted from the user / data file, tried in order.
     * <p>
     * A-MoreErrorHandling: parsed with {@link ResolverStyle#STRICT} rather
     * than the (default) smart resolver, so a non-existent calendar date
     * such as {@code 2019-2-30} is rejected instead of silently rolling
     * over to Feb 28. The year field uses {@code uuuu} (year-of-era-free
     * "proleptic year") rather than {@code yyyy}: under STRICT, {@code yyyy}
     * requires an era field to resolve and would reject every date.
     */
    private static final DateTimeFormatter[] DATE_INPUTS = {
        DateTimeFormatter.ofPattern("uuuu-M-d").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
    };
    private static final DateTimeFormatter TIME_INPUT = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DATE_DISPLAY = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter TIME_DISPLAY = DateTimeFormatter.ofPattern("h:mma");

    private final LocalDate date;
    /** Time of day, or {@code null} when only a date was given. */
    private final LocalTime time;

    Deadline(String description, String by) throws BenException {
        super(description);
        String[] parts = by.trim().split("\\s+", 2);
        // split(..., 2) can only ever produce 1 or 2 parts.
        assert parts.length == 1 || parts.length == 2;
        this.date = parseDate(parts[0], by);
        this.time = parts.length > 1 ? parseTime(parts[1], by) : null;
    }

    private static LocalDate parseDate(String text, String original) throws BenException {
        for (DateTimeFormatter format : DATE_INPUTS) {
            try {
                return LocalDate.parse(text, format);
            } catch (DateTimeParseException ignored) {
                // try the next accepted format
            }
        }
        throw new BenException("I couldn't read the deadline date \"" + original
                + "\". Try e.g. \"deadline return book /by 2019-12-02 1800\".");
    }

    private static LocalTime parseTime(String text, String original) throws BenException {
        try {
            return LocalTime.parse(text, TIME_INPUT);
        } catch (DateTimeParseException e) {
            throw new BenException("I couldn't read the deadline time in \"" + original
                    + "\". Use a 24-hour HHmm time, e.g. 1800.");
        }
    }

    @Override
    String getTypeIcon() {
        return "D";
    }

    @Override
    String extraFields() {
        // Store in a form the constructor above can read straight back.
        String saved = date.toString() + (time != null ? " " + time.format(TIME_INPUT) : "");
        return " | " + saved;
    }

    @Override
    String extraInfo() {
        String shown = date.format(DATE_DISPLAY)
                + (time != null ? ", " + time.format(TIME_DISPLAY) : "");
        return " (by: " + shown + ")";
    }
}
