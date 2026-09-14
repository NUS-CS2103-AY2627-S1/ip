package nova;

import java.time.LocalDateTime;

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
    private static final String BY_MARKER = "/by ";
    private static final String FROM_MARKER = "/from ";
    private static final String TO_MARKER = "/to ";
    private static final String EVENT_HINT =
            "Try: event trip /from 2019-08-06 /to 2019-08-08";


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
                return new AddCommand(new Todo(normalise(arguments)));

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
        int byIndex = findSingleMarker(arguments, BY_MARKER, "A deadline needs a description and a "
                + "/by date. Try: deadline return book /by 2019-12-02");
        String description = normalise(arguments.substring(0, byIndex));
        String by = arguments.substring(byIndex + BY_MARKER.length()).trim();
        requireNotEmpty(description, "A deadline needs a description before /by. "
                + "Try: deadline return book /by 2019-12-02");
        requireNotEmpty(by, "A deadline needs a date after /by. "
                + "Try: deadline return book /by 2019-12-02");
        return new Deadline(description, DateTimes.parse(by));
    }

    /** Builds an Event from the text after the event keyword. */
    private static Event parseEvent(String arguments) throws NovaException {
        int fromIndex = findSingleMarker(arguments, FROM_MARKER, EVENT_HINT);
        int toIndex = findSingleMarker(arguments, TO_MARKER, EVENT_HINT);
        if (fromIndex > toIndex) {
            throw new NovaException("/from must come before /to. " + EVENT_HINT);
        }

        String description = normalise(arguments.substring(0, fromIndex));
        String from = arguments.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_MARKER.length()).trim();
        requireNotEmpty(description, "An event needs a description before /from. " + EVENT_HINT);
        requireNotEmpty(from, "An event needs a date after /from. " + EVENT_HINT);
        requireNotEmpty(to, "An event needs a date after /to. " + EVENT_HINT);

        LocalDateTime start = DateTimes.parse(from);
        LocalDateTime end = DateTimes.parse(to);
        if (end.isBefore(start)) {
            throw new NovaException("An event cannot end before it starts. You gave "
                    + DateTimes.format(start) + " to " + DateTimes.format(end) + ".");
        }
        return new Event(description, start, end);
    }

    /**
     * Returns where the given marker appears, insisting it appears exactly once.
     *
     * <p>Repeating a marker is rejected rather than resolved by taking the first
     * or the last, because either choice silently discards part of what the user
     * typed.
     */
    private static int findSingleMarker(String arguments, String marker, String hint)
            throws NovaException {
        int first = arguments.indexOf(marker);
        if (first < 0) {
            throw new NovaException("Missing " + marker.trim() + ". " + hint);
        }
        if (arguments.indexOf(marker, first + 1) >= 0) {
            throw new NovaException("You gave " + marker.trim() + " more than once. " + hint);
        }
        return first;
    }

    /** Rejects a value the user left blank. */
    private static void requireNotEmpty(String value, String message) throws NovaException {
        if (value.isEmpty()) {
            throw new NovaException(message);
        }
    }

    /** Collapses runs of whitespace so "read    book" is stored as "read book". */
    private static String normalise(String description) {
        return description.trim().replaceAll("\\s+", " ");
    }
}
