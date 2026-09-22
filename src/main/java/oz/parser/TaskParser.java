package oz.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oz.exception.OzException;
import oz.task.Deadline;
import oz.task.Event;
import oz.task.Task;
import oz.task.TaskDateTime;
import oz.task.ToDo;

/**
 * Parses task-creation command arguments into validated task objects.
 */
public final class TaskParser {
    /** Error shown when an unexpected parameter flag is supplied to todo. */
    private static final String TODO_UNEXPECTED_FLAG_MESSAGE =
            "The todo command does not accept parameter flags like /by, /from, or /to.";

    /** Error template shown when a required parameter flag is missing. */
    private static final String MISSING_FLAG_MESSAGE_FORMAT = "Missing required '%s' parameter. %s";

    /** Error template shown when parameters are supplied out of order. */
    private static final String MISPLACED_FLAG_MESSAGE_FORMAT =
            "The '%s' parameter must precede '%s'. %s";

    /** Error template shown when an unexpected parameter flag is supplied. */
    private static final String UNEXPECTED_FLAG_MESSAGE_FORMAT =
            "Unexpected '%s' parameter in %s command. %s";

    /** Error shown when a todo description is missing. */
    private static final String EMPTY_TODO_DESCRIPTION_MESSAGE =
            "The description of a todo cannot be empty.";

    /** Usage message for adding a deadline. */
    private static final String DEADLINE_USAGE_MESSAGE = "Use: deadline <description> /by <date>.";

    /** Error shown when a deadline description is missing. */
    private static final String EMPTY_DEADLINE_DESCRIPTION_MESSAGE =
            "The description of a deadline cannot be empty.";

    /** Error shown when a deadline date-time is missing. */
    private static final String EMPTY_DEADLINE_DATE_TIME_MESSAGE =
            "The deadline date/time (/by) cannot be empty.";

    /** Usage message for adding an event. */
    private static final String EVENT_USAGE_MESSAGE =
            "Use: event <description> /from <start> /to <end>.";

    /** Error shown when an event description is missing. */
    private static final String EMPTY_EVENT_DESCRIPTION_MESSAGE =
            "The description of an event cannot be empty.";

    /** Error shown when an event start or end is missing. */
    private static final String EMPTY_EVENT_DATE_TIME_MESSAGE =
            "The event start (/from) and end (/to) dates cannot be empty.";

    /** Pattern parsing deadline description and deadline argument. */
    private static final Pattern DEADLINE_ARGUMENTS_PATTERN = Pattern
            .compile("^(?<description>.+?)\\s+/by\\s+(?<byTime>.+)$");

    /** Pattern parsing event description, start, and end arguments. */
    private static final Pattern EVENT_ARGUMENTS_PATTERN = Pattern
            .compile("^(?<description>.+?)\\s+/from\\s+(?<fromTime>.+?)\\s+/to\\s+(?<toTime>.+)$");

    /** Prevents instantiation of this stateless parser. */
    private TaskParser() {
    }

    /**
     * Parses arguments for a todo task.
     *
     * @param arguments Todo command arguments.
     * @return Parsed todo task.
     * @throws OzException If the arguments are invalid.
     */
    public static Task parseTodo(String arguments) throws OzException {
        if (arguments.isBlank()) {
            throw new OzException(EMPTY_TODO_DESCRIPTION_MESSAGE);
        }
        CommandArgumentValidator.validateNoStorageDelimiter(arguments);
        if (CommandArgumentValidator.containsAnyFlag(
                arguments, "/by", "/from", "/to", "/on")) {
            throw new OzException(TODO_UNEXPECTED_FLAG_MESSAGE);
        }
        return new ToDo(arguments);
    }

    /**
     * Parses arguments for a deadline task.
     *
     * @param arguments Deadline command arguments.
     * @return Parsed deadline task.
     * @throws OzException If the arguments are invalid.
     */
    public static Task parseDeadline(String arguments) throws OzException {
        CommandArgumentValidator.validateNoStorageDelimiter(arguments);
        validateDeadlineFlags(arguments);

        Matcher deadlineMatcher = DEADLINE_ARGUMENTS_PATTERN.matcher(arguments);
        if (!deadlineMatcher.matches()) {
            throw new OzException(DEADLINE_USAGE_MESSAGE);
        }

        String description = deadlineMatcher.group("description").trim();
        String deadlineTimeArgument = deadlineMatcher.group("byTime").trim();
        if (description.isEmpty()) {
            throw new OzException(EMPTY_DEADLINE_DESCRIPTION_MESSAGE);
        }
        if (deadlineTimeArgument.isEmpty()) {
            throw new OzException(EMPTY_DEADLINE_DATE_TIME_MESSAGE);
        }

        TaskDateTime deadlineTime = TaskDateTime.parse(deadlineTimeArgument);
        return new Deadline(description, deadlineTime);
    }

    /**
     * Parses arguments for an event task.
     *
     * @param arguments Event command arguments.
     * @return Parsed event task.
     * @throws OzException If the arguments are invalid.
     */
    public static Task parseEvent(String arguments) throws OzException {
        CommandArgumentValidator.validateNoStorageDelimiter(arguments);
        validateEventFlags(arguments);

        Matcher eventMatcher = EVENT_ARGUMENTS_PATTERN.matcher(arguments);
        if (!eventMatcher.matches()) {
            throw new OzException(EVENT_USAGE_MESSAGE);
        }

        String description = eventMatcher.group("description").trim();
        String fromTimeArgument = eventMatcher.group("fromTime").trim();
        String toTimeArgument = eventMatcher.group("toTime").trim();
        if (description.isEmpty()) {
            throw new OzException(EMPTY_EVENT_DESCRIPTION_MESSAGE);
        }
        if (fromTimeArgument.isEmpty() || toTimeArgument.isEmpty()) {
            throw new OzException(EMPTY_EVENT_DATE_TIME_MESSAGE);
        }

        TaskDateTime fromTime = TaskDateTime.parse(fromTimeArgument);
        TaskDateTime toTime = TaskDateTime.parse(toTimeArgument);
        return new Event(description, fromTime, toTime);
    }

    /**
     * Validates flags accepted by a deadline command.
     *
     * @param arguments Deadline command arguments.
     * @throws OzException If a required flag is missing or an unsupported flag is present.
     */
    private static void validateDeadlineFlags(String arguments) throws OzException {
        CommandArgumentValidator.validateNoDuplicateFlags(arguments, "/by");
        if (!CommandArgumentValidator.containsFlag(arguments, "/by")) {
            throw new OzException(DEADLINE_USAGE_MESSAGE);
        }
        if (CommandArgumentValidator.containsFlag(arguments, "/from")) {
            throw new OzException(String.format(UNEXPECTED_FLAG_MESSAGE_FORMAT,
                    "/from", "deadline", DEADLINE_USAGE_MESSAGE));
        }
        if (CommandArgumentValidator.containsFlag(arguments, "/to")) {
            throw new OzException(String.format(UNEXPECTED_FLAG_MESSAGE_FORMAT,
                    "/to", "deadline", DEADLINE_USAGE_MESSAGE));
        }
    }

    /**
     * Validates the presence, uniqueness, and order of event flags.
     *
     * @param arguments Event command arguments.
     * @throws OzException If required flags are missing, repeated, misplaced, or unsupported.
     */
    private static void validateEventFlags(String arguments) throws OzException {
        CommandArgumentValidator.validateNoDuplicateFlags(arguments, "/from", "/to");
        boolean hasFromFlag = CommandArgumentValidator.containsFlag(arguments, "/from");
        boolean hasToFlag = CommandArgumentValidator.containsFlag(arguments, "/to");
        if (!hasFromFlag && !hasToFlag) {
            throw new OzException(EVENT_USAGE_MESSAGE);
        }
        if (!hasFromFlag) {
            throw new OzException(String.format(MISSING_FLAG_MESSAGE_FORMAT,
                    "/from", EVENT_USAGE_MESSAGE));
        }
        if (!hasToFlag) {
            throw new OzException(String.format(MISSING_FLAG_MESSAGE_FORMAT,
                    "/to", EVENT_USAGE_MESSAGE));
        }
        int fromFlagIndex = CommandArgumentValidator.findFlagIndex(arguments, "/from");
        int toFlagIndex = CommandArgumentValidator.findFlagIndex(arguments, "/to");
        if (toFlagIndex < fromFlagIndex) {
            throw new OzException(String.format(MISPLACED_FLAG_MESSAGE_FORMAT,
                    "/from", "/to", EVENT_USAGE_MESSAGE));
        }
        if (CommandArgumentValidator.containsFlag(arguments, "/by")) {
            throw new OzException(String.format(UNEXPECTED_FLAG_MESSAGE_FORMAT,
                    "/by", "event", EVENT_USAGE_MESSAGE));
        }
    }

    /**
     * Parses arguments for a weekly recurring event.
     *
     * @param arguments Recurring event command arguments.
     * @return Parsed recurring event task.
     * @throws OzException If the arguments are invalid.
     */
    public static Task parseRecurringEvent(String arguments) throws OzException {
        CommandArgumentValidator.validateNoStorageDelimiter(arguments);
        return RecurringEventParser.parse(arguments);
    }

}
