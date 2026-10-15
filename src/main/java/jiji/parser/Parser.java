package jiji.parser;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jiji.command.AddDeadlineCommand;
import jiji.command.AddEventCommand;
import jiji.command.AddTodoCommand;
import jiji.command.Command;
import jiji.command.CommandType;
import jiji.command.DeleteCommand;
import jiji.command.ExitCommand;
import jiji.command.FindCommand;
import jiji.command.HelpCommand;
import jiji.command.ListCommand;
import jiji.command.ListFilter;
import jiji.command.MarkCommand;
import jiji.command.ScheduleCommand;
import jiji.command.StatsCommand;
import jiji.command.UnmarkCommand;
import jiji.exception.JijiException;
import jiji.exception.JijiInvalidIndexException;
import jiji.exception.JijiMissingArgumentException;
import jiji.exception.JijiUnknownCommandException;

/**
 * Parses user input commands into executable {@link Command} objects.
 */
public class Parser {

    private static final String DEADLINE_BY_DELIMITER = " /by ";
    private static final String EVENT_FROM_DELIMITER = " /from ";
    private static final String EVENT_TO_DELIMITER = " /to ";

    /**
     * Prevents instantiation of utility class.
     */
    private Parser() {
    }

    /**
     * Parses the full command string entered by the user.
     *
     * @param fullCommand The raw input string.
     * @return The corresponding executable {@link Command}.
     * @throws JijiException If the input is unrecognized, malformed, or missing required parameters.
     */
    public static Command parse(String fullCommand) throws JijiException {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            throw new JijiUnknownCommandException();
        }

        assert fullCommand != null && !fullCommand.trim().isEmpty() : "fullCommand must be non-empty";
        String trimmed = fullCommand.trim();
        String[] words = trimmed.split("\\s+", 2);
        String commandWord = words[0];
        assert !commandWord.isEmpty() : "commandWord cannot be empty after trimming";
        String arguments = (words.length > 1) ? words[1].trim() : "";
        CommandType commandType = CommandType.from(commandWord);

        Command command;
        switch (commandType) {
            case BYE:
                if (!arguments.isEmpty()) {
                    throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ The 'bye' command does not take any arguments.");
                }
                command = new ExitCommand();
                break;

            case LIST:
                command = parseList(arguments);
                break;

            case MARK:
                command = new MarkCommand(parseIndex(arguments, "mark"));
                break;

            case UNMARK:
                command = new UnmarkCommand(parseIndex(arguments, "unmark"));
                break;

            case DELETE:
                command = new DeleteCommand(parseIndex(arguments, "delete"));
                break;

            case TODO:
                command = parseTodo(arguments);
                break;

            case DEADLINE:
                command = parseDeadline(arguments);
                break;

            case EVENT:
                command = parseEvent(arguments);
                break;

            case FIND:
                command = parseFind(arguments);
                break;

            case HELP:
                command = parseHelp(arguments);
                break;

            case STATS:
                command = parseStats(arguments);
                break;

            case SCHEDULE:
                command = parseSchedule(arguments);
                break;

            default:
                throw new JijiUnknownCommandException();
        }
        assert command != null : "Parsed command must not be null";
        return command;
    }

    /**
     * Parses the task index argument for mark, unmark, and delete commands.
     *
     * @param arguments The command arguments string.
     * @param commandName The name of the command ("mark", "unmark", "delete").
     * @return The 0-based task index.
     * @throws JijiException If index is missing or non-numeric.
     */
    private static int parseIndex(String arguments, String commandName) throws JijiException {
        assert commandName != null && !commandName.isEmpty() : "commandName cannot be empty";
        if (arguments.isEmpty()) {
            throw JijiInvalidIndexException.forMissingIndex(commandName);
        }
        try {
            return Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw JijiInvalidIndexException.forInvalidNumber();
        }
    }

    /**
     * Counts the occurrences of a delimiter token surrounded by whitespace or string boundaries.
     *
     * @param text The input text.
     * @param delimiter The delimiter keyword (e.g. "/by").
     * @return The number of occurrences found.
     */
    private static int countDelimiterOccurrences(String text, String delimiter) {
        assert text != null && delimiter != null : "Text and delimiter cannot be null";
        String regex = "(?i)(?:^|\\s)" + Pattern.quote(delimiter) + "(?:\\s|$)";
        Matcher matcher = Pattern.compile(regex).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Parses the todo command arguments.
     *
     * @param arguments The command arguments string.
     * @return An {@link AddTodoCommand} instance.
     * @throws JijiException If description is empty or contains reserved delimiters.
     */
    private static Command parseTodo(String arguments) throws JijiException {
        if (arguments.isEmpty()) {
            throw JijiMissingArgumentException.forEmptyTodo();
        }
        if (arguments.contains("|")) {
            throw new JijiException(
                    "OOPS! ₍^› ꘍ ‹ ^₎⟆ Task descriptions and parameters cannot contain the '|' character.");
        }
        assert !arguments.isEmpty() : "Todo description must not be empty after check";
        return new AddTodoCommand(arguments);
    }

    /**
     * Parses the deadline command arguments.
     *
     * @param arguments The command arguments string.
     * @return An {@link AddDeadlineCommand} instance.
     * @throws JijiException If description or deadline parameter is missing or invalid.
     */
    private static Command parseDeadline(String arguments) throws JijiException {
        if (arguments.contains("|")) {
            throw new JijiException(
                    "OOPS! ₍^› ꘍ ‹ ^₎⟆ Task descriptions and parameters cannot contain the '|' character.");
        }
        if (countDelimiterOccurrences(arguments, "/by") > 1) {
            throw new JijiException("OOPS! ^๑_๑^ ੭ The parameter '/by' cannot be specified multiple times.");
        }
        if (arguments.isEmpty() || !arguments.contains(DEADLINE_BY_DELIMITER)) {
            throw JijiMissingArgumentException.forMissingDeadline();
        }
        String[] parts = arguments.split(DEADLINE_BY_DELIMITER, 2);
        String description = parts[0].trim();
        String by = parts.length > 1 ? parts[1].trim() : "";
        if (description.isEmpty() || by.isEmpty()) {
            throw JijiMissingArgumentException.forMissingDeadline();
        }
        if (DateTimeUtil.isNonExistentDate(by)) {
            throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ That date does not exist on the calendar (e.g. Feb 30). "
                    + "Please provide a valid calendar date.");
        }
        assert !description.isEmpty() && !by.isEmpty() : "Deadline description and by must not be empty after check";
        return new AddDeadlineCommand(description, by);
    }

    /**
     * Parses the event command arguments.
     *
     * @param arguments The command arguments string.
     * @return An {@link AddEventCommand} instance.
     * @throws JijiException If description, /from, or /to parameter is missing or invalid.
     */
    private static Command parseEvent(String arguments) throws JijiException {
        if (arguments.contains("|")) {
            throw new JijiException(
                    "OOPS! ₍^› ꘍ ‹ ^₎⟆ Task descriptions and parameters cannot contain the '|' character.");
        }
        if (countDelimiterOccurrences(arguments, "/from") > 1) {
            throw new JijiException("OOPS! ^๑_๑^ ੭ The parameter '/from' cannot be specified multiple times.");
        }
        if (countDelimiterOccurrences(arguments, "/to") > 1) {
            throw new JijiException("OOPS! ^๑_๑^ ੭ The parameter '/to' cannot be specified multiple times.");
        }
        if (arguments.isEmpty() || !arguments.contains(EVENT_FROM_DELIMITER)
                || !arguments.contains(EVENT_TO_DELIMITER)) {
            throw JijiMissingArgumentException.forMissingEvent();
        }
        String[] parts = arguments.split(EVENT_FROM_DELIMITER, 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw JijiMissingArgumentException.forMissingEvent();
        }
        String[] timeParts = parts[1].split(EVENT_TO_DELIMITER, 2);
        String from = timeParts[0].trim();
        String to = timeParts.length > 1 ? timeParts[1].trim() : "";
        if (from.isEmpty() || to.isEmpty()) {
            throw JijiMissingArgumentException.forMissingEvent();
        }
        if (DateTimeUtil.isNonExistentDate(from) || DateTimeUtil.isNonExistentDate(to)) {
            throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ That date does not exist on the calendar (e.g. Feb 30). "
                    + "Please provide a valid calendar date.");
        }
        assert !description.isEmpty() && !from.isEmpty() && !to.isEmpty()
                : "Event fields must not be empty after check";
        return new AddEventCommand(description, from, to);
    }

    /**
     * Parses the find command arguments.
     *
     * @param arguments The command arguments string.
     * @return A {@link FindCommand} instance.
     * @throws JijiException If search keyword is empty or contains reserved delimiters.
     */
    private static Command parseFind(String arguments) throws JijiException {
        if (arguments.isEmpty()) {
            throw JijiMissingArgumentException.forEmptyFind();
        }
        if (arguments.contains("|")) {
            throw new JijiException(
                    "OOPS! ₍^› ꘍ ‹ ^₎⟆ Search keyword cannot contain the '|' character.");
        }
        assert !arguments.isEmpty() : "Find keyword must not be empty after check";
        return new FindCommand(arguments);
    }

    /**
     * Parses the help command arguments.
     *
     * @param arguments The command arguments string.
     * @return A {@link HelpCommand} instance.
     */
    private static Command parseHelp(String arguments) {
        assert arguments != null : "Arguments string cannot be null";
        return new HelpCommand(arguments);
    }

    /**
     * Parses the list command arguments for optional filtering.
     *
     * @param arguments The command arguments string.
     * @return A {@link ListCommand} with the appropriate filter.
     * @throws JijiException If an unrecognized filter argument is specified.
     */
    private static Command parseList(String arguments) throws JijiException {
        assert arguments != null : "Arguments string cannot be null";
        if (arguments.isEmpty()) {
            return new ListCommand(ListFilter.ALL);
        }
        String filterWord = arguments.toLowerCase();
        if (filterWord.equals("pending") || filterWord.equals("uncompleted") || filterWord.equals("todo")) {
            return new ListCommand(ListFilter.PENDING);
        }
        if (filterWord.equals("done") || filterWord.equals("completed")) {
            return new ListCommand(ListFilter.DONE);
        }
        throw new JijiUnknownCommandException();
    }

    /**
     * Parses the stats command arguments.
     *
     * @param arguments The command arguments string.
     * @return A {@link StatsCommand} instance.
     * @throws JijiException If extra arguments are provided.
     */
    private static Command parseStats(String arguments) throws JijiException {
        assert arguments != null : "Arguments string cannot be null";
        if (!arguments.isEmpty()) {
            throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ The 'stats' command does not take any arguments.");
        }
        return new StatsCommand();
    }

    /**
     * Parses the schedule command arguments.
     *
     * @param arguments The command arguments string.
     * @return A {@link ScheduleCommand} instance.
     * @throws JijiException If arguments are missing, date is non-existent, or date format is invalid.
     */
    private static Command parseSchedule(String arguments) throws JijiException {
        assert arguments != null : "Arguments string cannot be null";
        if (arguments.isEmpty()) {
            throw JijiMissingArgumentException.forEmptySchedule();
        }

        LocalDate targetDate;
        if (arguments.equalsIgnoreCase("today")) {
            targetDate = LocalDate.now();
        } else {
            if (DateTimeUtil.isNonExistentDate(arguments)) {
                throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ That date does not exist on the calendar (e.g. Feb 30). "
                        + "Please provide a valid calendar date.");
            }
            targetDate = DateTimeUtil.parseLocalDate(arguments);
            if (targetDate == null) {
                throw new JijiException("OOPS! ₍^› ꘍ ‹ ^₎⟆ Invalid date format. "
                        + "Please use yyyy-MM-dd (e.g. 2026-08-30) or 'today'.");
            }
        }
        return new ScheduleCommand(targetDate);
    }
}
