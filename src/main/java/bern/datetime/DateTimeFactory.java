package bern.datetime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses command arguments and saved date-time fields using supported date and time patterns.
 * Textual month names and AM/PM markers are interpreted using the system's default locale.
 */
public class DateTimeFactory {
    private static final String[] MONTH_FORMATS_SHORT = {"M", "MM"};
    private static final String[] MONTH_FORMATS_LONG = {"MMM", "MMMM"};
    private static final String[] YEAR_FORMATS = {"y"}; // TODO: accommodate for "yy"
    private static final String[] DATE_SEPARATORS_SHORT = {"/", ".", "-"};

    /**
     * Lists accepted date patterns in the order used to resolve ambiguous inputs.
     */
    private static final List<String> DATE_FORMATS = DateTimeFactory.generateDateFormats();

    /**
     * Lists accepted time patterns, including twelve-hour and twenty-four-hour forms.
     */
    private static final List<String> TIME_FORMATS = List.of(
            "ha", // 5pm
            "h:ma", "h.ma", // 5:29pm, 5.29pm
            "k:m", "k.m" // 17:29, 17.29
    );

    /**
     * Creates a date-time factory with no instance state.
     */
    public DateTimeFactory() {
    }

    /**
     * Parses a date, a time, or a date with a time before or after it, separated by a space.
     * A time without a date uses today's date in the system's default time zone.
     * A date without a time retains an unspecified time component.
     *
     * @param dateTimeString The date and/or time to parse in a supported input format.
     * @return The parsed date and optional time.
     * @throws DateTimeParseException If the input does not match a supported date or time format.
     */
    public static DateTime parseDateTime(String dateTimeString) throws DateTimeParseException {
        String[] tokens = dateTimeString.split(" ");
        LocalTime localTime = null;
        LocalDate localDate = null;

        if (isTime(dateTimeString)) {
            localTime = parseTime(dateTimeString);
            localDate = LocalDate.now();
        } else if (isTime(tokens[0])) {
            localTime = parseTime(tokens[0]);
            localDate = parseDate(String.join(" ", Arrays.stream(tokens, 1, tokens.length).toList()));
        } else if (isTime(tokens[tokens.length - 1])) {
            localTime = parseTime(tokens[tokens.length - 1]);
            localDate = parseDate(String.join(" ", Arrays.stream(tokens, 0, tokens.length - 1).toList()));
        } else {
            localDate = parseDate(dateTimeString);
        }

        return new DateTime(localDate, localTime);
    }

    /**
     * Parses a saved date with an optional time separated by {@code " @ "}.
     *
     * @param dataString The saved date and optional time.
     * @return The parsed date and optional time.
     * @throws IllegalArgumentException If splitting the saved string yields neither one nor two parts.
     * @throws DateTimeParseException If the date or time cannot be parsed.
     */
    public static DateTime makeDateFromDataString(String dataString) {
        String[] tokens = dataString.split(DateTime.SEPARATOR);

        if (tokens.length == 1) {
            return new DateTime(parseDate(tokens[0]), null);
        }
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Data has insufficient arguments for date and time");
        }

        return new DateTime(parseDate(tokens[0]), parseTime(tokens[1]));
    }

    /**
     * Parses a date using the first supported pattern that accepts the input.
     *
     * @param dateString The date text to parse.
     * @return The parsed calendar date.
     * @throws DateTimeParseException If no supported date pattern accepts the input.
     */
    private static LocalDate parseDate(String dateString) throws DateTimeParseException {
        LocalDate date = null;
        DateTimeParseException firstSeenException = null;
        for (String format : DATE_FORMATS) {
            try {
                date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
                break;
            } catch (DateTimeParseException e) {
                firstSeenException = firstSeenException == null ? e : firstSeenException;
            }
        }

        if (date == null) {
            assert firstSeenException != null;
            throw firstSeenException;
        }
        return date;
    }

    /**
     * Parses a time using the first supported pattern that accepts the input.
     *
     * @param timeString The time text to parse.
     * @return The parsed time.
     * @throws DateTimeParseException If no supported time pattern accepts the input.
     */
    private static LocalTime parseTime(String timeString) throws DateTimeParseException {
        LocalTime time = null;
        DateTimeParseException firstSeenException = null;
        for (String format : TIME_FORMATS) {
            try {
                time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
                break;
            } catch (DateTimeParseException e) {
                firstSeenException = firstSeenException == null ? e : firstSeenException;
            }
        }

        if (time == null) {
            throw firstSeenException;
        }

        return time;
    }

    /**
     * Returns whether the string matches a supported time format.
     *
     * @param timeString The time text to check.
     * @return {@code true} if a supported time pattern accepts the input; {@code false} otherwise.
     */
    private static boolean isTime(String timeString) {
        try {
            parseTime(timeString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Builds the supported date patterns from month, year, and separator variants.
     * Numeric months support day-month-year and year-month-day order; textual months also support month-day-year.
     *
     * @return The date patterns in parsing order.
     */
    private static ArrayList<String> generateDateFormats() {
        ArrayList<String> formats = new ArrayList<>();
        for (String yearFormat : YEAR_FORMATS) {
            for (String monthFormat : MONTH_FORMATS_SHORT) {
                for (String dateSeparator : DATE_SEPARATORS_SHORT) {
                    // Little-Endian
                    formats.add(String.format("%s%s%s%s%s",
                            "d",
                            dateSeparator,
                            monthFormat,
                            dateSeparator,
                            yearFormat
                    ));
                    // Big-Endian
                    formats.add(String.format("%s%s%s%s%s",
                            yearFormat,
                            dateSeparator,
                            monthFormat,
                            dateSeparator,
                            "d"
                    ));
                }
            }

            for (String monthFormat : MONTH_FORMATS_LONG) {
                // Little-Endian
                formats.add(String.format("%s %s %s",
                        "d",
                        monthFormat,
                        yearFormat
                ));

                // Big-Endian
                formats.add(String.format("%s %s %s",
                        yearFormat,
                        monthFormat,
                        "d"
                ));

                // Middle-Endian
                formats.add(String.format("%s %s %s",
                        monthFormat,
                        "d",
                        yearFormat
                ));
            }
        }
        return formats;
    }
}
