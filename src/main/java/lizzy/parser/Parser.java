package lizzy.parser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import lizzy.command.Command;
import lizzy.command.DeadlineCommand;
import lizzy.command.DeleteCommand;
import lizzy.command.EventCommand;
import lizzy.command.ExitCommand;
import lizzy.command.FindCommand;
import lizzy.command.HelpCommand;
import lizzy.command.ListCommand;
import lizzy.command.MarkCommand;
import lizzy.command.TodoCommand;
import lizzy.command.UnmarkCommand;
import lizzy.command.ViewScheduleCommand;
import lizzy.command.WithinPeriodCommand;
import lizzy.exception.LizzyException;
import lizzy.task.Deadline;
import lizzy.task.Event;
import lizzy.task.Todo;
import lizzy.task.WithinPeriodTask;

/**
 * Parses Lizzy's command language and validates its command-specific arguments.
 */
public class Parser {
    private static final String EMPTY_INPUT_ERROR =
            "Silence may be elegant, but it gives me very little to work with.\n"
                    + "A little direction will do: todo <description>, list, or another command.";
    private static final String INVALID_TODO_ERROR = "A task with nothing to do is hardly a task at all.\n"
            + "Give it some substance: todo <description>.";
    private static final String MISSING_DEADLINE_DESCRIPTION_ERROR =
            "A deadline with nothing to accomplish would be all suspense and no substance.\n"
                    + "Give it a duty: deadline <description> /by <yyyy-MM-dd>.";
    private static final String INVALID_DEADLINE_SCHEDULE_ERROR =
            "A deadline is easier to keep when its due date is stated clearly.\n"
                    + "Write it as: deadline <description> /by <yyyy-MM-dd>.";
    private static final String MISSING_EVENT_DESCRIPTION_ERROR =
            "An engagement without a description leaves rather too much to guess.\n"
                    + "Tell me what it is: event <description> /on <yyyy-MM-dd>.";
    private static final String INVALID_EVENT_SCHEDULE_ERROR =
            "An engagement may be mysterious, but not about when.\n"
                    + "Use /on <yyyy-MM-dd>, or write the full range with both /from and /to.";
    private static final String INVALID_WITHIN_ERROR =
            "An interval, inconveniently, requires both a beginning and an end.\n"
                    + "Give it proper bounds: within <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>.";
    private static final String INVALID_DATE_FORMAT_ERROR =
            "That date format is a little too mysterious for me to parse.\n"
                    + "Please use yyyy-MM-dd—for example, 2026-09-10.";
    private static final String NONEXISTENT_DATE_ERROR =
            "That date is admirably imaginative, but the calendar refuses to acknowledge it.\n"
                    + "Choose a date that actually exists.";
    private static final String INVALID_TIME_FORMAT_ERROR =
            "That time format is a little too mysterious for me to parse.\n"
                    + "Please use HH:mm—for example, 14:30.";
    private static final String NONEXISTENT_TIME_ERROR =
            "That time asks rather more of the clock than it can provide.\n"
                    + "Choose a time from 00:00 to 23:59.";

    /**
     * Prevents instantiation because parsing is stateless.
     */
    private Parser() {
    }

    /**
     * Separates a supported command word from its argument.
     *
     * @param fullCommand the complete line entered by the user.
     * @return the command ready to execute.
     * @throws LizzyException if the line is empty or the command word is unknown.
     */
    public static Command parse(String fullCommand) throws LizzyException {
        if (fullCommand == null || fullCommand.isBlank()) {
            throw new LizzyException(EMPTY_INPUT_ERROR);
        }

        String[] commandParts = fullCommand.strip().split("\\s+", 2);
        String action = commandParts[0];
        String argument = commandParts.length == 2 ? commandParts[1].strip() : "";
        return switch (action) {
            case "list" -> {
                requireNoArgument(argument, "A list requires no further instruction.", "list");
                yield new ListCommand();
            }
            case "help" -> {
                requireNoArgument(argument, "A guide needs no embellishment.", "help");
                yield new HelpCommand();
            }
            case "on" -> new ViewScheduleCommand(parseOnDate(argument));
            case "todo" -> new TodoCommand(parseTodo(argument));
            case "deadline" -> new DeadlineCommand(parseDeadline(argument));
            case "event" -> new EventCommand(parseEvent(argument));
            case "within" -> new WithinPeriodCommand(parseWithinPeriod(argument));
            case "find" -> new FindCommand(parseFindKeyword(argument));
            case "mark" -> new MarkCommand(parseTaskNumber(argument, "mark"));
            case "unmark" -> new UnmarkCommand(parseTaskNumber(argument, "unmark"));
            case "delete" -> new DeleteCommand(parseTaskNumber(argument, "delete"));
            case "bye" -> {
                requireNoArgument(argument, "One farewell at a time, if you please.", "bye");
                yield new ExitCommand();
            }
            default -> throw unknownCommand(action);
        };
    }

    /**
     * Parses the argument of a todo command into a task.
     *
     * @param description the todo description.
     * @return a new incomplete todo.
     * @throws LizzyException if the description is blank.
     */
    private static Todo parseTodo(String description) throws LizzyException {
        if (description.isBlank()) {
            throw new LizzyException(INVALID_TODO_ERROR);
        }
        return new Todo(description);
    }

    /**
     * Parses the argument of a deadline command into a task.
     *
     * @param argument the deadline description and /by date.
     * @return a new incomplete deadline.
     * @throws LizzyException if the argument is incomplete or its date is invalid.
     */
    private static Deadline parseDeadline(String argument) throws LizzyException {
        if (argument.isBlank() || argument.matches("/by(?:\\s+.*)?")) {
            throw new LizzyException(MISSING_DEADLINE_DESCRIPTION_ERROR);
        }
        if (!containsMarker(argument, "/by")) {
            throw new LizzyException(INVALID_DEADLINE_SCHEDULE_ERROR);
        }
        String[] deadlineParts = splitExactly(argument, "\\s+/by(?:\\s+|$)",
                INVALID_DEADLINE_SCHEDULE_ERROR);
        if (deadlineParts[0].isBlank() || deadlineParts[1].isBlank()
                || containsScheduleMarker(deadlineParts[1])) {
            throw new LizzyException(INVALID_DEADLINE_SCHEDULE_ERROR);
        }
        return new Deadline(deadlineParts[0].strip(), parseDate(deadlineParts[1]));
    }

    /**
     * Parses the argument of an event command into a task.
     *
     * @param argument the event description and /from and /to dates.
     * @return a new incomplete event.
     * @throws LizzyException if the argument is incomplete, invalid, or ends before it begins.
     */
    private static Event parseEvent(String argument) throws LizzyException {
        if (argument.isBlank() || startsWithScheduleMarker(argument)) {
            throw new LizzyException(MISSING_EVENT_DESCRIPTION_ERROR);
        }

        boolean hasDateMarker = containsMarker(argument, "/on");
        boolean hasStartMarker = containsMarker(argument, "/from");
        boolean hasEndMarker = containsMarker(argument, "/to");
        if (!hasDateMarker && !hasStartMarker && !hasEndMarker) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }
        if (hasDateMarker) {
            return parseSingleDayEvent(argument);
        }
        if (!hasStartMarker || !hasEndMarker) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }

        String[] eventParts = splitExactly(argument, "\\s+/from(?:\\s+|$)",
                INVALID_EVENT_SCHEDULE_ERROR);
        String[] timeParts = splitExactly(eventParts[1], "\\s+/to(?:\\s+|$)",
                INVALID_EVENT_SCHEDULE_ERROR);
        if (eventParts[0].isBlank() || timeParts[0].isBlank() || timeParts[1].isBlank()) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }
        if (containsScheduleMarker(timeParts[0]) || containsScheduleMarker(timeParts[1])) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }
        LocalDate eventStartDate = parseDate(timeParts[0]);
        LocalDate eventEndDate = parseDate(timeParts[1]);
        if (eventEndDate.isBefore(eventStartDate)) {
            throw new LizzyException("An event cannot end before it begins.\n"
                    + "Let time keep its proper order: choose an end date after the start date.");
        }
        return new Event(eventParts[0].strip(), eventStartDate, eventEndDate);
    }

    /**
     * Parses an all-day or timed event that occurs on one date.
     *
     * @param argument the event description, date, and optional time range
     * @return a new incomplete single-day event
     * @throws LizzyException if the date or optional time range is invalid
     */
    private static Event parseSingleDayEvent(String argument) throws LizzyException {
        String[] eventParts = splitExactly(argument, "\\s+/on(?:\\s+|$)",
                INVALID_EVENT_SCHEDULE_ERROR);
        if (eventParts[0].isBlank()) {
            throw new LizzyException(MISSING_EVENT_DESCRIPTION_ERROR);
        }
        if (eventParts[1].isBlank() || containsScheduleMarker(eventParts[0])) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }

        String schedule = eventParts[1];
        boolean hasStartMarker = containsMarker(schedule, "/from");
        boolean hasEndMarker = containsMarker(schedule, "/to");
        if (!hasStartMarker && !hasEndMarker) {
            if (hasUnmarkedTimeDetail(schedule)) {
                throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
            }
            LocalDate eventDate = parseDate(schedule);
            return new Event(eventParts[0].strip(), eventDate, eventDate);
        }
        if (!hasStartMarker || !hasEndMarker) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }

        String[] dateAndTimes = splitExactly(schedule, "\\s+/from(?:\\s+|$)",
                INVALID_EVENT_SCHEDULE_ERROR);
        String[] times = splitExactly(dateAndTimes[1], "\\s+/to(?:\\s+|$)",
                INVALID_EVENT_SCHEDULE_ERROR);
        if (dateAndTimes[0].isBlank() || times[0].isBlank() || times[1].isBlank()) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }
        if (containsScheduleMarker(dateAndTimes[0]) || containsScheduleMarker(times[0])
                || containsScheduleMarker(times[1])) {
            throw new LizzyException(INVALID_EVENT_SCHEDULE_ERROR);
        }
        LocalDate eventDate = parseDate(dateAndTimes[0]);
        LocalTime startTime = parseTime(times[0]);
        LocalTime endTime = parseTime(times[1]);
        if (endTime.equals(startTime)) {
            throw new LizzyException("An event that begins and ends at the same moment scarcely has time to occur.\n"
                    + "Choose an end time later than the start time.");
        }
        if (endTime.isBefore(startTime)) {
            throw new LizzyException("That event ends before it begins—a trick even time will not oblige.\n"
                    + "Choose an end time later than the start time.");
        }
        return new Event(eventParts[0].strip(), eventDate, startTime, endTime);
    }

    /**
     * Parses the argument of a within command into a task with an inclusive completion period.
     *
     * @param argument the task description and /from and /to dates
     * @return a new incomplete period task
     * @throws LizzyException if the argument is incomplete, invalid, or ends before it begins
     */
    private static WithinPeriodTask parseWithinPeriod(String argument) throws LizzyException {
        String[] periodParts = splitExactly(argument, "\\s+/from\\s+", INVALID_WITHIN_ERROR);
        String[] dateParts = splitExactly(periodParts[1], "\\s+/to\\s+", INVALID_WITHIN_ERROR);
        if (periodParts[0].isBlank() || dateParts[0].isBlank() || dateParts[1].isBlank()) {
            throw new LizzyException(INVALID_WITHIN_ERROR);
        }
        LocalDate periodStartDate = parseDate(dateParts[0]);
        LocalDate periodEndDate = parseDate(dateParts[1]);
        if (periodEndDate.isBefore(periodStartDate)) {
            throw new LizzyException("A completion period cannot end before it begins.\n"
                    + "Keep the interval sensible: choose an end date on or after the start date.");
        }
        return new WithinPeriodTask(periodParts[0].strip(), periodStartDate, periodEndDate);
    }

    /**
     * Validates the keyword used to search task descriptions.
     *
     * @param argument the user-supplied keyword.
     * @return the trimmed keyword.
     * @throws LizzyException if no keyword was supplied.
     */
    private static String parseFindKeyword(String argument) throws LizzyException {
        if (argument.isBlank()) {
            throw new LizzyException("A keyword is needed to find a task.\n"
                    + "Give me something to seek: find <keyword>.");
        }
        return argument;
    }

    /**
     * Parses the date used by the on command.
     *
     * @param argument the supplied date text.
     * @return the requested date.
     * @throws LizzyException if the date is missing or malformed.
     */
    private static LocalDate parseOnDate(String argument) throws LizzyException {
        if (argument.isBlank()) {
            throw new LizzyException("A date is needed to consult the schedule.\n"
                    + "Name the day like this: on <yyyy-MM-dd>.");
        }
        return parseDate(argument);
    }

    /**
     * Parses a one-based task number for a command.
     *
     * @param argument the supplied task number.
     * @param command the command requesting the number.
     * @return the parsed task number.
     * @throws LizzyException if the number is missing or malformed.
     */
    private static int parseTaskNumber(String argument, String command) throws LizzyException {
        if (!argument.matches("[1-9]\\d*")) {
            throw invalidTaskNumber(command);
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw invalidTaskNumber(command);
        }
    }

    /**
     * Ensures a command that takes no argument has not received one.
     *
     * @param argument the supplied argument.
     * @param firstSentence the first line of the validation error.
     * @param command the command's name.
     * @throws LizzyException if an argument was supplied.
     */
    private static void requireNoArgument(String argument, String firstSentence, String command)
            throws LizzyException {
        if (!argument.isBlank()) {
            throw new LizzyException(firstSentence + "\nA simple \"" + command + "\" will do.");
        }
    }

    /**
     * Parses one strictly formatted ISO date for a deadline, event, or schedule search.
     *
     * @param dateText the user-supplied ISO date text.
     * @return the parsed calendar date.
     * @throws LizzyException if the text is not a valid ISO date.
     */
    private static LocalDate parseDate(String dateText) throws LizzyException {
        String normalizedDate = dateText.strip();
        if (!normalizedDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new LizzyException(INVALID_DATE_FORMAT_ERROR);
        }
        try {
            return LocalDate.parse(normalizedDate);
        } catch (DateTimeParseException exception) {
            throw new LizzyException(NONEXISTENT_DATE_ERROR);
        }
    }

    /**
     * Parses one 24-hour time.
     *
     * @param timeText the user-supplied time text
     * @return the parsed time
     * @throws LizzyException if the text is not a valid time in {@code HH:mm} format
     */
    private static LocalTime parseTime(String timeText) throws LizzyException {
        String normalizedTime = timeText.strip();
        if (!normalizedTime.matches("\\d{2}:\\d{2}")) {
            throw new LizzyException(INVALID_TIME_FORMAT_ERROR);
        }
        try {
            return LocalTime.parse(normalizedTime);
        } catch (DateTimeParseException exception) {
            throw new LizzyException(NONEXISTENT_TIME_ERROR);
        }
    }

    /**
     * Splits an argument around one required separator and rejects missing or repeated separators.
     *
     * @param argument the complete command argument.
     * @param separatorPattern the regular expression for the required separator.
     * @param errorMessage the user-facing message for invalid input.
     * @return the two argument parts on either side of the separator.
     * @throws LizzyException if the separator does not produce exactly two parts.
     */
    private static String[] splitExactly(String argument, String separatorPattern, String errorMessage)
            throws LizzyException {
        String[] parts = argument.split(separatorPattern, -1);
        if (parts.length != 2) {
            throw new LizzyException(errorMessage);
        }
        return parts;
    }

    /**
     * Returns whether an argument contains a complete command marker token.
     *
     * @param argument the command argument to inspect
     * @param marker the marker token, including its leading slash
     * @return {@code true} if the marker occurs as a separate token
     */
    private static boolean containsMarker(String argument, String marker) {
        return argument.matches("(?s).*(^|\\s)" + marker + "($|\\s).*");
    }

    /**
     * Returns whether an event argument begins with a scheduling marker instead of a description.
     *
     * @param argument the event argument to inspect
     * @return {@code true} if the first token is {@code /on}, {@code /from}, or {@code /to}
     */
    private static boolean startsWithScheduleMarker(String argument) {
        return argument.matches("/(?:on|from|to)(?:\\s+.*)?");
    }

    /**
     * Returns whether text contains any event or deadline scheduling marker.
     *
     * @param text the text to inspect
     * @return {@code true} if a supported slash marker occurs as a separate token
     */
    private static boolean containsScheduleMarker(String text) {
        return containsMarker(text, "/by") || containsMarker(text, "/on")
                || containsMarker(text, "/from") || containsMarker(text, "/to");
    }

    /**
     * Detects a time-like value supplied without the required slash markers.
     *
     * @param schedule the text following an event's {@code /on} marker
     * @return {@code true} if the schedule appears to contain an unmarked time range
     */
    private static boolean hasUnmarkedTimeDetail(String schedule) {
        return schedule.matches("(?s).*\\s+(?:on|from|to)(?:\\s+.*|$)")
                || schedule.matches("(?s).*\\s+\\d{1,2}:\\d{2}(?:\\s+.*|$)");
    }

    /**
     * Creates an exception for an unrecognised command word.
     *
     * @param command the command word that was not recognised.
     * @return an exception explaining the supported commands.
     */
    private static LizzyException unknownCommand(String command) {
        return new LizzyException("I'm afraid \"" + command + "\" is quite beyond my acquaintance.\n"
                + "You may try: todo, deadline, event, within, list, find, on, mark, unmark, delete, help, or bye.");
    }

    /**
     * Creates an exception for a missing or malformed task number.
     *
     * @param command the command that requires a task number.
     * @return an exception explaining the expected task-number syntax.
     */
    private static LizzyException invalidTaskNumber(String command) {
        return new LizzyException("I'm afraid that will not quite do; I need a proper task number.\n"
                + "Be precise: " + command + " <task number>.");
    }
}
