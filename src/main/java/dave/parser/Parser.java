package dave.parser;

import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dave.command.Command;
import dave.exception.DaveCommandException;
import dave.task.Deadline;
import dave.task.Event;
import dave.task.Todo;

/**
 * Deals with making sense of user commands and parsing input arguments.
 */
public class Parser {

    /** Reserved delimiter character used for disk storage columns. */
    private static final String RESERVED_STORAGE_CHAR = "|";

    /** Pattern matching the /by delimiter. */
    private static final Pattern BY_FLAG_PATTERN = Pattern.compile("(?i)(?:^|\\s+)/by(?:\\s+|$)");
    /** Pattern matching the /from delimiter. */
    private static final Pattern FROM_FLAG_PATTERN = Pattern.compile("(?i)(?:^|\\s+)/from(?:\\s+|$)");
    /** Pattern matching the /to delimiter. */
    private static final Pattern TO_FLAG_PATTERN = Pattern.compile("(?i)(?:^|\\s+)/to(?:\\s+|$)");

    /** Prefix for snooze target date-time. */
    private static final String SNOOZE_TO_PREFIX = "/to ";
    /** Prefix for snooze alternative deadline target date-time. */
    private static final String SNOOZE_BY_PREFIX = "/by ";
    /** Prefix for snooze event start boundary. */
    private static final String SNOOZE_FROM_PREFIX = "/from ";

    /**
     * Parses the command type from the full user input string.
     *
     * @param fullCommand Full input string entered by the user.
     * @return Corresponding Command enum value.
     */
    public static Command parseCommand(String fullCommand) {
        String[] parts = fullCommand.trim().split("\\s+", 2);
        return Command.from(parts[0]);
    }

    /**
     * Extracts the arguments portion following the command keyword.
     *
     * @param fullCommand Full input string entered by the user.
     * @return Argument string following the command keyword, or an empty string if none.
     */
    public static String parseArguments(String fullCommand) {
        String[] parts = fullCommand.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses a Todo task from the provided arguments string.
     *
     * @param arguments Argument string containing the task description.
     * @return Newly created Todo task.
     * @throws DaveCommandException If the description is empty or contains reserved characters.
     */
    public static Todo parseTodo(String arguments) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! The description of a todo cannot be empty.");
        }
        validateNoReservedCharacters(trimmed, "Task description");
        return new Todo(trimmed);
    }

    /**
     * Parses a Deadline task from the provided arguments string.
     *
     * @param arguments Argument string containing description and '/by' date/time.
     * @return Newly created Deadline task.
     * @throws DaveCommandException If delimiters are missing, duplicated, or description is empty.
     */
    public static Deadline parseDeadline(String arguments) {
        int byCount = countMatches(arguments, BY_FLAG_PATTERN);
        if (byCount > 1) {
            throw new DaveCommandException("Pardon, Sire! The /by delimiter cannot be specified multiple times.");
        }
        if (byCount == 0) {
            throw new DaveCommandException("Pardon, Sire! A deadline requires an appointed time (/by [time]).");
        }

        Matcher matcher = BY_FLAG_PATTERN.matcher(arguments);
        if (!matcher.find()) {
            throw new DaveCommandException("Pardon, Sire! A deadline requires an appointed time (/by [time]).");
        }

        String description = arguments.substring(0, matcher.start()).trim();
        String byString = arguments.substring(matcher.end()).trim();

        if (description.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! The description of a deadline cannot be empty.");
        }
        if (byString.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! A deadline requires an appointed time (/by [time]).");
        }

        validateNoReservedCharacters(description, "Task description");
        ParsedDateTime parsed = DateTimeParser.parse(byString);
        return new Deadline(description, parsed.getDateTime(), parsed.hasTime());
    }

    /**
     * Parses an Event task from the provided arguments string.
     * Supports either delimiter ordering (/from before /to, or /to before /from).
     *
     * @param arguments Argument string containing description, '/from' and '/to' dates/times.
     * @return Newly created Event task.
     * @throws DaveCommandException If delimiters are missing, duplicated, description is empty, or dates invalid.
     */
    public static Event parseEvent(String arguments) {
        int fromCount = countMatches(arguments, FROM_FLAG_PATTERN);
        int toCount = countMatches(arguments, TO_FLAG_PATTERN);

        if (fromCount > 1) {
            throw new DaveCommandException("Pardon, Sire! The /from delimiter cannot be specified multiple times.");
        }
        if (toCount > 1) {
            throw new DaveCommandException("Pardon, Sire! The /to delimiter cannot be specified multiple times.");
        }
        if (fromCount == 0 || toCount == 0) {
            throw new DaveCommandException("Pardon, Sire! An event requires both /from [time] and /to [time].");
        }

        Matcher fromMatcher = FROM_FLAG_PATTERN.matcher(arguments);
        Matcher toMatcher = TO_FLAG_PATTERN.matcher(arguments);
        fromMatcher.find();
        toMatcher.find();

        String description;
        String fromString;
        String toString;

        if (fromMatcher.start() < toMatcher.start()) {
            description = arguments.substring(0, fromMatcher.start()).trim();
            fromString = arguments.substring(fromMatcher.end(), toMatcher.start()).trim();
            toString = arguments.substring(toMatcher.end()).trim();
        } else {
            description = arguments.substring(0, toMatcher.start()).trim();
            toString = arguments.substring(toMatcher.end(), fromMatcher.start()).trim();
            fromString = arguments.substring(fromMatcher.end()).trim();
        }

        if (description.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! The description of an event cannot be empty.");
        }
        if (fromString.isEmpty() || toString.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! An event requires both /from [time] and /to [time].");
        }

        validateNoReservedCharacters(description, "Task description");
        ParsedDateTime fromParsed = DateTimeParser.parse(fromString);
        ParsedDateTime toParsed = DateTimeParser.parse(toString);

        return new Event(description,
                fromParsed.getDateTime(), fromParsed.hasTime(),
                toParsed.getDateTime(), toParsed.hasTime());
    }

    /**
     * Parses a 1-based task index string into a 0-based integer index.
     *
     * @param arguments Argument string containing the 1-based task number.
     * @return 0-based integer index.
     * @throws DaveCommandException If the argument is not a valid positive integer.
     */
    public static int parseIndex(String arguments) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty() || trimmed.contains(" ")) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }
        try {
            int itemNumber = Integer.parseInt(trimmed);
            if (itemNumber <= 0) {
                throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
            }
            return itemNumber - 1;
        } catch (NumberFormatException e) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }
    }

    /**
     * Parses the search keyword from the find command arguments string.
     *
     * @param arguments Argument string containing the keyword.
     * @return Search keyword.
     * @throws DaveCommandException If the keyword is empty or contains reserved characters.
     */
    public static String parseFind(String arguments) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! The search keyword cannot be empty.");
        }
        validateNoReservedCharacters(trimmed, "Search keyword");
        return trimmed;
    }

    /**
     * Parses a snooze command argument string into a SnoozeRequest object.
     *
     * @param arguments Argument string containing the task number and optional snooze parameters.
     * @return Parsed SnoozeRequest containing target task index and snooze details.
     * @throws DaveCommandException If the arguments string is invalid or cannot be parsed.
     */
    public static SnoozeRequest parseSnooze(String arguments) {
        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! That is an invalid scroll number.");
        }

        String[] parts = trimmed.split("\\s+", 2);
        int index = parseIndex(parts[0]);

        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            return new SnoozeRequest(index, 1, ChronoUnit.DAYS);
        }

        String spec = parts[1].trim();

        if (countMatches(spec, FROM_FLAG_PATTERN) > 1) {
            throw new DaveCommandException("Pardon, Sire! The /from delimiter cannot be specified multiple times.");
        }
        if (countMatches(spec, TO_FLAG_PATTERN) > 1) {
            throw new DaveCommandException("Pardon, Sire! The /to delimiter cannot be specified multiple times.");
        }
        if (countMatches(spec, BY_FLAG_PATTERN) > 1) {
            throw new DaveCommandException("Pardon, Sire! The /by delimiter cannot be specified multiple times.");
        }

        if (spec.contains(SNOOZE_FROM_PREFIX) || spec.startsWith("/from")) {
            return parseEventSnooze(index, spec);
        }

        if (spec.startsWith(SNOOZE_TO_PREFIX) || spec.startsWith(SNOOZE_BY_PREFIX)) {
            return parseTargetSnooze(index, spec);
        }

        return parseRelativeSnooze(index, spec);
    }

    /**
     * Parses event start and end boundaries from a snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string containing /from and /to delimiters.
     * @return SnoozeRequest configured for event rescheduling.
     * @throws DaveCommandException If delimiters are missing or boundaries cannot be parsed.
     */
    private static SnoozeRequest parseEventSnooze(int index, String spec) {
        String toPrefix = "/to ";
        if (!spec.contains(toPrefix) && !spec.contains("/to")) {
            throw new DaveCommandException("Pardon, Sire! Rescheduling an event requires /from [time] and /to [time].");
        }

        Matcher fromMatcher = FROM_FLAG_PATTERN.matcher(spec);
        Matcher toMatcher = TO_FLAG_PATTERN.matcher(spec);
        if (!fromMatcher.find() || !toMatcher.find()) {
            throw new DaveCommandException("Pardon, Sire! Rescheduling an event requires /from [time] and /to [time].");
        }

        String fromStr;
        String toStr;
        if (fromMatcher.start() < toMatcher.start()) {
            fromStr = spec.substring(fromMatcher.end(), toMatcher.start()).trim();
            toStr = spec.substring(toMatcher.end()).trim();
        } else {
            toStr = spec.substring(toMatcher.end(), fromMatcher.start()).trim();
            fromStr = spec.substring(fromMatcher.end()).trim();
        }

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! Rescheduling an event requires /from [time] and /to [time].");
        }

        ParsedDateTime fromParsed = DateTimeParser.parse(fromStr);
        ParsedDateTime toParsed = DateTimeParser.parse(toStr);
        return new SnoozeRequest(index, fromParsed, toParsed);
    }

    /**
     * Parses a target date-time from a /to or /by snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string starting with /to or /by.
     * @return SnoozeRequest configured for deadline rescheduling.
     * @throws DaveCommandException If the date-time string is missing or invalid.
     */
    private static SnoozeRequest parseTargetSnooze(int index, String spec) {
        String dateString;
        if (spec.startsWith(SNOOZE_TO_PREFIX)) {
            dateString = spec.substring(SNOOZE_TO_PREFIX.length()).trim();
        } else {
            dateString = spec.substring(SNOOZE_BY_PREFIX.length()).trim();
        }

        if (dateString.isEmpty()) {
            throw new DaveCommandException("Pardon, Sire! A target date-time must follow /to.");
        }

        ParsedDateTime targetParsed = DateTimeParser.parse(dateString);
        return new SnoozeRequest(index, targetParsed);
    }

    /**
     * Parses a relative duration from a snooze specification.
     *
     * @param index 0-based task index.
     * @param spec Specification string containing amount and time unit.
     * @return SnoozeRequest configured for relative duration adjustment.
     * @throws DaveCommandException If the duration is negative, non-numeric, or has an invalid unit.
     */
    private static SnoozeRequest parseRelativeSnooze(int index, String spec) {
        String[] durationParts = spec.split("\\s+", 2);
        if (durationParts.length < 2) {
            throw new DaveCommandException("Pardon, Sire! Specify a valid duration (e.g., '3 days') "
                    + "or target date-time (e.g., '/to 2026-10-25').");
        }

        try {
            long amount = Long.parseLong(durationParts[0]);
            if (amount <= 0) {
                throw new DaveCommandException("Pardon, Sire! Snooze duration amount must be a positive number.");
            }
            ChronoUnit unit = parseChronoUnit(durationParts[1]);
            return new SnoozeRequest(index, amount, unit);
        } catch (NumberFormatException e) {
            throw new DaveCommandException("Pardon, Sire! Specify a valid duration (e.g., '3 days') "
                    + "or target date-time (e.g., '/to 2026-10-25').");
        }
    }

    /**
     * Parses a text string into a supported ChronoUnit.
     *
     * @param unitText Text representing a time unit.
     * @return Corresponding ChronoUnit.
     * @throws DaveCommandException If the unit is not recognized.
     */
    private static ChronoUnit parseChronoUnit(String unitText) {
        String normalized = unitText.trim().toLowerCase();
        switch (normalized) {
            case "day":
                // Fallthrough
            case "days":
                // Fallthrough
            case "d":
                return ChronoUnit.DAYS;
            case "hour":
                // Fallthrough
            case "hours":
                // Fallthrough
            case "h":
                // Fallthrough
            case "hr":
                // Fallthrough
            case "hrs":
                return ChronoUnit.HOURS;
            case "week":
                // Fallthrough
            case "weeks":
                // Fallthrough
            case "w":
                return ChronoUnit.WEEKS;
            case "minute":
                // Fallthrough
            case "minutes":
                // Fallthrough
            case "m":
                // Fallthrough
            case "min":
                // Fallthrough
            case "mins":
                return ChronoUnit.MINUTES;
            default:
                throw new DaveCommandException("Pardon, Sire! Unrecognized time unit: " + unitText
                        + ". Use days, hours, weeks, or minutes.");
        }
    }

    /**
     * Counts occurrences of a regex pattern within a string.
     *
     * @param input String to search within.
     * @param pattern Compiled regex pattern.
     * @return Count of matches found.
     */
    private static int countMatches(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Validates that the input string does not contain reserved storage characters.
     *
     * @param text Text to validate.
     * @param fieldName Descriptive field name for the error message.
     * @throws DaveCommandException If the reserved character is found.
     */
    private static void validateNoReservedCharacters(String text, String fieldName) {
        if (text.contains(RESERVED_STORAGE_CHAR)) {
            throw new DaveCommandException(String.format(
                    "Pardon, Sire! %s cannot contain the '%s' character as it is reserved for storage.",
                    fieldName, RESERVED_STORAGE_CHAR));
        }
    }
}
