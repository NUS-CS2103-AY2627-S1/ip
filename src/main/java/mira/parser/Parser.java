package mira.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mira.command.Command;
import mira.command.CommandType;
import mira.exception.MiraException;
import mira.task.Deadline;
import mira.task.Event;
import mira.task.Task;
import mira.task.Todo;

/**
 * Converts raw user input into validated commands and task objects.
 */
public class Parser {
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^(.+?)\\s+/by\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BY_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/by\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/from\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern TO_DELIMITER_PATTERN = Pattern.compile(
            "\\s+/to\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * Creates a parser for Mira's text command syntax.
     */
    public Parser() {
    }

    /**
     * Parses and validates one complete command line.
     *
     * @param input complete user input.
     * @return the parsed command.
     * @throws MiraException if the input does not form a valid command.
     */
    public Command parse(String input) throws MiraException {
        if (input == null || input.isBlank()) {
            throw new MiraException("Please enter a command.");
        }
        if (input.contains("\n") || input.contains("\r")) {
            throw new MiraException("Please enter one command on a single line.");
        }

        String[] commandParts = input.strip().split("\\s+", 2);
        CommandType type = CommandType.fromWord(commandParts[0]);
        String arguments = commandParts.length == 2 ? commandParts[1].trim() : "";

        switch (type) {
            case BYE:
                ensureNoArguments(arguments, "bye");
                return Command.withoutArguments(type);
            case LIST:
                ensureNoArguments(arguments, "list");
                return Command.withoutArguments(type);
            case TODO:
                return Command.withTask(type, parseTodo(arguments));
            case DEADLINE:
                return Command.withTask(type, parseDeadline(arguments));
            case EVENT:
                return Command.withTask(type, parseEvent(arguments));
            case MARK, UNMARK, DELETE:
                return Command.withTaskNumber(type, parseTaskNumber(arguments));
            case FIND:
                return Command.withKeyword(type, parseKeyword(arguments));
            case UNKNOWN:
                throw new MiraException("I'm sorry, but I don't know what that means :-(");
            default:
                throw new MiraException("That command is not supported.");
        }
    }

    /**
     * Parses a todo description.
     *
     * @param arguments Text following the todo command word.
     * @return A todo containing the supplied description.
     * @throws MiraException If the description is empty.
     */
    private Task parseTodo(String arguments) throws MiraException {
        if (arguments.isBlank()) {
            throw new MiraException("The description of a todo cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Parses a deadline description and strict ISO date.
     *
     * @param arguments Text following the deadline command word.
     * @return A deadline containing the parsed description and date.
     * @throws MiraException If the delimiter or date is invalid.
     */
    private Task parseDeadline(String arguments) throws MiraException {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(BY_DELIMITER_PATTERN, arguments) || !matcher.matches()
                || matcher.group(1).isBlank() || matcher.group(2).isBlank()) {
            throw new MiraException(
                    "A deadline must follow: deadline DESCRIPTION /by YYYY-MM-DD.");
        }

        try {
            LocalDate by = LocalDate.parse(matcher.group(2).trim());
            return new Deadline(matcher.group(1).trim(), by);
        } catch (DateTimeParseException exception) {
            throw new MiraException("A deadline date must use YYYY-MM-DD.");
        }
    }

    /**
     * Parses an event description, start, and end value.
     *
     * @param arguments Text following the event command word.
     * @return An event containing the parsed fields.
     * @throws MiraException If either delimiter is missing or ambiguous.
     */
    private Task parseEvent(String arguments) throws MiraException {
        Matcher matcher = EVENT_PATTERN.matcher(arguments);
        if (!hasExactlyOneMatch(FROM_DELIMITER_PATTERN, arguments)
                || !hasExactlyOneMatch(TO_DELIMITER_PATTERN, arguments)
                || !matcher.matches() || matcher.group(1).isBlank()
                || matcher.group(2).isBlank() || matcher.group(3).isBlank()) {
            throw new MiraException(
                    "An event must follow: event DESCRIPTION /from START /to END.");
        }
        return new Event(
                matcher.group(1).trim(),
                matcher.group(2).trim(),
                matcher.group(3).trim());
    }

    /**
     * Parses a single positive integer task number.
     *
     * @param arguments Text expected to contain the task number.
     * @return The parsed one-based task number.
     * @throws MiraException If the value is missing, malformed, or too large.
     */
    private int parseTaskNumber(String arguments) throws MiraException {
        if (!arguments.matches("\\d+")) {
            throw new MiraException("Please provide a valid task number.");
        }

        try {
            int taskNumber = Integer.parseInt(arguments);
            if (taskNumber < 1) {
                throw new MiraException("Please provide a valid task number.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new MiraException("That task number is too large.");
        }
    }

    /**
     * Parses and validates the keyword supplied to a find command.
     *
     * @param arguments Text following the find command word.
     * @return The search keyword.
     * @throws MiraException If the keyword is empty.
     */
    private String parseKeyword(String arguments) throws MiraException {
        if (arguments.isBlank()) {
            throw new MiraException("Please provide a keyword to find.");
        }
        return arguments;
    }

    /**
     * Rejects text supplied to a command that takes no arguments.
     *
     * @param arguments Text following the command word.
     * @param command Command name used in the error message.
     * @throws MiraException If unexpected arguments are present.
     */
    private void ensureNoArguments(String arguments, String command) throws MiraException {
        if (!arguments.isBlank()) {
            throw new MiraException("The " + command + " command does not take extra text.");
        }
    }

    /**
     * Checks whether a delimiter occurs exactly once.
     *
     * @param pattern Delimiter pattern to find.
     * @param input Input in which the delimiter is expected.
     * @return {@code true} if there is exactly one match.
     */
    private static boolean hasExactlyOneMatch(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() && !matcher.find();
    }
}
