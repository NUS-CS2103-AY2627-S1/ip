package zabud.commands.tokens;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

import zabud.commands.Token;

/**
 * Parses a date, time, or date-and-time command value into separate fields.
 */
public final class DateTimeToken extends Token {
    /**
     * Formatter for user-entered day/month/year dates.
     */
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    /**
     * Parsed date, or {@code null} when the value contains only a time.
     */
    private LocalDate date;
    /**
     * Parsed time, or {@code null} when the value contains only a date.
     */
    private LocalTime time;
    /**
     * Whether parsing has already been attempted.
     */
    private boolean isParsed;

    /**
     * Creates a token that accepts a date, time, or date followed by a time.
     *
     * @param name token name used in command syntax.
     * @param value raw date/time value.
     */
    public DateTimeToken(String name, String value) {
        super(name, value);
    }

    /**
     * Returns the parsed date.
     *
     * @return parsed date, or {@code null} when the value contains no date.
     */
    public LocalDate getDate() {
        parse();
        return date;
    }

    /**
     * Returns the parsed time.
     *
     * @return parsed time, or {@code null} when the value contains no time.
     */
    public LocalTime getTime() {
        parse();
        return time;
    }

    private void parse() {
        if (isParsed) {
            return;
        }
        isParsed = true;
        try {
            String[] parts = value.split(" ");
            if (parts.length == 1 && parts[0].matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
                date = LocalDate.parse(parts[0], DATE);
            } else if (parts.length == 1 && parts[0].matches("\\d{3,4}")) {
                time = parseTime(parts[0]);
            } else if (parts.length == 2 && parts[0].matches("\\d{1,2}/\\d{1,2}/\\d{4}")
                    && parts[1].matches("\\d{3,4}")) {
                date = LocalDate.parse(parts[0], DATE);
                time = parseTime(parts[1]);
            }
        } catch (DateTimeException | NumberFormatException exception) {
            date = null;
            time = null;
        }
    }

    private LocalTime parseTime(String text) {
        String normalized = text.length() == 3 ? "0" + text : text;
        return LocalTime.of(Integer.parseInt(normalized.substring(0, 2)), Integer.parseInt(normalized.substring(2)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        parse();
        return !value.isBlank() && (date != null || time != null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String hint() {
        return "DATE_OR_TIME";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String requirement() {
        return "enter a date as DD/MM/YYYY, a 24-hour time as HHMM, "
                + "or both as DD/MM/YYYY HHMM.";
    }
}
