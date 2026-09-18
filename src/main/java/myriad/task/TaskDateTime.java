package myriad.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import myriad.MyriadException;

/**
 * An immutable date, optionally paired with a time, parsed from user- or
 * save-file-supplied text. Whether a time was given matters: it decides
 * what {@link #toString()} prints (a bare date vs. a date+time), and lets
 * "no time" be represented honestly instead of being defaulted to
 * midnight and displayed as if the user had typed one.
 * <p>
 * The only way to build one is {@link #parse(String)}, which tries a fixed
 * list of formats in turn, so callers never need to know which specific
 * format the text ended up matching.
 */
public class TaskDateTime {
    /**
     * Formats that include a time component, tried before
     * {@link #DATE_ONLY_FORMATS}. Trying these first is what keeps parsing
     * unambiguous: a date-only string like {@code 2019-12-02} simply doesn't
     * have enough characters to satisfy any of these, so it always falls
     * through to the date-only attempts rather than being mis-parsed here.
     */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            createStrictFormatter("uuuu-MM-dd HHmm"),
            createStrictFormatter("uuuu-MM-dd HH:mm"),
            createStrictFormatter("d/M/uuuu HHmm"),
            createStrictFormatter("d/M/uuuu HH:mm"));

    /**
     * Date-only formats, tried after every date+time format has failed.
     */
    private static final List<DateTimeFormatter> DATE_ONLY_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            createStrictFormatter("d/M/uuuu"));

    /**
     * Display format used by {@link #toString()} when a time is present. Its
     * month names are always English, whatever the language of the computer,
     * because every reply they appear in is English. Without an explicit
     * locale the formatter would take the default one when this class loads,
     * so a Chinese-language computer would show "12月 02 2019" mid-sentence.
     */
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy HHmm", Locale.ENGLISH);

    /** Display format used by {@link #toString()} when only a date is present. */
    private static final DateTimeFormatter DISPLAY_DATE_ONLY =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final LocalDate date;
    private final LocalTime time;

    /**
     * Creates a value directly from its parts. Private so that
     * {@link #parse(String)} is the only entry point, which keeps the
     * accepted formats in one place.
     *
     * @param date the date part.
     * @param time the time part, or null if the input gave no time.
     */
    private TaskDateTime(LocalDate date, LocalTime time) {
        assert date != null : "time may be null (no time given), but date never is";
        this.date = date;
        this.time = time;
    }

    /**
     * Returns a formatter for {@code pattern} that rejects dates which do not
     * exist, such as 30 February. A formatter from
     * {@link DateTimeFormatter#ofPattern} is lenient by default and would move
     * such a date to the last day of the month without telling the user.
     * The ISO formatters used alongside these are already strict.
     *
     * The year is written {@code uuuu} rather than {@code yyyy}: in strict
     * mode, {@code yyyy} means "year of era" and cannot be resolved into a
     * date without an era (AD/BC) that no user types.
     *
     * @param pattern the pattern, e.g. {@code "d/M/uuuu"}.
     * @return the strict formatter.
     */
    private static DateTimeFormatter createStrictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }

    /**
     * Parses {@code raw} against, in order, every format in
     * {@link #DATE_TIME_FORMATS} then every format in
     * {@link #DATE_ONLY_FORMATS}, returning as soon as one succeeds. Throws
     * {@link MyriadException}, naming a couple of example accepted formats,
     * if {@code raw} matches none of them.
     *
     * @param raw the date/time text, from user input or the data file.
     * @return the parsed value, remembering whether a time was given.
     * @throws MyriadException if {@code raw} matches no accepted format.
     */
    public static TaskDateTime parse(String raw) throws MyriadException {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATS) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(raw, formatter);
                return new TaskDateTime(dateTime.toLocalDate(), dateTime.toLocalTime());
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATS) {
            try {
                return new TaskDateTime(LocalDate.parse(raw, formatter), null);
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }
        throw new MyriadException(
                "\"" + raw + "\" doesn't look like a date/time I understand. Try formats "
                        + "like \"2019-12-02\" or \"2019-12-02 1800\" (or \"2/12/2019\" / "
                        + "\"2/12/2019 1800\").");
    }

    /**
     * Returns this value as an ISO-8601 string suitable for saving to disk:
     * date+time (with the {@code T} separator) if a time is present,
     * otherwise just the date. Both forms are themselves accepted by
     * {@link #parse(String)} (they are the first entries of
     * {@link #DATE_TIME_FORMATS} and {@link #DATE_ONLY_FORMATS}), so a saved
     * value is always re-readable on the next load.
     *
     * @return an ISO-8601 date, or date and time, string.
     */
    public String toSaveFormat() {
        return time == null ? date.toString() : LocalDateTime.of(date, time).toString();
    }

    /**
     * Returns the earliest instant this value could refer to: the exact
     * date+time if a time is present, otherwise the very start of the date
     * (00:00). Paired with {@link #rangeEnd()}, this lets a date-only value
     * stand in for "any time during that day" when checking overlap with
     * another {@code TaskDateTime}.
     *
     * @return the start of the interval this value stands for.
     */
    public LocalDateTime rangeStart() {
        return time == null ? LocalDateTime.of(date, LocalTime.MIN) : LocalDateTime.of(date, time);
    }

    /**
     * Returns the latest instant this value could refer to: the exact
     * date+time if a time is present, otherwise the very end of the date
     * (23:59:59.999999999). See {@link #rangeStart()}.
     *
     * @return the end of the interval this value stands for.
     */
    public LocalDateTime rangeEnd() {
        return time == null ? LocalDateTime.of(date, LocalTime.MAX) : LocalDateTime.of(date, time);
    }

    /**
     * Returns whether the closed interval [{@code aStart}, {@code aEnd}]
     * overlaps the closed interval [{@code bStart}, {@code bEnd}]. Shared by
     * {@link Deadline#overlaps} and {@link Event#overlaps}, which each just
     * supply their own two range endpoints: a deadline's range is its own
     * {@link #rangeStart()} to {@link #rangeEnd()}, and an event's runs from
     * its start's {@code rangeStart()} to its end's {@code rangeEnd()}.
     *
     * @param aStart start of the first interval.
     * @param aEnd   end of the first interval.
     * @param bStart start of the second interval.
     * @param bEnd   end of the second interval.
     * @return whether the two intervals share at least one instant.
     */
    public static boolean rangesOverlap(
            LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }

    /**
     * Returns whether {@code other} is a {@code TaskDateTime} for the same
     * date and the same time, or likewise has no time. A date-only value is
     * therefore not equal to the same date at midnight, because the user
     * gave different information in each case.
     *
     * @param other the object to compare with.
     * @return true if both have the same date and the same time or lack of one.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TaskDateTime otherDateTime)) {
            return false;
        }
        return date.equals(otherDateTime.date) && Objects.equals(time, otherDateTime.time);
    }

    /**
     * Returns a hash code consistent with {@link #equals}, as the contract
     * of {@link Object#hashCode} requires whenever equals is overridden.
     */
    @Override
    public int hashCode() {
        return Objects.hash(date, time);
    }

    /**
     * Returns this value in the user-facing display format:
     * {@code MMM dd yyyy} for a date-only value, or {@code MMM dd yyyy HHmm}
     * when a time is present.
     *
     * @return the display form, e.g. {@code Dec 02 2019 1800}.
     */
    @Override
    public String toString() {
        return time == null
                ? date.format(DISPLAY_DATE_ONLY)
                : LocalDateTime.of(date, time).format(DISPLAY_DATE_TIME);
    }
}
