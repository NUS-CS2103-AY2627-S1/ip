package potatobot.logic.parser;

import potatobot.exception.PotatoBotException;
import potatobot.logic.command.AddCommand;
import potatobot.logic.command.Command;
import potatobot.logic.command.CommandType;
import potatobot.logic.command.DeleteCommand;
import potatobot.logic.command.ExitCommand;
import potatobot.logic.command.FindCommand;
import potatobot.logic.command.ListCommand;
import potatobot.logic.command.MarkCommand;
import potatobot.logic.command.UndoCommand;
import potatobot.logic.command.UnmarkCommand;
import potatobot.model.task.Deadline;
import potatobot.model.task.Event;
import potatobot.model.task.Task;
import potatobot.model.task.Todo;

/**
 * Converts raw user input into commands that PotatoBot can execute.
 */
public class Parser {
    private static final String INVALID_COMMAND_MESSAGE = "Me no gets?";

    /**
     * Creates a parser for PotatoBot commands.
     */
    public Parser() {
    }

    /**
     * Parses one line of user input.
     *
     * @param input User input to parse.
     * @return Structured command represented by the input.
     * @throws PotatoBotException If the command or its arguments are invalid.
     */
    public Command parse(String input) throws PotatoBotException {
        String[] inputParts = input.split(" ", 2);
        CommandType commandType = CommandType.parse(inputParts[0]);

        if (inputParts.length == 1) {
            return switch (commandType) {
                case LIST -> new ListCommand();
                case UNDO -> new UndoCommand();
                case BYE -> new ExitCommand();
                default -> throw new PotatoBotException(INVALID_COMMAND_MESSAGE);
            };
        }

        String argument = inputParts[1];
        return switch (commandType) {
            case MARK -> new MarkCommand(parseTaskNumber(argument));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(argument));
            case DELETE -> new DeleteCommand(parseTaskNumber(argument));
            case ADD -> new AddCommand(new Task(argument));
            case TODO -> new AddCommand(new Todo(argument));
            case DEADLINE -> new AddCommand(parseDeadline(argument));
            case EVENT -> new AddCommand(parseEvent(argument));
            case LIST, UNDO, BYE -> throw new PotatoBotException(INVALID_COMMAND_MESSAGE);
            case FIND -> new FindCommand(argument);
        };
    }

    /**
     * Parses a task number used by a mark, unmark, or delete command.
     *
     * @param argument Text expected to contain a whole number.
     * @return Parsed task number.
     * @throws PotatoBotException If the argument is not a whole number.
     */
    private static int parseTaskNumber(String argument) throws PotatoBotException {
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new PotatoBotException(
                    "Do you hear yourself?? Task number must be a whole number.");
        }
    }

    /**
     * Parses the description and date of a deadline command.
     *
     * @param argument Deadline command argument.
     * @return Deadline represented by the argument.
     * @throws PotatoBotException If the deadline details are invalid.
     */
    private static Deadline parseDeadline(String argument) throws PotatoBotException {
        String[] deadlineDetails = argument.split(" /by ", 2);
        if (deadlineDetails.length < 2) {
            throw new PotatoBotException(INVALID_COMMAND_MESSAGE);
        }
        return new Deadline(deadlineDetails[0], deadlineDetails[1]);
    }

    /**
     * Parses the description and dates of an event command.
     *
     * @param argument Event command argument.
     * @return Event represented by the argument.
     * @throws PotatoBotException If the event details are invalid.
     */
    private static Event parseEvent(String argument) throws PotatoBotException {
        String[] eventDetails = argument.split(" /from ", 2);
        if (eventDetails.length < 2) {
            throw new PotatoBotException(INVALID_COMMAND_MESSAGE);
        }

        String[] eventTimes = eventDetails[1].split(" /to ", 2);
        if (eventTimes.length < 2) {
            throw new PotatoBotException(INVALID_COMMAND_MESSAGE);
        }
        return new Event(eventDetails[0], eventTimes[0], eventTimes[1]);
    }
}
