package nova;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.CommandType;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.HelpCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.OnCommand;
import nova.command.SortCommand;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Todo;

/**
 * Turns a line of user input into the {@link Command} it describes.
 *
 * <p>This is where all the string handling lives: splitting off the keyword,
 * finding the {@code /by}, {@code /from} and {@code /to} markers, and reading
 * task numbers. Everything downstream works with typed objects instead.
 */
public class Parser {

    /** Utility class: not meant to be instantiated. */
    private Parser() {
    }

    /**
     * Parses a full line of user input.
     *
     * @param input the line the user typed
     * @return the command it describes, ready to execute
     * @throws NovaException if the line is not a command the chatbot understands,
     *                       or if the command is missing required parts
     */
    public static Command parse(String input) throws NovaException {
        CommandType type = CommandType.fromInput(input);
        String arguments = type.argumentsIn(input);

        switch (type) {
            case BYE:
                return new ExitCommand();

            case LIST:
                return new ListCommand();

            case MARK:
                return new MarkCommand(parseIndex(arguments, type), true);

            case UNMARK:
                return new MarkCommand(parseIndex(arguments, type), false);

            case DELETE:
                return new DeleteCommand(parseIndex(arguments, type));

            case TODO:
                if (arguments.isEmpty()) {
                    throw new NovaException("A todo needs a description. Try: todo read book");
                }
                return new AddCommand(new Todo(arguments));

            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));

            case EVENT:
                return new AddCommand(parseEvent(arguments));

            case FIND:
                if (arguments.isEmpty()) {
                    throw new NovaException("find needs a keyword. Try: find book");
                }
                return new FindCommand(arguments);

            case SORT:
                if (!arguments.isEmpty()) {
                    throw new NovaException("sort takes no arguments. Just type: sort");
                }
                return new SortCommand();

            case ON:
                if (arguments.isEmpty()) {
                    throw new NovaException("on needs a date. Try: on 2019-12-02");
                }
                return new OnCommand(DateTimes.parseDate(arguments));

            case HELP:
                return new HelpCommand();

            default:
                throw new NovaException("I don't know that command. Type help to see what I understand.");
        }
    }

    /** Reads the task number that follows a mark, unmark or delete keyword. */
    private static int parseIndex(String arguments, CommandType type) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException(type.getKeyword() + " needs a task number. Try: "
                    + type.getKeyword() + " 1");
        }
        try {
            return Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new NovaException("\"" + arguments + "\" is not a number. Try: "
                    + type.getKeyword() + " 1");
        }
    }

    /** Builds a Deadline from the text after the deadline keyword. */
    private static Deadline parseDeadline(String arguments) throws NovaException {
        String[] parts = arguments.split(" /by ");
        if (parts.length < 2 || parts[0].isEmpty()) {
            throw new NovaException("A deadline needs a description and a /by date. "
                    + "Try: deadline return book /by 2019-12-02");
        }
        return new Deadline(parts[0], DateTimes.parse(parts[1]));
    }

    /** Builds an Event from the text after the event keyword. */
    private static Event parseEvent(String arguments) throws NovaException {
        String[] parts = arguments.split(" /from | /to ");
        if (parts.length < 3 || parts[0].isEmpty()) {
            throw new NovaException("An event needs a description, a /from and a /to. "
                    + "Try: event trip /from 2019-08-06 /to 2019-08-08");
        }
        return new Event(parts[0], DateTimes.parse(parts[1]), DateTimes.parse(parts[2]));
    }
}
