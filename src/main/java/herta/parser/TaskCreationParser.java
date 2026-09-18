package herta.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import herta.exception.HertaException;
import herta.task.Deadline;
import herta.task.Event;
import herta.task.TaskDescriptionValidator;
import herta.task.Todo;

/**
 * Parses commands that create todo, deadline, and event tasks.
 */
final class TaskCreationParser {
    private static final String DEADLINE_MARKER = "/by";
    private static final String DEADLINE_USAGE = "deadline <description> /by <date/time>";
    private static final String TODO_USAGE = "todo <description>";
    private static final String TODO_DESCRIPTION_ERROR = "You forgot the todo description. "
            + "Try: " + TODO_USAGE + ".";
    private static final String DEADLINE_FORMAT_ERROR = "That deadline format won't work. Use: "
            + DEADLINE_USAGE + ".";
    private static final String DEADLINE_DESCRIPTION_ERROR =
            "Tell me what the deadline is for. Try: %s.";
    private static final String DEADLINE_TIME_ERROR = "A deadline needs a time. Use: %s.";
    private static final String DEADLINE_DATE_ERROR =
            "I can't schedule that value. Use a valid date/time, such as 2019-10-15 1800.";
    private static final String EVENT_FROM_MARKER = "/from";
    private static final String EVENT_TO_MARKER = "/to";
    private static final String EVENT_FROM_USAGE = EVENT_FROM_MARKER + " <start>";
    private static final String EVENT_TO_USAGE = EVENT_TO_MARKER + " <end>";
    private static final String EVENT_USAGE = "event <description> /from <start> /to <end>";
    private static final String EVENT_FORMAT_ERROR = "That event format won't work. Try: "
            + EVENT_USAGE + ".";
    private static final String EVENT_FROM_MARKER_ERROR = "An event needs a start marker: %s.";
    private static final String EVENT_TO_MARKER_ERROR = "An event needs an end marker: %s.";
    private static final String EVENT_DESCRIPTION_ERROR =
            "Tell me what the event is. Try: %s.";
    private static final String MISSING_EVENT_START_ERROR =
            "An event cannot start from nowhere. Add: %s.";
    private static final String MISSING_EVENT_END_ERROR = "An event cannot end nowhere. Add: %s.";
    private static final String EVENT_DATE_ERROR = "Those dates won't do. Use valid dates, "
            + "such as 2019-10-15 or 2/12/2019 1800.";
    private static final String EVENT_RANGE_ERROR =
            "Time moves forward. Make the event end after it starts.";
    private static final String LONG_DESCRIPTION_ERROR = "Even a task description has limits. "
            + "Keep it under %d characters.";
    private static final String INVALID_DESCRIPTION_ERROR = "Keep the description on one line and "
            + "leave out the storage delimiter and control characters.";
    private static final String TASK_DESCRIPTION_ERROR_PREFIX = "Task descriptions";
    private static final String DUPLICATE_PARAMETER_ERROR = "One %s is enough. Use: %s.";

    /** Holds the validated fields extracted from an event command. */
    private record EventParts(String description, String fromInput, String toInput) {
    }

    /**
     * Parses a todo command into a todo task.
     *
     * @param input the complete todo command.
     * @return the parsed todo task.
     * @throws HertaException if the todo description is empty.
     */
    Todo parseTodo(String input) throws HertaException {
        String description = CommandType.TODO.extractArguments(input);
        if (description.isEmpty()) {
            throw new HertaException(TODO_DESCRIPTION_ERROR);
        }
        return createTodo(description);
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param input the complete deadline command.
     * @return the parsed deadline task.
     * @throws HertaException if the command format or date/time is invalid.
     */
    Deadline parseDeadline(String input) throws HertaException {
        String deadlineArguments = CommandType.DEADLINE.extractArguments(input);
        int markerCount = CommandTokenizer.countMarker(deadlineArguments, DEADLINE_MARKER);
        if (markerCount == 0) {
            throw new HertaException(DEADLINE_FORMAT_ERROR);
        }
        if (markerCount > 1) {
            throw createDuplicateParameterError(DEADLINE_MARKER);
        }

        int markerIndex = CommandTokenizer.findMarker(deadlineArguments, DEADLINE_MARKER);
        String description = deadlineArguments.substring(0, markerIndex).trim();
        String byInput = deadlineArguments.substring(markerIndex + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new HertaException(DEADLINE_DESCRIPTION_ERROR.formatted(DEADLINE_USAGE));
        }
        if (byInput.isEmpty()) {
            throw new HertaException(DEADLINE_TIME_ERROR.formatted(DEADLINE_USAGE));
        }
        return createDeadline(description, parseUserDateTime(byInput, DEADLINE_DATE_ERROR));
    }

    /**
     * Parses an event command into an event task.
     *
     * @param input the complete event command.
     * @return the parsed event task.
     * @throws HertaException if the command format, date/time, or event range is invalid.
     */
    Event parseEvent(String input) throws HertaException {
        EventParts eventParts = parseEventParts(input);
        LocalDateTime from = parseUserDateTime(eventParts.fromInput(), EVENT_DATE_ERROR);
        LocalDateTime to = parseUserDateTime(eventParts.toInput(), EVENT_DATE_ERROR);
        return createEvent(eventParts.description(), from, to);
    }

    /**
     * Extracts and validates the description and date/time inputs from an event command.
     *
     * @param input the complete event command.
     * @return the validated event fields.
     * @throws HertaException if the command format or any field is invalid.
     */
    private EventParts parseEventParts(String input) throws HertaException {
        String eventArguments = CommandType.EVENT.extractArguments(input);
        validateEventMarkers(eventArguments);
        return extractEventParts(eventArguments);
    }

    /** Validates event marker presence and uniqueness before extracting any fields. */
    private void validateEventMarkers(String eventArguments) throws HertaException {
        int fromCount = CommandTokenizer.countMarker(eventArguments, EVENT_FROM_MARKER);
        int toCount = CommandTokenizer.countMarker(eventArguments, EVENT_TO_MARKER);
        if (fromCount > 1) {
            throw createDuplicateParameterError(EVENT_FROM_MARKER);
        }
        if (toCount > 1) {
            throw createDuplicateParameterError(EVENT_TO_MARKER);
        }
        if (fromCount == 0) {
            throw new HertaException(EVENT_FROM_MARKER_ERROR.formatted(EVENT_FROM_USAGE));
        }
        if (toCount == 0) {
            throw new HertaException(EVENT_TO_MARKER_ERROR.formatted(EVENT_TO_USAGE));
        }
    }

    /** Extracts the event description and both date/time values from validated arguments. */
    private EventParts extractEventParts(String eventArguments) throws HertaException {
        int fromIndex = CommandTokenizer.findMarker(eventArguments, EVENT_FROM_MARKER);
        String description = eventArguments.substring(0, fromIndex).trim();
        String remainder = eventArguments.substring(fromIndex + EVENT_FROM_MARKER.length());
        int toIndex = CommandTokenizer.findMarker(remainder, EVENT_TO_MARKER);
        if (toIndex < 0) {
            throw new HertaException(EVENT_FORMAT_ERROR);
        }

        String fromInput = remainder.substring(0, toIndex).trim();
        String toInput = remainder.substring(toIndex + EVENT_TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new HertaException(EVENT_DESCRIPTION_ERROR.formatted(EVENT_USAGE));
        }
        if (fromInput.isEmpty()) {
            throw new HertaException(MISSING_EVENT_START_ERROR.formatted(EVENT_FROM_USAGE));
        }
        if (toInput.isEmpty()) {
            throw new HertaException(MISSING_EVENT_END_ERROR.formatted(EVENT_TO_USAGE));
        }
        return new EventParts(description, fromInput, toInput);
    }

    /**
     * Creates an event and translates invalid time ranges into a user-facing error.
     *
     * @param description the event description.
     * @param from the event start.
     * @param to the event end.
     * @return the event with the supplied details.
     * @throws HertaException if the end does not occur after the start.
     */
    private Event createEvent(String description, LocalDateTime from, LocalDateTime to)
            throws HertaException {
        if (!from.isBefore(to)) {
            throw new HertaException(EVENT_RANGE_ERROR);
        }
        try {
            return new Event(description, from, to);
        } catch (IllegalArgumentException e) {
            throw getDescriptionValidationError(description, e);
        }
    }

    /** Creates a todo and exposes domain validation as command usage guidance. */
    private Todo createTodo(String description) throws HertaException {
        try {
            return new Todo(description);
        } catch (IllegalArgumentException e) {
            throw getDescriptionValidationError(description, e);
        }
    }

    /** Creates a deadline and exposes domain validation as command usage guidance. */
    private Deadline createDeadline(String description, LocalDateTime by)
            throws HertaException {
        try {
            return new Deadline(description, by);
        } catch (IllegalArgumentException e) {
            throw getDescriptionValidationError(description, e);
        }
    }

    /** Converts task-description domain failures into command-entry guidance. */
    private HertaException getDescriptionValidationError(String description,
                                                          IllegalArgumentException exception) {
        if (description.length() > TaskDescriptionValidator.MAX_DESCRIPTION_LENGTH) {
            return new HertaException(LONG_DESCRIPTION_ERROR.formatted(
                    TaskDescriptionValidator.MAX_DESCRIPTION_LENGTH));
        }
        if (exception.getMessage() != null
                && exception.getMessage().startsWith(TASK_DESCRIPTION_ERROR_PREFIX)) {
            return new HertaException(INVALID_DESCRIPTION_ERROR);
        }
        return new HertaException(exception.getMessage());
    }

    /**
     * Parses a user-provided date/time and converts parsing failures into an explanation.
     *
     * @param input the date/time text.
     * @param errorMessage the explanation to use when parsing fails.
     * @return the parsed date/time.
     * @throws HertaException if the date/time is invalid.
     */
    private LocalDateTime parseUserDateTime(String input, String errorMessage)
            throws HertaException {
        try {
            return DateTimeParser.parseUserDateTime(input);
        } catch (DateTimeParseException e) {
            throw new HertaException(errorMessage);
        }
    }

    /** Returns the stable message used for repeated command parameters. */
    private HertaException createDuplicateParameterError(String marker) {
        String usage = marker.equals(DEADLINE_MARKER) ? DEADLINE_USAGE : EVENT_USAGE;
        return new HertaException(DUPLICATE_PARAMETER_ERROR.formatted(marker, usage));
    }
}
