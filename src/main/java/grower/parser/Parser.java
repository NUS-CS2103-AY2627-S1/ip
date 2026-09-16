package grower.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

import grower.commands.ByeCommand;
import grower.commands.Command;
import grower.commands.DeadlineCommand;
import grower.commands.DeleteCommand;
import grower.commands.EchoCommand;
import grower.commands.EventCommand;
import grower.commands.FindCommand;
import grower.commands.ListCommand;
import grower.commands.MarkCommand;
import grower.commands.SortCommand;
import grower.commands.ToDoCommand;
import grower.commands.UnmarkCommand;
import grower.exceptions.GrowerException;
import grower.exceptions.MissingDescriptionException;
import grower.exceptions.UnknownCommandException;

/**
 * Converts user input into executable commands.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm")
                    .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Represents the command words recognized by the parser.
     */
    private enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        TODO,
        DEADLINE,
        EVENT,
        ECHO,
        DELETE,
        FIND,
        SORT
    }

    /**
     * Converts a one-based user task number to a zero-based index.
     * TaskList checks whether the resulting index refers to an existing task.
     *
     * @param args Task number supplied by the user.
     * @param command Command requiring the task number, used in error messages.
     * @return Zero-based task index.
     * @throws GrowerException If the task number is missing or is not an integer.
     */
    private static int parseTaskIndex(String args, CommandType command) throws GrowerException {
        if (args.isEmpty()) {
            throw new GrowerException("Hoom Hum! Young one, don't be hasty, provide a task number to " + command + ".");
        }
        try {
            int number = Integer.parseInt(args);
            if (number <= 0) {
                throw new GrowerException("Hoom! Task numbers start at 1. Use list to see your tasks.");
            }
            return number - 1;
        } catch (NumberFormatException e) {
            throw new GrowerException("Hum! Friend the task number must be an integer.");
        }
    }

    /**
     * Creates a mark command from a user-supplied task number.
     *
     * @param args Task number to mark.
     * @return Command that marks the selected task as completed.
     * @throws GrowerException If the task number is missing or is not an integer.
     */
    private static Command parseMark(String args) throws GrowerException {
        int index = parseTaskIndex(args, CommandType.MARK);
        return new MarkCommand(index);
    }

    /**
     * Creates an unmark command from a user-supplied task number.
     *
     * @param args Task number to unmark.
     * @return Command that marks the selected task as not completed.
     * @throws GrowerException If the task number is missing or is not an integer.
     */
    private static Command parseUnmark(String args) throws GrowerException {
        int index = parseTaskIndex(args, CommandType.UNMARK);
        return new UnmarkCommand(index);
    }

    /**
     * Validates a description and creates a to-do command.
     *
     * @param args Description of the to-do task.
     * @return Command that adds the to-do task.
     * @throws GrowerException If the description is empty.
     */
    private static Command parseTodo(String args) throws GrowerException {
        if (args.isEmpty()) {
            throw new MissingDescriptionException(
                    "Why so hasty little one, the description for a todo cannot be empty.");
        }
        return new ToDoCommand(args);
    }

    /**
     * Parses a task description and its deadline using the /by separator.
     *
     * @param args Description and deadline in the user input format.
     * @return Command that adds the deadline task.
     * @throws GrowerException If the arguments are empty or the format or date is invalid.
     */
    private static Command parseDeadline(String args) throws GrowerException {
        if (args.isEmpty()) {
            throw new MissingDescriptionException(
                    "Why so hasty little one, the description for a deadline cannot be empty.");
        }
        String[] deadlineParts = args.split("\\s+/by\\s+", 2);
        if (deadlineParts.length < 2) {
            throw new GrowerException(
                    "Hoomm! Invalid deadline format. Use: deadline <description> /by <d/M/yyyy HHmm>");
        }
        try {
            LocalDateTime deadline = LocalDateTime.parse(
                    deadlineParts[1].trim(), INPUT_DATE_TIME_FORMATTER);
            return new DeadlineCommand(deadlineParts[0].trim(), deadline);
        } catch (DateTimeParseException e) {
            throw new GrowerException(
                    "Hoomm! Use the date format d/M/yyyy HHmm, for example: 28/8/2026 1800.");
        }
    }

    /**
     * Parses an event description and time range using the /from and /to separators.
     *
     * @param args Description, start time, and end time in the user input format.
     * @return Command that adds the event task.
     * @throws GrowerException If the arguments are empty, the format or dates are invalid,
     *     or the end is not after the start.
     */
    private static Command parseEvent(String args) throws GrowerException {
        if (args.isEmpty()) {
            throw new MissingDescriptionException(
                    "Why so hasty little one, the description for an event cannot be empty.");
        }
        String[] eventParts = args.split("\\s+/from\\s+", 2);
        if (eventParts.length < 2) {
            throw new GrowerException("Hoom! Invalid event format. Use: event <desc> /from <start> /to <end>");
        }
        String[] timeParts = eventParts[1].split("\\s+/to\\s+", 2);
        if (timeParts.length < 2) {
            throw new GrowerException("Hoom! Invalid event format. Use: event <desc> /from <start> /to <end>");
        }
        try {
            LocalDateTime start = LocalDateTime.parse(timeParts[0].trim(), INPUT_DATE_TIME_FORMATTER);
            LocalDateTime end = LocalDateTime.parse(timeParts[1].trim(), INPUT_DATE_TIME_FORMATTER);

            if (!end.isAfter(start)) {
                throw new GrowerException("Why the rush? The event end must be after its start.");
            }

            return new EventCommand(eventParts[0].trim(), start, end);
        } catch (DateTimeParseException e) {
            throw new GrowerException(
                    "Hoom! Use the date format d/M/yyyy HHmm, for example: 28/8/2026 1800.");
        }
    }

    /**
     * Creates a delete command from a user-supplied task number.
     *
     * @param args Task number to delete.
     * @return Command that deletes the selected task.
     * @throws GrowerException If the task number is missing or is not an integer.
     */
    private static Command parseDelete(String args) throws GrowerException {
        int index = parseTaskIndex(args, CommandType.DELETE);
        return new DeleteCommand(index);
    }

    /**
     * Returns the command represented by the supplied user input.
     *
     * @param userInput Full user input.
     * @return Command ready for execution.
     * @throws GrowerException If the input is invalid or malformed.
     */
    public static Command parse(String userInput) throws GrowerException {
        validateInput(userInput);
        String[] parts = userInput.strip().split("\\s+", 2);
        String commandWord = parts[0];
        String args = parts.length > 1 ? parts[1].strip() : "";

        CommandType commandType = parseCommandType(commandWord);
        validateArguments(commandType, commandWord, args);
        return createCommand(commandType, args);
    }

    /**
     * Rejects empty input and characters that cannot occur in a single command.
     */
    private static void validateInput(String userInput) throws GrowerException {
        if (userInput == null || userInput.isBlank()) {
            throw new UnknownCommandException("Hoom! Enter a command, such as list or todo read book.");
        }
        if (userInput.contains("\n") || userInput.contains("\r") || userInput.indexOf('\0') >= 0) {
            throw new GrowerException("Hoom! Please enter one command on a single line.");
        }
    }

    /**
     * Recognizes a command word independently of the user's locale and letter case.
     */
    private static CommandType parseCommandType(String commandWord) throws UnknownCommandException {
        try {
            return CommandType.valueOf(commandWord.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new UnknownCommandException("Even for one as wise as me, this task is beyond my strength.");
        }
    }

    /**
     * Enforces argument restrictions shared by groups of commands.
     */
    private static void validateArguments(CommandType commandType, String commandWord, String args)
            throws GrowerException {
        boolean takesNoArguments = commandType == CommandType.BYE || commandType == CommandType.LIST
                || commandType == CommandType.SORT;
        if (takesNoArguments && !args.isEmpty()) {
            throw new GrowerException("Hoom! " + commandWord + " does not take any arguments.");
        }
        boolean createsTask = commandType == CommandType.TODO || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT;
        if (createsTask && args.contains("|")) {
            throw new GrowerException("Hoom! Please remove the | character; it separates fields in saved tasks.");
        }
    }

    /**
     * Creates the selected command using its command-specific argument parser.
     */
    private static Command createCommand(CommandType commandType, String args) throws GrowerException {
        switch (commandType) {
            case BYE:
                return new ByeCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                return parseMark(args);
            case UNMARK:
                return parseUnmark(args);
            case TODO:
                return parseTodo(args);
            case DEADLINE:
                return parseDeadline(args);
            case EVENT:
                return parseEvent(args);
            case SORT:
                return new SortCommand();
            case ECHO:
                return parseEcho(args);
            case DELETE:
                return parseDelete(args);
            case FIND:
                return parseFind(args);
            default:
                assert false : "Missing switch case for command type: " + commandType;
                throw new UnknownCommandException("Even for one as wise as me, this task is beyond my strength.");
        }
    }

    /**
     * Creates an echo command after checking that a message was supplied.
     */
    private static Command parseEcho(String args) throws MissingDescriptionException {
        if (args.isEmpty()) {
            throw new MissingDescriptionException("There is not echo without first, a sound!");
        }
        return new EchoCommand(args);
    }

    /**
     * Creates a find command after checking that a search keyword was supplied.
     */
    private static Command parseFind(String args) throws MissingDescriptionException {
        if (args.isEmpty()) {
            throw new MissingDescriptionException("Hoom Hum! Speak forth your search request first.");
        }
        return new FindCommand(args.trim());
    }
}
