package bingus.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Todo;

/**
 * Parses and validates arguments that describe todo, deadline, and event tasks.
 */
class TaskParser {

    /** Strict format accepted for deadline and event date/time input. */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final String DEADLINE_USAGE = "Try `deadline [DESCRIPTION] /by [DATETIME]`.";
    private static final String EVENT_USAGE =
            "Try `event [DESCRIPTION] /from [FROM_DATETIME] /to [TO_DATETIME]`.";
    private static final String INVALID_DEADLINE_DATE_TIME_MESSAGE =
            "That deadline date and time are not valid.\n"
                    + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";
    private static final String INVALID_EVENT_DATE_TIME_MESSAGE =
            "That event date and time are not valid.\n"
                    + "Use yyyy-MM-dd HHmm, for example 2019-12-02 1800.";

    /**
     * Parses the arguments of a todo command.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @return validated todo task.
     * @throws BingusException if the description is missing or empty.
     */
    Todo parseTodo(String[] commandParts) throws BingusException {
        if (commandParts.length < 2 || commandParts[1].trim().isEmpty()) {
            throw new BingusException("I need a description for that task.\nTry `todo [DESCRIPTION]`.");
        }
        return new Todo(commandParts[1].trim());
    }

    /**
     * Parses the arguments of a deadline command.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @return validated deadline task.
     * @throws BingusException if the command format or date/time is invalid.
     */
    Deadline parseDeadline(String[] commandParts) throws BingusException {
        if (commandParts.length < 2) {
            throw new BingusException("I need a description and date for that deadline.\n" + DEADLINE_USAGE);
        }

        if (countOccurrences(commandParts[1], "/by") > 1) {
            throw new BingusException("I can only use one `/by` date for a deadline.\n" + DEADLINE_USAGE);
        }

        String[] deadlineParts = commandParts[1].split("/by");
        if (deadlineParts.length != 2) {
            throw new BingusException("I could not understand that deadline.\n" + DEADLINE_USAGE);
        }

        String description = deadlineParts[0].trim();
        String deadlineDateTimeText = deadlineParts[1].trim();
        requireNonEmpty(description, "I need a description for that deadline.\n" + DEADLINE_USAGE);
        requireNonEmpty(deadlineDateTimeText, "I need a date and time for that deadline.\n" + DEADLINE_USAGE);

        LocalDateTime deadlineDateTime = parseDeadlineDateTime(deadlineDateTimeText);
        return new Deadline(description, deadlineDateTime);
    }

    /**
     * Parses the arguments of an event command.
     *
     * @param commandParts command and arguments split into at most two parts.
     * @return validated event task.
     * @throws BingusException if the command format or date/time is invalid.
     */
    Event parseEvent(String[] commandParts) throws BingusException {
        if (commandParts.length < 2) {
            throw new BingusException("I need a description, start, and end for that event.\n" + EVENT_USAGE);
        }

        validateEventMarkers(commandParts[1]);
        String[] descriptionAndTimes = commandParts[1].split("/from");
        if (descriptionAndTimes.length != 2) {
            throw new BingusException("I could not understand that event.\n" + EVENT_USAGE);
        }

        String[] startAndEnd = descriptionAndTimes[1].split("/to");
        if (startAndEnd.length != 2) {
            throw new BingusException("I could not understand that event.\n" + EVENT_USAGE);
        }

        String description = descriptionAndTimes[0].trim();
        String startDateTimeText = startAndEnd[0].trim();
        String endDateTimeText = startAndEnd[1].trim();
        requireNonEmpty(startDateTimeText, "I need a start date and time for that event.\n" + EVENT_USAGE);
        requireNonEmpty(endDateTimeText, "I need an end date and time for that event.\n" + EVENT_USAGE);
        requireNonEmpty(description, "I need a description for that event.\n" + EVENT_USAGE);

        LocalDateTime fromDateTime = parseEventDateTime(startDateTimeText);
        LocalDateTime toDateTime = parseEventDateTime(endDateTimeText);
        if (!toDateTime.isAfter(fromDateTime)) {
            throw new BingusException("The event must end after it starts.");
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    private void validateEventMarkers(String eventArguments) throws BingusException {
        if (countOccurrences(eventArguments, "/from") > 1) {
            throw new BingusException("I can only use one `/from` date for an event.\n" + EVENT_USAGE);
        }
        if (countOccurrences(eventArguments, "/to") > 1) {
            throw new BingusException("I can only use one `/to` date for an event.\n" + EVENT_USAGE);
        }
    }

    /**
     * Parses a deadline date and time using the command input format.
     *
     * @param input date-time text to parse.
     * @return parsed deadline date and time.
     * @throws BingusException if the text is not a valid date and time.
     */
    LocalDateTime parseDeadlineDateTime(String input) throws BingusException {
        return parseDateTime(input, INVALID_DEADLINE_DATE_TIME_MESSAGE);
    }

    /**
     * Parses an event date and time using the command input format.
     *
     * @param input date-time text to parse.
     * @return parsed event date and time.
     * @throws BingusException if the text is not a valid date and time.
     */
    LocalDateTime parseEventDateTime(String input) throws BingusException {
        return parseDateTime(input, INVALID_EVENT_DATE_TIME_MESSAGE);
    }

    private LocalDateTime parseDateTime(String input, String errorMessage) throws BingusException {
        try {
            return LocalDateTime.parse(input, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new BingusException(errorMessage);
        }
    }

    private void requireNonEmpty(String value, String errorMessage) throws BingusException {
        if (value.isEmpty()) {
            throw new BingusException(errorMessage);
        }
    }

    private int countOccurrences(String text, String marker) {
        int occurrenceCount = 0;
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            occurrenceCount++;
            markerIndex = text.indexOf(marker, markerIndex + marker.length());
        }
        return occurrenceCount;
    }
}
