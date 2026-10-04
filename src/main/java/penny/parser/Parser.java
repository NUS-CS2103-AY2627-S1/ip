package penny.parser;

import java.time.LocalDate;

import penny.command.AddCommand;
import penny.command.Command;
import penny.command.DeleteCommand;
import penny.command.ExitCommand;
import penny.command.FindCommand;
import penny.command.ListCommand;
import penny.command.MarkCommand;
import penny.command.OnCommand;
import penny.command.UnmarkCommand;
import penny.common.PennyException;
import penny.common.Time;
import penny.task.Deadline;
import penny.task.Event;
import penny.task.Todo;

/**
 * Deals with making sense of user commands.
 * Parses raw text input into executable Command objects.
 */
public class Parser {

    /**
     * Parses the full user command string and returns the corresponding executable Command.
     *
     * @param fullCommand The raw line of text entered by the user.
     * @return The executable Command object.
     * @throws PennyException If the command word is unknown or the arguments are malformed.
     */
    public static Command parse(String fullCommand) throws PennyException {
        assert fullCommand != null : "Command string cannot be null";
        String trimmed = fullCommand.trim();
        String[] parts = trimmed.split(" ", 2);
        String commandWord = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        Command command;
        switch (commandWord) {
            case "bye":
                command = new ExitCommand();
                break;
            case "list":
                command = new ListCommand();
                break;
            case "mark":
                command = new MarkCommand(parseTaskIndex(fullCommand, "mark"));
                break;
            case "unmark":
                command = new UnmarkCommand(parseTaskIndex(fullCommand, "unmark"));
                break;
            case "delete":
                command = new DeleteCommand(parseTaskIndex(fullCommand, "delete"));
                break;
            case "todo":
                command = new AddCommand(parseTodo(args));
                break;
            case "deadline":
                command = new AddCommand(parseDeadline(args));
                break;
            case "event":
                command = new AddCommand(parseEvent(args));
                break;
            case "on":
                command = new OnCommand(parseDateQuery(args));
                break;
            case "find":
                command = new FindCommand(parseFindQuery(args));
                break;
            default:
                throw new PennyException("Hmm, I don't quite understand that command. "
                        + "Valid commands: todo, deadline, event, list, mark, unmark, delete, on, find, bye.");
        }

        assert command != null : "Parsed command must not be null";
        return command;
    }

    /**
     * Parses a 0-based task index from commands like "mark 2", "unmark 1", "delete 3".
     *
     * @param fullCommand The raw command string.
     * @param keyword The command keyword (e.g., "mark", "unmark", "delete").
     * @return The 0-based index of the target task.
     * @throws PennyException If the task number is missing or non-numeric.
     */
    public static int parseTaskIndex(String fullCommand, String keyword) throws PennyException {
        assert fullCommand != null : "fullCommand cannot be null";
        assert keyword != null : "keyword cannot be null";
        assert fullCommand.trim().toLowerCase().startsWith(keyword) : "fullCommand must start with keyword";

        String argument = fullCommand.substring(keyword.length()).trim();
        if (argument.isEmpty()) {
            throw new PennyException("Please specify a task number. Try: " + keyword + " 1");
        }
        try {
            int index = Integer.parseInt(argument) - 1;
            if (index < 0) {
                throw new PennyException("Invalid task number: '" + argument
                        + "'. Please enter a positive integer.");
            }
            assert index >= 0 : "Parsed 0-based task index must be non-negative";
            return index;
        } catch (NumberFormatException e) {
            throw new PennyException("Invalid task number: '" + argument
                    + "'. Please enter a positive integer.");
        }
    }

    /**
     * Parses arguments for a todo command and constructs a Todo task.
     *
     * @param args The argument string after "todo".
     * @return A new Todo task.
     * @throws PennyException If description is empty or contains reserved delimiter '|'.
     */
    public static Todo parseTodo(String args) throws PennyException {
        assert args != null : "args cannot be null";
        if (args.isEmpty()) {
            throw new PennyException("Whoops! A todo needs a description. Try: todo read a book");
        }
        validateNoReservedDelimiter("Task description", args);
        Todo todo = new Todo(args);
        assert !todo.getDescription().isEmpty() : "Todo description must not be empty";
        return todo;
    }

    /**
     * Parses arguments for a deadline command and constructs a Deadline task.
     *
     * @param args The argument string after "deadline".
     * @return A new Deadline task.
     * @throws PennyException If description or deadline time is missing or invalid.
     */
    public static Deadline parseDeadline(String args) throws PennyException {
        assert args != null : "args cannot be null";
        if (args.isEmpty()) {
            throw new PennyException("Whoops! A deadline needs a description. "
                    + "Try: deadline return book /by 2019-10-15");
        }
        if (!args.contains("/by")) {
            throw new PennyException("Wait, a deadline needs a time limit. "
                    + "Try: deadline return book /by 2019-10-15");
        }
        String[] parts = args.split("/by", 2);
        String description = parts[0].trim();
        String deadlineTime = parts[1].trim();

        validateDeadlineArguments(description, deadlineTime);
        validateNoReservedDelimiter("Task description and deadline", description, deadlineTime);
        Deadline deadline = new Deadline(description, deadlineTime);
        assert !deadline.getDescription().isEmpty() : "Deadline description must not be empty";
        assert deadline.getBy() != null : "Deadline by time must not be null";
        return deadline;
    }

    private static void validateDeadlineArguments(String description, String deadlineTime)
            throws PennyException {
        if (description.isEmpty() && deadlineTime.isEmpty()) {
            throw new PennyException("Wait, a deadline needs both a description and a time limit. "
                    + "Try: deadline return book /by 2019-10-15");
        } else if (description.isEmpty()) {
            throw new PennyException("Whoops! A deadline needs a description before /by. "
                    + "Try: deadline return book /by 2019-10-15");
        } else if (deadlineTime.isEmpty()) {
            throw new PennyException("Wait, a deadline needs a time limit after /by. "
                    + "Try: deadline return book /by 2019-10-15");
        }
    }

    /**
     * Parses arguments for an event command and constructs an Event task.
     *
     * @param args The argument string after "event".
     * @return A new Event task.
     * @throws PennyException If description, start time, or end time is missing or invalid.
     */
    public static Event parseEvent(String args) throws PennyException {
        assert args != null : "args cannot be null";
        if (args.isEmpty()) {
            throw new PennyException("Whoops! An event needs a description. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        }
        String[] parts = extractEventParts(args);
        String description = parts[0];
        String startTime = parts[1];
        String endTime = parts[2];

        validateEventArguments(description, startTime, endTime);
        validateNoReservedDelimiter("Task description and event times", description, startTime, endTime);
        Event event = new Event(description, startTime, endTime);
        assert !event.getDescription().isEmpty() : "Event description must not be empty";
        assert event.getFrom() != null && event.getTo() != null
                : "Event start and end times must not be null";
        return event;
    }

    private static String[] extractEventParts(String args) throws PennyException {
        if (!args.contains("/from") || !args.contains("/to")) {
            throw new PennyException("An event needs a start and end time. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        }
        String[] fromParts = args.split("/from", 2);
        if (!fromParts[1].contains("/to")) {
            throw new PennyException("An event needs a start and end time. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        }
        String[] toParts = fromParts[1].split("/to", 2);
        return new String[] { fromParts[0].trim(), toParts[0].trim(), toParts[1].trim() };
    }

    private static void validateEventArguments(String description, String startTime, String endTime)
            throws PennyException {
        if (description.isEmpty() && startTime.isEmpty() && endTime.isEmpty()) {
            throw new PennyException("An event is missing details. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        } else if (description.isEmpty()) {
            throw new PennyException("Whoops! An event needs a description before /from. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        } else if (startTime.isEmpty()) {
            throw new PennyException("Wait, an event needs a start time after /from. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        } else if (endTime.isEmpty()) {
            throw new PennyException("Wait, an event needs an end time after /to. "
                    + "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600");
        }
    }

    /**
     * Parses the date argument for the 'on' date search command.
     *
     * @param args The date argument string.
     * @return The parsed LocalDate.
     * @throws PennyException If date argument is empty or invalid.
     */
    public static LocalDate parseDateQuery(String args) throws PennyException {
        assert args != null : "args cannot be null";
        if (args.isEmpty()) {
            throw new PennyException("Please specify a date to search for. Try: on 2019-10-15 or on 2/12/2019");
        }
        LocalDate date = Time.parseDate(args);
        assert date != null : "Parsed date must not be null";
        return date;
    }

    /**
     * Parses the search keyword argument for the 'find' command.
     *
     * @param args The search keyword string.
     * @return The validated search keyword.
     * @throws PennyException If the keyword is empty or contains reserved characters.
     */
    public static String parseFindQuery(String args) throws PennyException {
        assert args != null : "args cannot be null";
        if (args.isEmpty()) {
            throw new PennyException("Please specify a keyword to search for. Try: find book");
        }
        validateNoReservedDelimiter("Search keyword", args);
        assert !args.isEmpty() : "Search keyword must not be empty";
        return args;
    }

    private static void validateNoReservedDelimiter(String fieldName, String... values)
            throws PennyException {
        for (String value : values) {
            if (value.contains("|")) {
                throw new PennyException(fieldName + " cannot contain the '|' character "
                        + "as it is reserved for data storage.");
            }
        }
    }
}
