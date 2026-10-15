package pip.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import pip.command.AddCommand;
import pip.command.Command;
import pip.command.DeleteCommand;
import pip.command.ExitCommand;
import pip.command.FindCommand;
import pip.command.ListCommand;
import pip.command.MarkCommand;
import pip.exception.ParserException;
import pip.task.Deadline;
import pip.task.Event;
import pip.task.Task;
import pip.task.Todo;


/**
 * Parses raw user input into executable {@link Command} objects.
 */
public class Parser {

    /** Prevents instantiation; commands are parsed through the static parse method. */
    private Parser() {
    }

    /** Fixed short aliases for the commands supported by Pip. */
    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "b", "bye",
            "l", "list",
            "m", "mark",
            "um", "unmark",
            "d", "delete",
            "f", "find",
            "t", "todo",
            "dl", "deadline",
            "e", "event");

    /** Optional woodland aliases; the original command names and short aliases remain valid. */
    private static final Map<String, String> WOODLAND_ALIASES = Map.of(
            "gather", "todo", "stash", "list", "sniff", "find", "scamper", "bye");

    /**
     * Parses one line of user input into a {@link Command}.
     *
     * @param fullInput The raw command line entered by the user.
     * @return The Command corresponding to the input.
     * @throws ParserException If the input is empty, uses an unknown command word,
     *         or does not follow the expected format for its command word.
     */
    public static Command parse(String fullInput) throws ParserException {
        if (fullInput == null || fullInput.trim().isEmpty()) {
            throw new ParserException("Sorry! Please enter a command.");
        }

        String trimmed = fullInput.trim();
        String[] parts = trimmed.split("\\s+", 2);
        assert parts.length >= 1 : "A non-empty command must have a command word";
        String commandWord = parts[0].toLowerCase();
        commandWord = COMMAND_ALIASES.getOrDefault(commandWord, commandWord);
        commandWord = WOODLAND_ALIASES.getOrDefault(commandWord, commandWord);
        String arguments = parts.length > 1 ? parts[1].trim() : "";

        return parseCommand(commandWord, arguments);
    }

    /** Dispatches a validated command word to the parser for its arguments. */
    private static Command parseCommand(String commandWord, String arguments) throws ParserException {
        switch (commandWord) {
            case "bye":
                requireNoDetails("bye", arguments);
                return new ExitCommand();

            case "list":
                requireNoDetails("list", arguments);
                return new ListCommand();

            case "mark":
            case "unmark":
                int markIndex = parseIndex(arguments);
                return new MarkCommand(markIndex, commandWord.equals("mark"));

            case "delete":
                int deleteIndex = parseIndex(arguments);
                return new DeleteCommand(deleteIndex);

            case "find":
                String keyword = parseFindKeyword(arguments);
                return new FindCommand(keyword);

            case "todo":
                Task todo = parseTodo(arguments);
                return new AddCommand(todo);

            case "deadline":
                Task deadline = parseDeadline(arguments);
                return new AddCommand(deadline);

            case "event":
                Task event = parseEvent(arguments);
                return new AddCommand(event);

            default:
                throw new ParserException("Sorry! Unknown task type: " + commandWord);
        }
    }

    /**
     * Ensures a command that takes no arguments was not given any.
     *
     * @param commandWord The command word, used in the error message if validation fails.
     * @param details The argument text following the command word.
     * @throws ParserException If {@code details} is non-empty.
     */
    private static void requireNoDetails(String commandWord, String details) throws ParserException {
        if (!details.isEmpty()) {
            throw new ParserException("Sorry! Please follow the format: " + commandWord);
        }
    }

    /**
     * Parses a one-based task index from user input into a zero-based index.
     *
     * @param details The raw index text supplied by the user.
     * @return The zero-based index.
     * @throws ParserException If {@code details} is not a valid non-negative integer
     *         after conversion (i.e. the one-based input is not a positive integer).
     */
    private static int parseIndex(String details) throws ParserException {
        try {
            int index = Integer.parseInt(details) - 1;
            if (index < 0) {
                throw new ParserException("Sorry! Invalid task index!");
            }
            assert index >= 0 : "parseIndex must return a zero-based non-negative index";
            return index;
        } catch (NumberFormatException e) {
            throw new ParserException("Sorry! Invalid task index!");
        }
    }

    /**
     * Parses the arguments of a {@code find} command into a search keyword.
     *
     * @param details The keyword text supplied by the user.
     * @return The keyword to search for.
     * @throws ParserException If the keyword is empty.
     */
    private static String parseFindKeyword(String details) throws ParserException {
        if (details.isEmpty()) {
            throw new ParserException("Sorry! Please follow the format: find KEYWORD");
        }
        return details;
    }

    /**
     * Parses the arguments of a {@code todo} command into a {@link Todo} task.
     *
     * @param details The task description supplied by the user.
     * @return The parsed Todo task.
     * @throws ParserException If the description is empty.
     */
    private static Task parseTodo(String details) throws ParserException {
        if (details.isEmpty()) {
            throw new ParserException("Sorry! Please follow the format: todo DESCRIPTION");
        }
        return new Todo(details);
    }

    /**
     * Parses the arguments of a {@code deadline} command into a {@link Deadline} task.
     *
     * @param details The task description and date, in the form {@code DESCRIPTION /by DATE}.
     * @return The parsed Deadline task.
     * @throws ParserException If the description or date is missing, or the date
     *         is not in {@code yyyy-MM-dd} format.
     */
    private static Task parseDeadline(String details) throws ParserException {
        String[] parts = details.split(" /by ", 2);
        boolean hasDescription = parts.length >= 2 && !parts[0].isEmpty();
        boolean hasDate = parts.length >= 2 && !parts[1].isEmpty();
        if (!hasDescription || !hasDate) {
            throw new ParserException("Sorry! Please follow the format: deadline DESCRIPTION /by DATE");
        }
        try {
            LocalDate byDate = LocalDate.parse(parts[1]);
            return new Deadline(parts[0], byDate);
        } catch (DateTimeParseException e) {
            throw new ParserException("Sorry! Please use a valid date in the format yyyy-MM-dd.");
        }
    }

    /**
     * Parses the arguments of an {@code event} command into an {@link Event} task.
     *
     * @param details The task description and dates, in the form
     *         {@code DESCRIPTION /from START /to END}.
     * @return The parsed Event task.
     * @throws ParserException If the description or either date is missing, either
     *         date is not in {@code yyyy-MM-dd} format, or the end date precedes the start date.
     */
    private static Task parseEvent(String details) throws ParserException {
        String[] tokens = details.split("\\s+");
        Map<String, String> dates = new HashMap<>();
        StringBuilder description = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.equals("/from") || token.equals("/to")) {
                if (dates.containsKey(token) || i + 1 >= tokens.length
                        || tokens[i + 1].startsWith("/")) {
                    throw invalidEventFormat();
                }
                dates.put(token, tokens[++i]);
            } else {
                if (description.length() > 0) {
                    description.append(' ');
                }
                description.append(token);
            }
        }

        if (description.length() == 0 || !dates.containsKey("/from") || !dates.containsKey("/to")) {
            throw invalidEventFormat();
        }

        try {
            LocalDate fromDate = LocalDate.parse(dates.get("/from"));
            LocalDate toDate = LocalDate.parse(dates.get("/to"));
            if (toDate.isBefore(fromDate)) {
                throw new ParserException("Sorry! The event end date must not be before its start date.");
            }
            assert !toDate.isBefore(fromDate) : "Parsed event dates must be in chronological order";
            return new Event(description.toString(), fromDate, toDate);
        } catch (DateTimeParseException e) {
            throw new ParserException("Sorry! Please use valid dates in the format yyyy-MM-dd.");
        }
    }

    /** Creates the standard error for a malformed event command. */
    private static ParserException invalidEventFormat() {
        return new ParserException("Sorry! Please follow the format: event DESCRIPTION /from START /to END");
    }
}
