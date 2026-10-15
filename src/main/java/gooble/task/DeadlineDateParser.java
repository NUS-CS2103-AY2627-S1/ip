package gooble.task;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Map;

import gooble.GoobleException;

/**
 * Parses, formats, and stores the date and optional time for deadline tasks.
 */
public final class DeadlineDateParser {
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter SLASH_DATE = DateTimeFormatter.ofPattern("d/M/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final String INVALID_DATE_MESSAGE = "Please use a proper date format. "
            + "Examples: 2019-12-02 or 2/12/2019 1800";
    private static final Map<Integer, LocalDate> CHINESE_NEW_YEAR_DATES = Map.ofEntries(
            Map.entry(2018, LocalDate.of(2018, 2, 16)),
            Map.entry(2019, LocalDate.of(2019, 2, 5)),
            Map.entry(2020, LocalDate.of(2020, 1, 25)),
            Map.entry(2021, LocalDate.of(2021, 2, 12)),
            Map.entry(2022, LocalDate.of(2022, 2, 1)),
            Map.entry(2023, LocalDate.of(2023, 1, 22)),
            Map.entry(2024, LocalDate.of(2024, 2, 10)),
            Map.entry(2025, LocalDate.of(2025, 1, 29)),
            Map.entry(2026, LocalDate.of(2026, 2, 17)),
            Map.entry(2027, LocalDate.of(2027, 2, 6)),
            Map.entry(2028, LocalDate.of(2028, 1, 26)),
            Map.entry(2029, LocalDate.of(2029, 2, 13)),
            Map.entry(2030, LocalDate.of(2030, 2, 3)),
            Map.entry(2031, LocalDate.of(2031, 1, 23)),
            Map.entry(2032, LocalDate.of(2032, 2, 11)),
            Map.entry(2033, LocalDate.of(2033, 1, 31)),
            Map.entry(2034, LocalDate.of(2034, 2, 19)),
            Map.entry(2035, LocalDate.of(2035, 2, 8)));

    /** Prevents construction of this static utility class. */
    private DeadlineDateParser() {
    }

    /**
     * Parses an ISO date or a day/month/year date, with an optional 24-hour time.
     *
     * @param input the deadline text supplied by the user
     * @return a deadline value with a date and, when supplied, a time
     * @throws GoobleException if the input does not use a supported format
     */
    public static DeadlineDate parse(String input) throws GoobleException {
        if (input == null) {
            throw new GoobleException(INVALID_DATE_MESSAGE);
        }
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 1 || parts.length > 2 || parts[0].isBlank()) {
            throw new GoobleException(INVALID_DATE_MESSAGE);
        }

        try {
            LocalDate date = parseDate(parts[0]);
            LocalTime time = parts.length == 2 ? LocalTime.parse(parts[1], TIME) : null;
            // Date parsing and the record contract guarantee a non-null date.
            assert date != null;
            return new DeadlineDate(date, time);
        } catch (DateTimeException e) {
            throw new GoobleException(INVALID_DATE_MESSAGE);
        }
    }

    /** Formats a deadline value for the task list. */
    public static String format(DeadlineDate deadline) {
        String formattedDate = deadline.date().format(DISPLAY_DATE);
        return deadline.time() == null ? formattedDate
                : formattedDate + ", " + deadline.time().format(DISPLAY_TIME);
    }

    /** Returns whether the supplied deadline falls on Valentine's Day. */
    public static boolean isValentinesDay(DeadlineDate deadline) {
        return deadline.date().getMonth() == Month.FEBRUARY && deadline.date().getDayOfMonth() == 14;
    }

    /** Returns whether the supplied deadline falls on Chinese New Year. */
    public static boolean isChineseNewYear(DeadlineDate deadline) {
        LocalDate date = deadline.date();
        return date.equals(CHINESE_NEW_YEAR_DATES.get(date.getYear()));
    }

    /** Parses a date using slash-separated or ISO notation based on its separator. */
    private static LocalDate parseDate(String text) {
        return text.contains("/") ? LocalDate.parse(text, SLASH_DATE) : LocalDate.parse(text, ISO_DATE);
    }

    /** Holds the typed date and optional time that make up a deadline. */
    public record DeadlineDate(LocalDate date, LocalTime time) {
        /** Creates a non-null deadline date with an optional deadline time. */
        public DeadlineDate {
            if (date == null) {
                throw new IllegalArgumentException("Deadline date cannot be null.");
            }
        }
    }
}
