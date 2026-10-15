package dingleberry.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dingleberry.exception.DingleberryException;
import dingleberry.model.Deadlines;
import dingleberry.model.Events;
import dingleberry.model.Todo;

/**
 * Validates task arguments and constructs todo, deadline, and event tasks.
 */
final class TaskParser {
    /** Recognizes the existing task parameter syntax, ignoring case. */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(
            "(?i)/(by|from|to)(?=\\s|$)");
    /** Defines the number of date parameters accepted by a deadline. */
    private static final int DEADLINE_PARAMETER_COUNT = 1;
    /** Defines the number of time parameters required by an event. */
    private static final int EVENT_PARAMETER_COUNT = 2;
    /** Explains the required deadline syntax. */
    private static final String DEADLINE_USAGE_MESSAGE =
            "A deadline needs a description and '/by <date>' in the"
                    + " format yyyy-MM-dd HHmm, e.g. 2019-12-02 1800,"
                    + " or a weekday such as next Tue.";
    /** Explains the required event syntax. */
    private static final String EVENT_USAGE_MESSAGE =
            "An event needs a description, '/from <time>',"
                    + " and '/to <time>' in the format"
                    + " yyyy-MM-dd HHmm, e.g. 2019-12-02 1800,"
                    + " or a weekday such as next Tue.";
    /** Explains invalid or repeated event parameter markers. */
    private static final String EVENT_ORDER_MESSAGE =
            "An event needs '/from <time>' before '/to <time>'.";

    private TaskParser() {
    }

    /**
     * Constructs a todo from its description.
     *
     * @param argument the text following the command word.
     * @return the parsed todo.
     * @throws DingleberryException if the description is blank.
     */
    static Todo parseTodo(final String argument) throws DingleberryException {
        return new Todo(requireValue(argument, "todo"));
    }

    /**
     * Constructs a deadline after validating its description and by marker.
     *
     * @param argument the text following the command word.
     * @return the parsed deadline.
     * @throws DingleberryException if a marker, field, or date is invalid.
     */
    static Deadlines parseDeadline(final String argument)
            throws DingleberryException {
        List<MatchResult> parameters = findParameters(argument);
        if (parameters.size() != DEADLINE_PARAMETER_COUNT
                || !hasMarker(parameters.getFirst(), "by")) {
            throw new DingleberryException(DEADLINE_USAGE_MESSAGE);
        }

        MatchResult by = parameters.getFirst();
        String description = requireValue(
                argument.substring(0, by.start()), "deadline");
        String dueDateText = requireValue(
                argument.substring(by.end()), "deadline");
        LocalDateTime dueDate = DateTimeParser.parse(
                dueDateText, LocalDate.now(), null);
        return new Deadlines(description, dueDate);
    }

    /**
     * Constructs an event after validating its fields and chronological order.
     *
     * @param argument the text following the command word.
     * @return the parsed event.
     * @throws DingleberryException if fields or the time range are invalid.
     */
    static Events parseEvent(final String argument)
            throws DingleberryException {
        List<MatchResult> parameters = findParameters(argument);
        requireEventParameters(parameters);
        MatchResult from = parameters.getFirst();
        MatchResult to = parameters.getLast();

        String description = requireValue(
                argument.substring(0, from.start()), "event");
        String fromText = requireValue(
                argument.substring(from.end(), to.start()), "event");
        String toText = requireValue(argument.substring(to.end()), "event");
        LocalDate today = LocalDate.now();
        LocalDateTime fromDateTime = DateTimeParser.parse(
                fromText, today, null);
        LocalDateTime toDateTime = DateTimeParser.parse(
                toText, today, fromDateTime.toLocalDate());
        if (!toDateTime.isAfter(fromDateTime)) {
            throw new DingleberryException(
                    "An event's end time must be after its start time.");
        }
        return new Events(description, fromDateTime, toDateTime);
    }

    /**
     * Requires exactly one from marker followed by exactly one to marker.
     *
     * @param parameters the markers found in input order.
     * @throws DingleberryException if markers are missing or misordered.
     */
    private static void requireEventParameters(
            final List<MatchResult> parameters) throws DingleberryException {
        if (parameters.isEmpty()) {
            throw new DingleberryException(EVENT_USAGE_MESSAGE);
        }
        if (!hasMarker(parameters.getFirst(), "from")) {
            if (hasMarker(parameters.getFirst(), "to")) {
                throw new DingleberryException(EVENT_ORDER_MESSAGE);
            }
            throw new DingleberryException(EVENT_USAGE_MESSAGE);
        }
        if (parameters.size() < EVENT_PARAMETER_COUNT) {
            throw new DingleberryException(EVENT_USAGE_MESSAGE);
        }
        if (parameters.size() != EVENT_PARAMETER_COUNT
                || !hasMarker(parameters.getLast(), "to")) {
            throw new DingleberryException(EVENT_ORDER_MESSAGE);
        }
    }

    /**
     * Collects stable marker positions for independent parameter validation.
     *
     * @param argument the task arguments to inspect.
     * @return the parameter matches in input order.
     */
    private static List<MatchResult> findParameters(final String argument) {
        List<MatchResult> parameters = new ArrayList<>();
        Matcher matcher = PARAMETER_PATTERN.matcher(argument);
        while (matcher.find()) {
            parameters.add(matcher.toMatchResult());
        }
        return parameters;
    }

    /**
     * Returns whether a parameter uses the expected marker name.
     *
     * @param parameter the captured marker.
     * @param marker the expected marker name without a slash.
     * @return true when the names match, ignoring case.
     */
    private static boolean hasMarker(final MatchResult parameter,
            final String marker) {
        return marker.equalsIgnoreCase(parameter.group(1));
    }

    /**
     * Requires a non-blank description or date field.
     *
     * @param value the field to validate.
     * @param command the command name used in the error message.
     * @return the trimmed field value.
     * @throws DingleberryException if the field is blank.
     */
    private static String requireValue(final String value, final String command)
            throws DingleberryException {
        String trimmedValue = value.trim();
        if (trimmedValue.isBlank()) {
            throw new DingleberryException(
                    "The " + command + " needs a non-empty description"
                            + " and details.");
        }
        return trimmedValue;
    }
}
