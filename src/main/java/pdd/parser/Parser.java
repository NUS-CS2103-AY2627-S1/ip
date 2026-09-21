package pdd.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import pdd.PDDException;
import pdd.command.Command;
import pdd.command.DeadlineCommand;
import pdd.command.DeleteCommand;
import pdd.command.EventCommand;
import pdd.command.ExitCommand;
import pdd.command.FindCommand;
import pdd.command.ListCommand;
import pdd.command.MarkCommand;
import pdd.command.OnCommand;
import pdd.command.SortCommand;
import pdd.command.TodoCommand;
import pdd.command.UnmarkCommand;
import pdd.task.Deadline;
import pdd.task.Event;
import pdd.task.Todo;

/**
 * Makes sense of raw user input: splitting a command line into its
 * command word and arguments, and turning the arguments for each
 * command type into validated data (a parsed date, a new Task, etc),
 * throwing {@link PDDException} with a user-facing message when the
 * input doesn't fit what that command expects.
 */
public class Parser {
    private static final String BY_DELIMITER = " /by ";
    private static final String FROM_DELIMITER = " /from ";
    private static final String TO_DELIMITER = " /to ";

    /** The kind of command a user input line names. */
    private enum CommandType { LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, ON, FIND, SORT, BYE }

    /**
     * Parses one full line of user input into a ready-to-run {@link Command}.
     * Throws {@link PDDException} (with a user-facing message) if the input
     * isn't a recognized command or its arguments are invalid.
     */
    public static Command parse(String fullCommand) throws PDDException {
        String commandWord = getCommandWord(fullCommand);
        String commandArgs = getCommandArgs(fullCommand);
        switch (parseCommandType(commandWord)) {
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(commandArgs);
            case UNMARK:
                return new UnmarkCommand(commandArgs);
            case DELETE:
                return new DeleteCommand(commandArgs);
            case TODO:
                return new TodoCommand(parseTodo(commandArgs));
            case DEADLINE:
                return new DeadlineCommand(parseDeadline(commandArgs));
            case EVENT:
                return new EventCommand(parseEvent(commandArgs));
            case ON:
                return new OnCommand(parseOnDate(commandArgs));
            case FIND:
                return new FindCommand(parseFindKeyword(commandArgs));
            case SORT:
                return new SortCommand();
            case BYE:
                return new ExitCommand();
            default:
                // Unreachable: parseCommandType() only ever returns a CommandType constant,
                // and every constant is handled by a case above. This differs from the
                // PDDException thrown inside parseCommandType() for genuine user typos.
                assert false : "Unhandled CommandType: every enum constant has a case above";
                throw new PDDException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /**
     * Returns the command word (before the first space), e.g. "deadline"
     * from "deadline return book /by ...".
     */
    private static String getCommandWord(String fullCommand) {
        int spaceIndex = fullCommand.indexOf(' ');
        return spaceIndex == -1 ? fullCommand : fullCommand.substring(0, spaceIndex);
    }

    /** Returns the trimmed text after the command word, or "" if there is none. */
    private static String getCommandArgs(String fullCommand) {
        int spaceIndex = fullCommand.indexOf(' ');
        return spaceIndex == -1 ? "" : fullCommand.substring(spaceIndex + 1).trim();
    }

    /** Resolves a command word to a {@link CommandType}, throwing if it isn't recognized. */
    private static CommandType parseCommandType(String commandWord) throws PDDException {
        try {
            return CommandType.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new PDDException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }

    /** Parses the arguments of a {@code todo} command into a {@link Todo}. */
    private static Todo parseTodo(String commandArgs) throws PDDException {
        if (commandArgs.isEmpty()) {
            throw new PDDException("OOPS!!! The description of a todo cannot be empty.");
        }
        return new Todo(commandArgs);
    }

    /** Parses the arguments of a {@code deadline} command into a {@link Deadline}. */
    private static Deadline parseDeadline(String commandArgs) throws PDDException {
        if (commandArgs.isEmpty()) {
            throw new PDDException("OOPS!!! The description of a deadline cannot be empty.");
        }
        String[] parts = splitIntoTwoNonEmptyParts(commandArgs, BY_DELIMITER,
                "OOPS!!! A deadline needs a description and a '/by' "
                        + "date, e.g. deadline return book /by 2019-10-15");
        LocalDate by = parseDate(parts[1]);
        return new Deadline(parts[0], by);
    }

    /** Parses the arguments of an {@code event} command into an {@link Event}. */
    private static Event parseEvent(String commandArgs) throws PDDException {
        if (commandArgs.isEmpty()) {
            throw new PDDException("OOPS!!! The description of an event cannot be empty.");
        }
        String eventUsage = "OOPS!!! An event needs a description, a '/from' date and a "
                + "'/to' time, e.g. event meeting /from 2019-10-15 /to 4pm";
        String[] parts = splitIntoTwoNonEmptyParts(commandArgs, FROM_DELIMITER, eventUsage);
        String[] fromTo = splitIntoTwoNonEmptyParts(parts[1], TO_DELIMITER, eventUsage);
        LocalDate from = parseDate(fromTo[0]);
        return new Event(parts[0], from, fromTo[1]);
    }

    /**
     * Splits {@code text} on the first occurrence of {@code delimiter} into exactly two
     * trimmed, non-empty parts, throwing a {@link PDDException} with {@code usageMessage}
     * if the delimiter is missing or either side is empty.
     */
    private static String[] splitIntoTwoNonEmptyParts(String text, String delimiter,
            String usageMessage) throws PDDException {
        String[] parts = text.split(delimiter, 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new PDDException(usageMessage);
        }
        return new String[] { parts[0].trim(), parts[1].trim() };
    }

    /** Parses the arguments of an {@code on} command into the date to filter tasks by. */
    private static LocalDate parseOnDate(String commandArgs) throws PDDException {
        if (commandArgs.isEmpty()) {
            throw new PDDException("OOPS!!! Please provide a date, e.g. on 2019-10-15");
        }
        return parseDate(commandArgs);
    }

    /** Parses the arguments of a {@code find} command into the keyword to search for. */
    private static String parseFindKeyword(String commandArgs) throws PDDException {
        if (commandArgs.isEmpty()) {
            throw new PDDException("OOPS!!! Please provide a keyword to search for, e.g. find book");
        }
        return commandArgs;
    }

    private static LocalDate parseDate(String text) throws PDDException {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw new PDDException("OOPS!!! Please enter the date in yyyy-MM-dd format, e.g. 2019-10-15.");
        }
    }
}
