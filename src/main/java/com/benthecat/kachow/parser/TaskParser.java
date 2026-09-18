package com.benthecat.kachow.parser;

import static com.benthecat.kachow.parser.ParserSupport.DATE_TIME_FORMAT_GUIDANCE;
import static com.benthecat.kachow.parser.ParserSupport.USAGE_DEADLINE;
import static com.benthecat.kachow.parser.ParserSupport.USAGE_EVENT;
import static com.benthecat.kachow.parser.ParserSupport.createInvalidEventDateTimeException;
import static com.benthecat.kachow.parser.ParserSupport.findToken;
import static com.benthecat.kachow.parser.ParserSupport.validateDescription;

import java.time.format.DateTimeParseException;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.Parser.ParsedCommand;
import com.benthecat.kachow.task.Deadline;
import com.benthecat.kachow.task.Event;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.Todo;

/** Creates tasks from validated add-command arguments. */
final class TaskParser {
    /**
     * Creates the task described by an add command.
     *
     * @param parsedCommand Todo, deadline, or event command to parse.
     * @return Parsed task.
     * @throws KachowException If a required task component is missing or invalid.
     */
    public Task parseTask(ParsedCommand parsedCommand) throws KachowException {
        assert parsedCommand.command() == Command.TODO
                || parsedCommand.command() == Command.DEADLINE
                || parsedCommand.command() == Command.EVENT
                : "Only task-creation commands can be parsed as tasks";

        return switch (parsedCommand.command()) {
            case TODO -> parseTodo(parsedCommand.argument());
            case DEADLINE -> parseDeadline(parsedCommand.argument());
            case EVENT -> parseEvent(parsedCommand.argument());
            default -> throw new IllegalArgumentException(
                    "Command does not create a task: " + parsedCommand.command());
        };
    }

    /** Creates a todo after ensuring that it has a description. */
    private Todo parseTodo(String description) throws KachowException {
        if (description.isBlank()) {
            throw new KachowException("This racer needs a name. Use: todo DESCRIPTION");
        }
        return new Todo(validateDescription(description));
    }

    /** Parses a deadline in the form {@code DESCRIPTION /by DATE_OR_TIME}. */
    private Deadline parseDeadline(String argument) throws KachowException {
        int byIndex = findToken(argument, "/by", 0);
        if (argument.isEmpty() || byIndex == 0) {
            throw new KachowException(
                    "This deadline racer needs a task description. Use: " + USAGE_DEADLINE);
        }
        if (byIndex == -1) {
            throw new KachowException("That deadline is missing its /by checkpoint. Use: " + USAGE_DEADLINE);
        }
        if (findToken(argument, "/by", byIndex + 3) != -1) {
            throw new KachowException(
                    "That deadline has too many /by checkpoints. Use exactly one: " + USAGE_DEADLINE);
        }

        String description = validateDescription(argument.substring(0, byIndex).strip());
        String by = argument.substring(byIndex + 3).strip();
        if (description.isEmpty()) {
            throw new KachowException(
                    "This deadline racer needs a task description. Use: " + USAGE_DEADLINE);
        }
        if (by.isEmpty()) {
            throw new KachowException(
                    "That deadline needs a date or time after /by. Use: " + USAGE_DEADLINE);
        }

        try {
            return new Deadline(description, DateTimeParser.parse(by));
        } catch (DateTimeParseException exception) {
            throw new KachowException(
                    "That deadline date or time is invalid. " + DATE_TIME_FORMAT_GUIDANCE,
                    exception);
        }
    }

    /** Parses an event in the form {@code DESCRIPTION /from START /to END}. */
    private Event parseEvent(String argument) throws KachowException {
        EventArguments fields = parseEventArguments(argument);
        return createEvent(fields);
    }

    /** Extracts event fields after checking the order and number of route markers. */
    private EventArguments parseEventArguments(String argument) throws KachowException {
        int fromIndex = findToken(argument, "/from", 0);
        int toIndex = validateEventMarkers(argument, fromIndex);
        String description = validateDescription(argument.substring(0, fromIndex).strip());
        String from = argument.substring(fromIndex + 5, toIndex).strip();
        String to = argument.substring(toIndex + 3).strip();
        if (from.isEmpty()) {
            throw new KachowException("That event needs a start after /from. Use: " + USAGE_EVENT);
        }
        if (to.isEmpty()) {
            throw new KachowException("That event needs an end after /to. Use: " + USAGE_EVENT);
        }
        return new EventArguments(description, from, to);
    }

    /** Validates event markers and returns the finish marker's position. */
    private int validateEventMarkers(String argument, int fromIndex) throws KachowException {
        int firstToIndex = findToken(argument, "/to", 0);
        if (argument.isEmpty() || fromIndex == 0) {
            throw new KachowException("This event racer needs a description. Use: " + USAGE_EVENT);
        }
        if (fromIndex == -1) {
            throw new KachowException("That event is missing its /from starting line. Use: " + USAGE_EVENT);
        }
        if (firstToIndex != -1 && firstToIndex < fromIndex) {
            throw new KachowException("That event's /from must come before /to. Use: " + USAGE_EVENT);
        }

        int toIndex = findToken(argument, "/to", fromIndex + 5);
        if (toIndex == -1) {
            throw new KachowException("That event is missing its /to finish line. Use: " + USAGE_EVENT);
        }
        if (findToken(argument, "/from", fromIndex + 5) != -1
                || findToken(argument, "/to", toIndex + 3) != -1) {
            throw new KachowException(
                    "That event has extra route markers. Use one /from and one /to: " + USAGE_EVENT);
        }

        return toIndex;
    }

    /** Parses event dates and translates invalid ranges into user-facing guidance. */
    private Event createEvent(EventArguments fields) throws KachowException {
        DateTimeParser.ParsedDateTime parsedFrom;
        try {
            parsedFrom = DateTimeParser.parse(fields.from());
        } catch (DateTimeParseException exception) {
            throw createInvalidEventDateTimeException("start", exception);
        }

        DateTimeParser.ParsedDateTime parsedTo;
        try {
            parsedTo = DateTimeParser.parse(fields.to(), parsedFrom.date());
        } catch (DateTimeParseException exception) {
            throw createInvalidEventDateTimeException("end", exception);
        }

        try {
            return new Event(fields.description(), parsedFrom, parsedTo);
        } catch (IllegalArgumentException exception) {
            throw new KachowException(
                    "That event must end after it starts. Use a full /to date for an overnight event.",
                    exception);
        }
    }

    /** Holds the validated text fields of an event command. */
    private record EventArguments(String description, String from, String to) { }
}
