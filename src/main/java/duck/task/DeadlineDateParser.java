package duck.task;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Parses canonical saved dates and user-entered deadline dates.
 */
public final class DeadlineDateParser {
    /** Exact, ASCII-only shape accepted for deadline dates. */
    private static final Pattern DEADLINE_DATE_PATTERN =
            Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");

    /** Strict formatter used to parse deadline dates. */
    private static final DateTimeFormatter DEADLINE_DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    private DeadlineDateParser() {
    }

    /**
     * Parses a canonical deadline date. The shape check rejects abbreviated, signed,
     * extended, and non-ASCII years before strict calendar validation is attempted.
     *
     * @param dateText Date in yyyy-MM-dd format.
     * @return Parsed date.
     * @throws DateTimeParseException If the text is not a valid date from year 0001 to 9999.
     */
    public static LocalDate parse(String dateText) {
        if (!DEADLINE_DATE_PATTERN.matcher(dateText).matches()) {
            throw createInvalidDateException(dateText);
        }

        LocalDate date = LocalDate.parse(dateText, DEADLINE_DATE_FORMAT);
        if (date.getYear() == 0) {
            throw createInvalidDateException(dateText);
        }
        return date;
    }

    /**
     * Parses a deadline date entered in a command. In addition to the canonical date
     * format, this accepts a three-letter English weekday and resolves it to the next
     * occurrence after the reference date.
     *
     * @param dateText Canonical date or abbreviated weekday.
     * @param currentDate Date from which an abbreviated weekday is resolved.
     * @return Parsed or resolved deadline date.
     * @throws DateTimeParseException If the text is not a supported deadline date.
     */
    public static LocalDate parseCommandDate(String dateText, LocalDate currentDate) {
        try {
            return parse(dateText);
        } catch (DateTimeParseException ignored) {
            // An abbreviated weekday remains a possible valid command date.
        }

        DayOfWeek weekday = parseWeekday(dateText);
        return currentDate.with(TemporalAdjusters.next(weekday));
    }

    /** Returns the weekday represented by a supported three-letter abbreviation. */
    private static DayOfWeek parseWeekday(String dateText) {
        return switch (dateText.toLowerCase(Locale.ROOT)) {
            case "mon" -> DayOfWeek.MONDAY;
            case "tue" -> DayOfWeek.TUESDAY;
            case "wed" -> DayOfWeek.WEDNESDAY;
            case "thu" -> DayOfWeek.THURSDAY;
            case "fri" -> DayOfWeek.FRIDAY;
            case "sat" -> DayOfWeek.SATURDAY;
            case "sun" -> DayOfWeek.SUNDAY;
            default -> throw createInvalidDateException(dateText);
        };
    }

    /** Creates a consistent exception for deadline-specific validation failures. */
    private static DateTimeParseException createInvalidDateException(String dateText) {
        return new DateTimeParseException("Invalid deadline date.", dateText, 0);
    }
}
