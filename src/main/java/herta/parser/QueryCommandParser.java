package herta.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import herta.command.UpcomingCommand;
import herta.exception.HertaException;

/**
 * Parses arguments shared by Herta's query commands.
 */
final class QueryCommandParser {
    private static final String FILTER_DATE_MARKER = "/on";
    private static final String FILTER_USAGE = "filter /on <date>";
    private static final String MISSING_FIND_KEYWORD_ERROR =
            "Find something specific. Use: find <keyword>.";
    private static final String DUPLICATE_FILTER_MARKER_ERROR =
            "One %s marker is enough. Try: %s.";
    private static final String MISSING_FILTER_MARKER_ERROR =
            "Your filter needs %s. Try: %s.";
    private static final String MISSING_FILTER_DATE_ERROR =
            "You gave me %s without a date. Try: %s.";
    private static final String INVALID_FILTER_DATE_ERROR =
            "That date won't do. Try 2019-10-15 or 15/10/2019.";
    private static final String DATE_SORT_COMMAND = "sort date";
    private static final String UNSUPPORTED_SORT_OPTION_ERROR =
            "That sorting option is not supported. Try: %s.";
    /** Parses the keyword from a find command. */
    String parseFindKeyword(String input) throws HertaException {
        String keyword = CommandType.FIND.extractArguments(input);
        if (keyword.isEmpty()) {
            throw new HertaException(MISSING_FIND_KEYWORD_ERROR);
        }
        return keyword;
    }

    /** Parses the date from a filter command. */
    LocalDate parseFilterDate(String input) throws HertaException {
        Objects.requireNonNull(input, "A filter command cannot be null.");
        String arguments = CommandType.FILTER.extractArguments(input);
        int markerCount = CommandTokenizer.countMarker(arguments, FILTER_DATE_MARKER);
        if (markerCount > 1) {
            throw new HertaException(DUPLICATE_FILTER_MARKER_ERROR.formatted(
                    FILTER_DATE_MARKER, FILTER_USAGE));
        }
        String[] filterParts = arguments.split("[ \\t]+", 2);
        if (!FILTER_DATE_MARKER.equals(filterParts[0])) {
            throw new HertaException(MISSING_FILTER_MARKER_ERROR.formatted(
                    FILTER_DATE_MARKER, FILTER_USAGE));
        }
        if (filterParts.length == 1 || filterParts[1].isBlank()) {
            throw new HertaException(MISSING_FILTER_DATE_ERROR.formatted(
                    FILTER_DATE_MARKER, FILTER_USAGE));
        }
        return parseFilterDateInput(filterParts[1]);
    }

    /** Parses a supported user date while retaining the filter-specific error message. */
    private LocalDate parseFilterDateInput(String dateInput) throws HertaException {
        try {
            return DateTimeParser.parseUserDate(dateInput);
        } catch (DateTimeParseException e) {
            throw new HertaException(INVALID_FILTER_DATE_ERROR);
        }
    }

    /** Parses the number of days from an upcoming command. */
    int parseUpcomingDays(String input) throws HertaException {
        String daysInput = CommandType.UPCOMING.extractArguments(input);
        if (!ParserLimits.isBoundedDecimal(daysInput)) {
            throw createInvalidUpcomingDaysException();
        }
        try {
            int days = Integer.parseInt(daysInput);
            if (days <= 0 || days > ParserLimits.MAX_UPCOMING_DAYS) {
                throw createInvalidUpcomingDaysException();
            }
            return days;
        } catch (NumberFormatException e) {
            throw createInvalidUpcomingDaysException();
        }
    }

    /** Validates a sort command. */
    void validateSortCommand(String input) throws HertaException {
        if (!CommandType.SORT.extractArguments(input).equals("date")) {
            throw new HertaException(UNSUPPORTED_SORT_OPTION_ERROR.formatted(DATE_SORT_COMMAND));
        }
    }

    /** Creates the stable error used for invalid upcoming-day arguments. */
    private HertaException createInvalidUpcomingDaysException() {
        return new HertaException(UpcomingCommand.INVALID_UPCOMING_DAYS_ERROR);
    }
}
