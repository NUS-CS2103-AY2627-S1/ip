package oz.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oz.exception.OzException;
import oz.task.RecurringEvent;
import oz.task.TaskDateTime;

/**
 * Parses weekly recurring-event command arguments.
 */
final class RecurringEventParser {
    /** Usage message for adding a recurring event. */
    private static final String USAGE_MESSAGE =
            "Use: recurring <description> /on <date> /start <time> /end <time> "
                    + "/every <interval> week|weeks [/until <date>].";

    /** Error shown when recurring parameters are out of order. */
    private static final String PARAMETER_ORDER_MESSAGE =
            "Recurring parameters are out of order. ";

    /** Error shown when a recurring event description is missing. */
    private static final String EMPTY_DESCRIPTION_MESSAGE =
            "The description of a recurring event cannot be empty.";

    /** Error shown when a recurrence unit is not weekly. */
    private static final String INVALID_UNIT_MESSAGE =
            "The recurrence unit must be week or weeks.";

    /** Error shown when a recurrence interval is not positive. */
    private static final String INVALID_INTERVAL_MESSAGE =
            "The recurrence interval must be a positive whole number.";

    /** Pattern parsing a weekly recurring event and optional end date. */
    private static final Pattern ARGUMENTS_PATTERN = Pattern.compile(
            "^(?<description>.+?)\\s+/on\\s+(?<eventDate>.+?)"
                    + "\\s+/start\\s+(?<startTime>.+?)\\s+/end\\s+(?<endTime>.+?)"
                    + "\\s+/every\\s+(?<interval>\\S+)\\s+(?<unit>\\S+)"
                    + "(?:\\s+/until\\s+(?<untilDate>.+))?$");

    /** Prevents instantiation of this utility class. */
    private RecurringEventParser() {
    }

    /**
     * Parses a recurring event after validating its flags and fields.
     *
     * @param arguments Recurring-event command arguments.
     * @return Parsed recurring event.
     * @throws OzException If the arguments are invalid.
     */
    static RecurringEvent parse(String arguments) throws OzException {
        validateFlags(arguments);

        Matcher argumentsMatcher = ARGUMENTS_PATTERN.matcher(arguments);
        if (!argumentsMatcher.matches()) {
            throw new OzException(USAGE_MESSAGE);
        }

        String description = argumentsMatcher.group("description").trim();
        if (description.isEmpty()) {
            throw new OzException(EMPTY_DESCRIPTION_MESSAGE);
        }

        String unitArgument = argumentsMatcher.group("unit").trim();
        validateRecurrenceUnit(unitArgument);
        return createRecurringEvent(argumentsMatcher, description);
    }

    /**
     * Validates recurring-event flag presence, uniqueness, and order.
     *
     * @param arguments Recurring-event command arguments.
     * @throws OzException If required flags are missing, repeated, or misplaced.
     */
    private static void validateFlags(String arguments) throws OzException {
        CommandArgumentValidator.validateNoDuplicateFlags(
                arguments, "/on", "/start", "/end", "/every", "/until");

        int onIndex = CommandArgumentValidator.findFlagIndex(arguments, "/on");
        int startIndex = CommandArgumentValidator.findFlagIndex(arguments, "/start");
        int endIndex = CommandArgumentValidator.findFlagIndex(arguments, "/end");
        int everyIndex = CommandArgumentValidator.findFlagIndex(arguments, "/every");
        int untilIndex = CommandArgumentValidator.findFlagIndex(arguments, "/until");
        if (onIndex == -1 || startIndex == -1 || endIndex == -1 || everyIndex == -1) {
            throw new OzException(USAGE_MESSAGE);
        }

        boolean areRequiredFlagsOrdered = onIndex < startIndex
                && startIndex < endIndex
                && endIndex < everyIndex;
        boolean isUntilFlagOrdered = untilIndex == -1 || everyIndex < untilIndex;
        if (!areRequiredFlagsOrdered || !isUntilFlagOrdered) {
            throw new OzException(PARAMETER_ORDER_MESSAGE + USAGE_MESSAGE);
        }
    }

    /**
     * Validates that a recurrence uses the supported weekly unit.
     *
     * @param unitArgument Recurrence unit argument.
     * @throws OzException If the unit is not week or weeks.
     */
    private static void validateRecurrenceUnit(String unitArgument) throws OzException {
        boolean isWeekly = unitArgument.equalsIgnoreCase("week")
                || unitArgument.equalsIgnoreCase("weeks");
        if (!isWeekly) {
            throw new OzException(INVALID_UNIT_MESSAGE);
        }
    }

    /**
     * Creates a recurring event from successfully matched command fields.
     *
     * @param argumentsMatcher Matcher containing the recurring-event fields.
     * @param description Validated event description.
     * @return Parsed recurring event.
     * @throws OzException If an interval, date, time, or event rule is invalid.
     */
    private static RecurringEvent createRecurringEvent(Matcher argumentsMatcher,
            String description) throws OzException {
        int weekInterval = parseRecurrenceInterval(
                argumentsMatcher.group("interval").trim());
        LocalDate eventDate = TaskDateTime.parseDate(
                argumentsMatcher.group("eventDate").trim());
        LocalTime startTime = TaskDateTime.parseTime(
                argumentsMatcher.group("startTime").trim());
        LocalTime endTime = TaskDateTime.parseTime(
                argumentsMatcher.group("endTime").trim());
        TaskDateTime firstStartDateTime = TaskDateTime.fromDateTime(
                LocalDateTime.of(eventDate, startTime));
        TaskDateTime firstEndDateTime = TaskDateTime.fromDateTime(
                LocalDateTime.of(eventDate, endTime));
        LocalDate untilDate = parseOptionalUntilDate(
                argumentsMatcher.group("untilDate"));
        return new RecurringEvent(description, firstStartDateTime,
                firstEndDateTime, weekInterval, untilDate);
    }

    /**
     * Parses an optional recurrence end date.
     *
     * @param argument Raw end-date argument, or null when omitted.
     * @return Parsed end date, or null for an indefinite recurrence.
     * @throws OzException If the supplied date is invalid.
     */
    private static LocalDate parseOptionalUntilDate(String argument) throws OzException {
        return argument == null ? null : TaskDateTime.parseDate(argument.trim());
    }

    /**
     * Parses and validates a positive recurrence interval.
     *
     * @param argument Raw recurrence interval.
     * @return Positive interval in weeks.
     * @throws OzException If the argument is not a positive whole number.
     */
    private static int parseRecurrenceInterval(String argument) throws OzException {
        if (!argument.matches("\\d+")) {
            throw new OzException(INVALID_INTERVAL_MESSAGE);
        }

        try {
            int interval = Integer.parseInt(argument);
            if (interval <= 0) {
                throw new OzException(INVALID_INTERVAL_MESSAGE);
            }
            return interval;
        } catch (NumberFormatException exception) {
            throw new OzException(INVALID_INTERVAL_MESSAGE);
        }
    }
}
