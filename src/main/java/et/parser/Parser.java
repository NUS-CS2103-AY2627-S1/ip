package et.parser;

import et.command.AddCommand;
import et.command.Command;
import et.command.DeleteCommand;
import et.command.ExitCommand;
import et.command.FindCommand;
import et.command.ListCommand;
import et.command.MarkCommand;
import et.command.SortCommand;
import et.command.UnmarkCommand;
import et.exception.ETException;
import et.task.Deadline;
import et.task.Event;
import et.task.Task;
import et.task.Todo;

/**
 * Interprets user commands and converts their task-related parts into objects.
 */
public class Parser {
    /**
     * Converts one full user command into the command object that will execute it.
     *
     * @param command the command entered by the user
     * @return the matching command object
     * @throws ETException if the command is unknown or missing required information
     */
    public Command parseCommand(String command) throws ETException {
        CommandType commandType = CommandType.fromInput(command);
        switch (commandType) {
        case TODO:
        case DEADLINE:
        case EVENT:
            return new AddCommand(parseTask(command, commandType));
        case LIST:
            if (command.equals(CommandType.LIST.getKeyword())) {
                return new ListCommand();
            }
            break;
        case SORT:
            if (command.equals(CommandType.SORT.getKeyword())) {
                return new SortCommand();
            }
            break;
        case FIND:
            return new FindCommand(parseKeyword(command, commandType));
        case MARK:
            return new MarkCommand(parseTaskIndex(command, commandType));
        case UNMARK:
            return new UnmarkCommand(parseTaskIndex(command, commandType));
        case DELETE:
            return new DeleteCommand(parseTaskIndex(command, commandType));
        case BYE:
            if (command.equals(CommandType.BYE.getKeyword())) {
                return new ExitCommand();
            }
            break;
        default:
            break;
        }
        throw new ETException("Hmm... my Earth decoder doesn't know that command. Try todo, deadline, event, "
                + "list, sort, find, mark, unmark, delete, or bye.");
    }

    /**
     * Converts a task-creation command into a task, including its date and time input.
     *
     * @param command the command entered by the user
     * @param commandType the recognized type of the command
     * @return the task represented by the command
     * @throws ETException if the command is unknown or missing required information
     */
    private Task parseTask(String command, CommandType commandType) throws ETException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands can be parsed as tasks";

        String taskDetails = command.substring(commandType.getKeyword().length()).trim();
        return switch (commandType) {
        case TODO -> parseTodo(taskDetails);
        case DEADLINE -> parseDeadline(taskDetails);
        case EVENT -> parseEvent(taskDetails);
        default -> throw new IllegalArgumentException("Unsupported task command");
        };
    }

    /**
     * Converts to-do details into a task.
     *
     * @param taskDetails the text following the to-do command keyword
     * @return the to-do represented by the details
     * @throws ETException if the description is missing
     */
    private Todo parseTodo(String taskDetails) throws ETException {
        requireText(taskDetails, "Please provide a description for the ToDo.");
        return new Todo(taskDetails);
    }

    /**
     * Converts deadline details into a task.
     *
     * @param taskDetails the text following the deadline command keyword
     * @return the deadline represented by the details
     * @throws ETException if the description or deadline date is missing or invalid
     */
    private Deadline parseDeadline(String taskDetails) throws ETException {
        int byMarker = taskDetails.indexOf("/by");
        if (byMarker < 0) {
            throw new ETException("My Earth calendar needs /by followed by the deadline date or time.");
        }

        String description = taskDetails.substring(0, byMarker).trim();
        String by = taskDetails.substring(byMarker + "/by".length()).trim();
        requireText(description, "Please provide a description for the deadline.");
        requireText(by, "Please provide a date or time after /by.");
        DateTimeParser.ParsedDateTime deadlineDate = DateTimeParser.parse(by);
        return new Deadline(description, deadlineDate.value(), deadlineDate.hasTime());
    }

    /**
     * Converts event details into a task.
     *
     * @param taskDetails the text following the event command keyword
     * @return the event represented by the details
     * @throws ETException if required event details are missing, invalid, or reversed
     */
    private Event parseEvent(String taskDetails) throws ETException {
        int fromMarker = taskDetails.indexOf("/from");
        int toMarker = fromMarker < 0
                ? -1
                : taskDetails.indexOf("/to", fromMarker + "/from".length());
        if (fromMarker < 0 || toMarker < 0) {
            throw new ETException("My Earth calendar needs both /from and /to for the event time.");
        }

        String description = taskDetails.substring(0, fromMarker).trim();
        String from = taskDetails.substring(fromMarker + "/from".length(), toMarker).trim();
        String to = taskDetails.substring(toMarker + "/to".length()).trim();
        requireText(description, "Please provide a description for the event.");
        requireText(from, "Please provide a starting date or time after /from.");
        requireText(to, "Please provide an ending date or time after /to.");

        DateTimeParser.ParsedDateTime startDate = DateTimeParser.parse(from);
        DateTimeParser.ParsedDateTime endDate = DateTimeParser.parse(to);
        if (endDate.value().isBefore(startDate.value())) {
            throw new ETException("Oops! An event cannot end before it starts.");
        }
        return new Event(description, startDate.value(), startDate.hasTime(),
                endDate.value(), endDate.hasTime());
    }

    /**
     * Extracts the one-based task number used by task commands.
     *
     * @param command the full command entered by the user
     * @param commandType the command that accepts a task number
     * @return the zero-based index of the referenced task
     * @throws ETException if the task number is absent or invalid
     */
    private int parseTaskIndex(String command, CommandType commandType) throws ETException {
        String taskNumber = command.substring(commandType.getKeyword().length()).trim();
        try {
            return Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException e) {
            throw new ETException("My scanner needs a valid task number after " + commandType.getKeyword() + ".");
        }
    }

    /**
     * Extracts the keyword used to search task descriptions.
     *
     * @param command the full command entered by the user
     * @param commandType the command that accepts a search keyword
     * @return the keyword without surrounding whitespace
     * @throws ETException if the keyword is absent
     */
    private String parseKeyword(String command, CommandType commandType) throws ETException {
        String keyword = command.substring(commandType.getKeyword().length()).trim();
        requireText(keyword, "Please provide a keyword after find.");
        return keyword;
    }

    /**
     * Ensures that a required command component is present.
     *
     * @param value the command component to check
     * @param errorMessage the message to use when it is missing
     * @throws ETException if the command component is blank
     */
    private void requireText(String value, String errorMessage) throws ETException {
        if (value.isBlank()) {
            throw new ETException(errorMessage);
        }
    }
}
