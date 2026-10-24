package clara.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import clara.exception.ClaraException;
import clara.task.Deadline;
import clara.task.Event;
import clara.task.Priority;
import clara.task.Todo;

/**
 * Provides utility methods for validating and parsing user commands into tasks.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Checks that a command does not have any arguments.
     *
     * @param command   the command being validated
     * @param arguments the arguments provided with the command
     * @throws ClaraException if arguments are provided
     */
    public static void requireNoArguments(final String command, final String arguments)
            throws ClaraException {
        if (!arguments.isEmpty()) {
            throw new ClaraException(command + " does not accept arguments");
        }
    }

    /**
     * Parses a task index from the given command arguments.
     *
     * @param command   the command being parsed
     * @param arguments the arguments containing the task index
     * @return the parsed one-based task index
     * @throws ClaraException if the arguments do not contain a valid integer
     */
    public static int parseTaskIndex(final String command, final String arguments)
            throws ClaraException {
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException ex) {
            // NOTE: Used to be AI-assisted invalid-index input validation. See CITATIONS.md [C-001].
            throw new ClaraException("Use: " + command + " <task number>.");
        }
    }

    /**
     * Parses the search text supplied to a find command.
     *
     * @param arguments the search text
     * @return the non-blank search text
     * @throws ClaraException if no search text is supplied
     */
    public static String parseFindQuery(final String arguments) throws ClaraException {
        if (arguments.isBlank()) {
            throw new ClaraException("A find command needs search text.");
        }
        return arguments;
    }

    // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
    /**
     * Parses a priority supplied in a priority command.
     *
     * @param priorityText the priority text to parse
     * @return the matching task priority
     * @throws ClaraException if the priority is not low, medium, or high
     */
    public static Priority parsePriority(final String priorityText) throws ClaraException {
        return switch (priorityText) {
            case "low" -> Priority.LOW;
            case "medium" -> Priority.MEDIUM;
            case "high" -> Priority.HIGH;
            default -> throw new ClaraException("Use: priority <task number> <low|medium|high>.");
        };
    }

    // NOTE: AI-assisted task-command input validation. See CITATIONS.md [C-002], [C-017].
    /**
     * Parses a todo command argument into a {@link Todo} task.
     *
     * @param arguments the task description
     * @return a new todo task with the specified description
     * @throws ClaraException if the description is blank or contains a reserved character
     */
    public static Todo parseTodo(String arguments) throws ClaraException {
        if (arguments.isBlank()) {
            throw new ClaraException("A todo needs a description.");
        }
        rejectReservedCharacter(arguments);
        return new Todo(arguments);
    }

    /**
     * Parses a deadline command argument into a {@link Deadline} task.
     *
     * @param arguments the command arguments containing the task name and deadline
     * @return a new deadline task with the specified name and deadline
     * @throws ClaraException if the arguments are malformed or the deadline has an invalid format
     */
    public static Deadline parseDeadline(String arguments) throws ClaraException {
        rejectReservedCharacter(arguments);
        String[] nameAndTime = arguments.split(" /by ", 2);

        if (nameAndTime.length != 2 || nameAndTime[0].isBlank() || nameAndTime[1].isBlank()) {
            throw new ClaraException("Use: deadline <name> /by <time>.");
        }

        LocalDateTime deadline = parseDateTime(nameAndTime[1]);
        return new Deadline(nameAndTime[0], deadline);
    }

    /**
     * Parses an event command argument into an {@link Event} task.
     *
     * @param arguments the command arguments containing the event name, start time, and end time
     * @return a new event task with the specified name and start and end times
     * @throws ClaraException if the arguments are malformed or either time has an invalid format
     */
    public static Event parseEvent(String arguments) throws ClaraException {
        rejectReservedCharacter(arguments);
        String[] nameAndRest = arguments.split(" /from ", 2);
        if (nameAndRest.length != 2 || nameAndRest[0].isBlank()) {
            throw new ClaraException("Use: event <name> /from <time> /to <time>.");
        }

        String[] fromTimeAndToTime = nameAndRest[1].split(" /to ", 2);
        if (fromTimeAndToTime.length != 2
                || fromTimeAndToTime[0].isBlank()
                || fromTimeAndToTime[1].isBlank()) {
            throw new ClaraException("Use: event <name> /from <time> /to <time>.");
        }

        // NOTE: AI-assisted event-range validation. See CITATIONS.md [C-017].
        LocalDateTime fromTime = parseDateTime(fromTimeAndToTime[0]);
        LocalDateTime toTime = parseDateTime(fromTimeAndToTime[1]);
        if (!fromTime.isBefore(toTime)) {
            throw new ClaraException("An event's start time must be before its end time.");
        }
        return new Event(nameAndRest[0], fromTime, toTime);
    }

    /**
     * Rejects task details that contain the character reserved by the save-file format.
     *
     * @param taskDetails the task details to validate
     * @throws ClaraException if the task details contain the reserved character
     */
    private static void rejectReservedCharacter(String taskDetails) throws ClaraException {
        if (taskDetails.indexOf('|') != -1) {
            throw new ClaraException("The character '|' is reserved and cannot be used in task details.");
        }
    }

    /**
     * Parses a date and time supplied in Clara's command format.
     *
     * @param dateTimeText the date and time text to parse
     * @return the parsed date and time
     * @throws ClaraException if the text does not use Clara's command format
     */
    private static LocalDateTime parseDateTime(String dateTimeText) throws ClaraException {
        // NOTE: AI-assisted `LocalDateTime` pattern implementation. See CITATIONS.md [C-005].
        try {
            return LocalDateTime.parse(dateTimeText, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new ClaraException("Time format: yyyy-MM-dd HHmm (e.g. 2025-12-25 1357).");
        }
    }
}
