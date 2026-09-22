package stewie.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

/**
 * Represents a calendar date backed by a {@link LocalDate}.
 * Accepts supported numeric and English textual formats and displays dates as {@code dd MMM uuuu}.
 */
public class Date {
    private static final List<DateTimeFormatter> FORMATTERS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d.M.uuuu").withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM d, uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT),

        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMMM d, uuuu")
            .toFormatter(Locale.ENGLISH)
            .withResolverStyle(ResolverStyle.STRICT)
    );
    private final LocalDate date;

    /**
     * Creates a date by parsing a supported numeric or English textual representation.
     *
     * @param s Date text, with optional surrounding whitespace.
     * @throws IllegalArgumentException If the text is null, blank, unparseable, or outside years 0001 to 9999.
     */
    public Date(String s) {
        this.date = this.parseDate(s);
        assert this.date != null : "A Date must contain a parsed LocalDate";
    }

    /**
     * Returns a calendar date parsed using the supported date formats.
     *
     * @param d Date text, with optional surrounding whitespace.
     * @return Parsed calendar date.
     * @throws IllegalArgumentException If the text is null, blank, unparseable, or outside years 0001 to 9999.
     */
    private LocalDate parseDate(String d) {
        if (d == null || d.isBlank()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }

        String value = d.trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                LocalDate parsed = LocalDate.parse(value, formatter);
                if (parsed.getYear() < 1 || parsed.getYear() > 9999) {
                    throw new IllegalArgumentException("Use a year between 0001 and 9999.");
                }
                return parsed;
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }
        throw new IllegalArgumentException("Use a real calendar date, such as 2026-08-28 or 28/8/2026 (dates only).");
    }

    /**
     * Checks whether this date strictly precedes another date.
     *
     * @param other Date to compare with.
     * @return True when this date is earlier.
     */
    public boolean isBefore(Date other) {
        return date.isBefore(other.date);
    }

    /**
     * Returns this date formatted as {@code dd MMM uuuu} using English month names.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM uuuu", Locale.ENGLISH);
        return date.format(formatter);
    }
}
