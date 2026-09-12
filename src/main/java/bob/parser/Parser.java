package bob.parser;

import bob.command.AddCommand;
import bob.command.ByeCommand;
import bob.command.Command;
import bob.command.CommandType;
import bob.command.DeleteCommand;
import bob.command.FindCommand;
import bob.command.ListCommand;
import bob.command.MarkCommand;
import bob.command.SearchCommand;
import bob.command.UnmarkCommand;
import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.TaskDateTime;
import bob.task.ToDo;

/**
 * Converts raw user input into command objects.
 */
public class Parser {
    private static final String DEADLINE_EXAMPLE =
            "deadline submit report /by 2/12/2019 1800";
    private static final String EVENT_EXAMPLE =
            "event meeting /from 2/12/2019 1800 /to 2/12/2019 1900";

    /**
     * Parses a line of user input into an executable command.
     *
     * @param input complete line entered by the user.
     * @return command represented by the input.
     * @throws BobException if the command or its arguments are invalid.
     */
    public Command parse(String input) throws BobException {
        if (input == null || input.trim().isEmpty()) {
            throw new BobException("Please enter a command, such as 'todo read chapter 1'.");
        }

        String[] words = input.trim().split("\\s+", 2);
        CommandType commandType = CommandType.fromKeyword(words[0])
                .orElseThrow(this::createUnknownCommandException);
        String argument = words.length == 2 ? words[1].trim() : "";

        return parseCommand(commandType, argument);
    }

    /**
     * Creates the command represented by a validated command type and argument.
     *
     * @param commandType parsed command type.
     * @param argument text following the command keyword.
     * @return command represented by the input.
     * @throws BobException if the argument is invalid.
     */
    private Command parseCommand(CommandType commandType, String argument) throws BobException {
        switch (commandType) {
            case BYE:
                requireNoArgument(argument, commandType);
                return new ByeCommand();
            case LIST:
                requireNoArgument(argument, commandType);
                return new ListCommand();
            case FIND:
                return new FindCommand(requireText(argument, "'find' needs a keyword. Try: find book"));
            case SEARCH:
                return new SearchCommand(requireText(argument, "'search' needs a keyword. Try: search book"));
            case MARK:
                return new MarkCommand(parseTaskNumber(argument, commandType));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(argument, commandType));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(argument, commandType));
            case TODO:
                return new AddCommand(new ToDo(requireText(argument,
                        "A todo needs a description. Try: todo read chapter 1")));
            case DEADLINE:
                return parseDeadline(argument);
            case EVENT:
                return parseEvent(argument);
            default:
                throw createUnknownCommandException();
        }
    }

    /**
     * Parses a deadline description and due date into an add command.
     *
     * @param argument text following the deadline keyword.
     * @return command that adds the parsed deadline.
     * @throws BobException if the description or due date is missing.
     */
    private Command parseDeadline(String argument) throws BobException {
        requireSingleParameter(argument, "/by", DEADLINE_EXAMPLE);
        String[] parts = argument.split("\\s+/by\\s+", 2);
        if (parts.length < 2) {
            throw new BobException("A deadline needs a due date. Try: " + DEADLINE_EXAMPLE);
        }
        String description = requireText(parts[0].trim(), "A deadline needs a description before '/by'.");
        TaskDateTime dueDate = parseDateTime(parts[1].trim());
        return new AddCommand(new Deadline(description, dueDate));
    }

    /**
     * Parses an event description and date range into an add command.
     *
     * @param argument text following the event keyword.
     * @return command that adds the parsed event.
     * @throws BobException if the description or either date is missing.
     */
    private Command parseEvent(String argument) throws BobException {
        requireSingleParameter(argument, "/from", EVENT_EXAMPLE);
        requireSingleParameter(argument, "/to", EVENT_EXAMPLE);
        String[] fromParts = argument.split("\\s+/from\\s+", 2);
        if (fromParts.length < 2) {
            throw new BobException("An event needs a time range. Try: " + EVENT_EXAMPLE);
        }
        String description = requireText(fromParts[0].trim(),
                "An event needs a description before '/from'.");

        String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
        if (toParts.length < 2) {
            throw new BobException("An event needs an end time after '/to'. Try: " + EVENT_EXAMPLE);
        }
        TaskDateTime from = parseDateTime(toParts[0].trim());
        TaskDateTime to = parseDateTime(toParts[1].trim());

        try {
            return new AddCommand(new Event(description, from, to));
        } catch (IllegalArgumentException exception) {
            throw new BobException(exception.getMessage());
        }
    }

    /**
     * Rejects repeated or malformed date-range parameters.
     *
     * @param argument command argument to inspect.
     * @param parameter parameter name to require once.
     * @param example usage example for the error response.
     * @throws BobException if the parameter is missing or repeated.
     */
    private void requireSingleParameter(String argument, String parameter, String example) throws BobException {
        int occurrences = 0;
        String[] tokens = argument.trim().split("\\s+");
        for (String token : tokens) {
            if (token.equals(parameter)) {
                occurrences++;
            }
        }
        if (occurrences > 1) {
            throw new BobException("The parameter '" + parameter + "' may be specified only once. Try: " + example);
        }
    }

    /**
     * Parses a task date-time and converts format errors into user-facing chatbot errors.
     *
     * @param text date-time entered by the user.
     * @return parsed task date-time.
     * @throws BobException if the date-time does not use a supported format.
     */
    private TaskDateTime parseDateTime(String text) throws BobException {
        try {
            return DateTimeParser.parseUserInput(text);
        } catch (IllegalArgumentException exception) {
            throw new BobException(exception.getMessage());
        }
    }

    /**
     * Parses and validates the one-based task number used by a command.
     *
     * @param argument text expected to contain a task number.
     * @param command command for which the number is being parsed.
     * @return positive one-based task number.
     * @throws BobException if the argument is not a positive whole number.
     */
    private int parseTaskNumber(String argument, CommandType command) throws BobException {
        if (argument.isEmpty()) {
            throw new BobException("'" + command.getKeyword() + "' needs a task number. Try: "
                    + command.getKeyword() + " 1");
        }
        try {
            int taskNumber = Integer.parseInt(argument);
            if (taskNumber <= 0) {
                throw new BobException("Task numbers start at 1. Use 'list' to see the available tasks.");
            }
            assert taskNumber > 0 : "Parsed task numbers must be positive and one-based";
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new BobException("'" + command.getKeyword()
                    + "' needs a whole-number task index, such as 1.");
        }
    }

    /**
     * Ensures that a task detail is present and safe for pipe-delimited storage.
     *
     * @param text task detail to validate.
     * @param message error message used when the detail is empty.
     * @return the validated text.
     * @throws BobException if the text is empty or contains the field separator.
     */
    private String requireText(String text, String message) throws BobException {
        if (text.isEmpty()) {
            throw new BobException(message);
        }
        if (text.contains(Storage.FIELD_SEPARATOR)) {
            throw new BobException("The character '|' is not allowed in task details.");
        }
        return text;
    }

    /**
     * Rejects unexpected arguments supplied to an argument-free command.
     *
     * @param argument text following the command keyword.
     * @param command command being validated.
     * @throws BobException if an argument was supplied.
     */
    private void requireNoArgument(String argument, CommandType command) throws BobException {
        if (!argument.isEmpty()) {
            throw new BobException("'" + command.getKeyword() + "' does not take extra arguments.");
        }
    }

    private BobException createUnknownCommandException() {
        return new BobException("I don't recognise that command. Try 'todo', 'deadline', 'event', 'list', 'find', "
                + "'search', 'mark', 'unmark', 'delete', or 'bye'.");
    }
}
