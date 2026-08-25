package matthew.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import matthew.command.AddCommand;
import matthew.command.Command;
import matthew.command.DeleteCommand;
import matthew.command.ExitCommand;
import matthew.command.FindCommand;
import matthew.command.ListCommand;
import matthew.command.MarkCommand;
import matthew.command.OnCommand;
import matthew.command.UnmarkCommand;
import matthew.exception.MatthewException;
import matthew.task.Deadline;
import matthew.task.Event;
import matthew.task.Todo;

/** Parses user input into executable Matthew commands. */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** Creates a parser. */
    public Parser() {
    }

    /**
     * Parses a complete user command.
     *
     * @param input Raw command entered by the user.
     * @return Command represented by the input.
     * @throws MatthewException If the input is empty, unknown, or malformed.
     */
    public static Command parse(String input)
            throws MatthewException {

        String trimmed = input.trim();

        if (trimmed.isEmpty()) {
            throw new MatthewException(
                    "I don't recognise an empty command.");
        }

        String commandWord = trimmed.split("\\s+")[0];

        switch (commandWord) {
            case "bye":
                return parseExit(trimmed);

            case "list":
                return parseList(trimmed);

            case "find":
                return new FindCommand(parseFindKeyword(trimmed));

            case "mark":
                return new MarkCommand(
                        parseTaskNumber(trimmed, "mark"));

            case "unmark":
                return new UnmarkCommand(
                        parseTaskNumber(trimmed, "unmark"));

            case "delete":
                return new DeleteCommand(
                        parseTaskNumber(trimmed, "delete"));

            case "todo":
                return new AddCommand(
                        new Todo(parseTodoDescription(trimmed)));

            case "deadline":
                return new AddCommand(
                        parseDeadline(trimmed));

            case "event":
                return new AddCommand(
                        parseEvent(trimmed));

            case "on":
                return new OnCommand(
                        parseOnDate(trimmed));

            default:
                throw new MatthewException(
                        "I don't recognise that command.");
        }
    }

    /**
     * Parses the exit command and rejects additional arguments.
     *
     * @param input Trimmed command input.
     * @return Exit command represented by the input.
     * @throws MatthewException If the input contains arguments.
     */
    private static Command parseExit(String input)
            throws MatthewException {
        if (!input.equals("bye")) {
            throw new MatthewException(
                    "The 'bye' command does not take extra arguments.");
        }
        return new ExitCommand();
    }

    /**
     * Parses the list command and rejects additional arguments.
     *
     * @param input Trimmed command input.
     * @return List command represented by the input.
     * @throws MatthewException If the input contains arguments.
     */
    private static Command parseList(String input)
            throws MatthewException {
        if (!input.equals("list")) {
            throw new MatthewException(
                    "The 'list' command does not take extra arguments.");
        }
        return new ListCommand();
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input Trimmed find command input.
     * @return Keyword to search for.
     * @throws MatthewException If the command has no keyword.
     */
    private static String parseFindKeyword(String input)
            throws MatthewException {

        if (!input.startsWith("find ")) {
            throw new MatthewException(
                    "Please provide a keyword to search for, e.g. find book.");
        }

        String keyword = input.substring(5).trim();

        if (keyword.isEmpty()) {
            throw new MatthewException(
                    "Please provide a keyword to search for, e.g. find book.");
        }

        return keyword;
    }

    /**
     * Parses the one-based task number following a task command.
     *
     * @param input Trimmed command input.
     * @param command Command whose task number is being parsed.
     * @return Parsed task number.
     * @throws MatthewException If the command has no valid task number.
     */
    private static int parseTaskNumber(
            String input, String command)
            throws MatthewException {

        String prefix = command + " ";

        if (!input.startsWith(prefix)) {
            throw new MatthewException(
                    "Please tell me which task to " + command
                            + ", e.g. " + command + " 2.");
        }

        String numberText = input.substring(prefix.length()).trim();

        if (numberText.isEmpty()) {
            throw new MatthewException(
                    "Please tell me which task to " + command
                            + ", e.g. " + command + " 2.");
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new MatthewException(
                    "The task number must be a number.");
        }
    }

    /**
     * Extracts a todo description from the command input.
     *
     * @param input Trimmed todo command input.
     * @return Todo description.
     * @throws MatthewException If the command has no description.
     */
    private static String parseTodoDescription(String input)
            throws MatthewException {

        if (!input.startsWith("todo ")) {
            throw new MatthewException(
                    "A todo needs a description.");
        }

        String description = input.substring(5).trim();

        if (description.isEmpty()) {
            throw new MatthewException(
                    "A todo needs a description.");
        }

        return description;
    }

    /**
     * Parses a deadline command containing a description and due date/time.
     *
     * @param input Trimmed deadline command input.
     * @return Deadline represented by the input.
     * @throws MatthewException If the command format or date/time is invalid.
     */
    private static Deadline parseDeadline(String input)
            throws MatthewException {

        if (!input.startsWith("deadline ")) {
            throw new MatthewException(
                    "A deadline needs a description and /by date/time.");
        }

        String content = input.substring(9).trim();
        int byIndex = content.indexOf(" /by ");

        if (byIndex == -1) {
            throw new MatthewException(
                    "A deadline must include '/by'. "
                            + "Example: deadline return book /by 2019-12-02 1800");
        }

        String description = content.substring(0, byIndex).trim();
        String dateTimeText = content.substring(byIndex + 5).trim();

        if (description.isEmpty()) {
            throw new MatthewException(
                    "A deadline needs a description.");
        }

        return new Deadline(
                description, parseDateTime(dateTimeText));
    }

    /**
     * Parses an event command containing a description and start/end date-times.
     *
     * @param input Trimmed event command input.
     * @return Event represented by the input.
     * @throws MatthewException If the command format or date/time is invalid.
     */
    private static Event parseEvent(String input)
            throws MatthewException {

        if (!input.startsWith("event ")) {
            throw new MatthewException(
                    "An event needs a description, /from date/time and /to date/time.");
        }

        String content = input.substring(6).trim();

        int fromIndex = content.indexOf(" /from ");
        int toIndex = content.indexOf(" /to ");

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new MatthewException(
                    "An event must use '/from' and '/to'. "
                            + "Example: event meeting /from 2019-12-02 1400 "
                            + "/to 2019-12-02 1600");
        }

        String description = content.substring(0, fromIndex).trim();
        String fromText = content.substring(fromIndex + 7, toIndex).trim();
        String toText = content.substring(toIndex + 5).trim();

        if (description.isEmpty()) {
            throw new MatthewException(
                    "An event needs a description.");
        }

        LocalDateTime from = parseDateTime(fromText);
        LocalDateTime to = parseDateTime(toText);

        if (to.isBefore(from)) {
            throw new MatthewException(
                    "The event end time cannot be before its start time.");
        }

        return new Event(description, from, to);
    }

    /**
     * Parses the date supplied to an on command.
     *
     * @param input Trimmed on command input.
     * @return Date on which to search for tasks.
     * @throws MatthewException If the command or date format is invalid.
     */
    private static LocalDate parseOnDate(String input)
            throws MatthewException {

        if (!input.startsWith("on ")) {
            throw new MatthewException(
                    "Please provide a date, e.g. on 2019-12-02.");
        }

        String dateText = input.substring(3).trim();

        try {
            return LocalDate.parse(dateText, INPUT_DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new MatthewException(
                    "Date must use yyyy-MM-dd, e.g. 2019-12-02.");
        }
    }

    /**
     * Parses a date/time using the application's accepted input format.
     *
     * @param input Date/time text in yyyy-MM-dd HHmm format.
     * @return Parsed date/time.
     * @throws MatthewException If the date/time format is invalid.
     */
    private static LocalDateTime parseDateTime(String input)
            throws MatthewException {
        try {
            return LocalDateTime.parse(
                    input, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new MatthewException(
                    "Date/time must use yyyy-MM-dd HHmm, "
                            + "e.g. 2019-12-02 1800.");
        }
    }
}
