package clearblue.parser;

import java.time.LocalDate;

import clearblue.ClearblueException;
import clearblue.command.AddCommand;
import clearblue.command.Command;
import clearblue.command.DeleteCommand;
import clearblue.command.ExitCommand;
import clearblue.command.FindCommand;
import clearblue.command.ListCommand;
import clearblue.command.MarkCommand;
import clearblue.command.OnCommand;
import clearblue.command.UndoCommand;
import clearblue.storage.Storage;
import clearblue.task.Deadline;
import clearblue.task.Event;
import clearblue.task.TaskDateTime;
import clearblue.task.Todo;

/**
 * Makes sense of a raw command line: turns it into a ready-to-run
 * {@link Command}, or reports why the input isn't a valid command.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE = "I don't recognize that command. "
            + "Try todo, deadline, event, list, mark, unmark, delete, on, find, undo, or bye.";

    /**
     * Parses one command line into a {@link Command}.
     *
     * @param fullCommand complete command entered by the user
     * @return the command to execute
     * @throws ClearblueException if the command is empty, unrecognized, or malformed
     */
    public static Command parse(String fullCommand) throws ClearblueException {
        // Ui.readCommand() already trims console input, but Clearblue.getResponse()
        // passes the GUI text field's value straight through untrimmed. Trimming
        // here (rather than relying on each caller to do it) keeps the invariant
        // that parseArguments() below depends on true for both entry points.
        String trimmedCommand = fullCommand.trim();

        CommandType commandType = CommandType.fromCommand(trimmedCommand);
        String arguments = parseArguments(trimmedCommand, commandType);

        if (commandType == CommandType.BYE && arguments.isEmpty()) {
            return new ExitCommand();
        }

        return switch (commandType) {
            case LIST -> parseList(arguments);
            case MARK -> parseMarkOrUnmark(arguments, true);
            case UNMARK -> parseMarkOrUnmark(arguments, false);
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments, "delete"));
            case ON -> parseOn(arguments);
            case FIND -> parseFind(arguments);
            case TODO -> parseTodo(arguments);
            case DEADLINE -> parseDeadline(arguments);
            case EVENT -> parseEvent(arguments);
            case UNDO -> parseUndo(arguments);
            case BYE, UNKNOWN -> throw new ClearblueException(
                    trimmedCommand.isEmpty() ? "Please enter a command." : UNKNOWN_COMMAND_MESSAGE);
        };
    }

    /**
     * Returns the part of a command that follows its command word.
     *
     * @param command complete command entered by the user
     * @param commandType parsed command type
     * @return trimmed command arguments, or the original input for an unknown command
     */
    private static String parseArguments(String command, CommandType commandType) {
        // command must already be trimmed: the substring below cuts off exactly
        // commandType.getCommandWord().length() characters from the front, which
        // only lines up with the command word if there is no leading whitespace.
        // A leading space here would silently shift the extracted arguments by
        // one character instead of throwing, so this is worth asserting rather
        // than leaving as an unstated assumption.
        assert command.equals(command.trim()) : "command must be pre-trimmed by the caller";

        if (commandType == CommandType.UNKNOWN) {
            return command;
        }
        return command.substring(commandType.getCommandWord().length()).trim();
    }

    /**
     * Parses the arguments for a {@code list} command.
     *
     * @param arguments text following the {@code list} command word
     * @return a {@link ListCommand}
     * @throws ClearblueException if extra arguments were given
     */
    private static Command parseList(String arguments) throws ClearblueException {
        if (!arguments.isEmpty()) {
            throw new ClearblueException(UNKNOWN_COMMAND_MESSAGE);
        }
        return new ListCommand();
    }

    /**
     * Parses the arguments for a {@code mark} or {@code unmark} command.
     *
     * @param arguments text following the command word
     * @param isDone {@code true} for {@code mark}, {@code false} for {@code unmark}
     * @return a {@link MarkCommand} for the parsed task number
     * @throws ClearblueException if the task number is missing or not a whole number
     */
    private static Command parseMarkOrUnmark(String arguments, boolean isDone) throws ClearblueException {
        String action = isDone ? "mark" : "unmark";
        return new MarkCommand(parseTaskNumber(arguments, action), isDone);
    }

    /**
     * Parses a one-based task number from raw argument text.
     *
     * @param arguments text expected to hold just a task number
     * @param action operation that will use the number, for the error message
     * @return the parsed task number
     * @throws ClearblueException if the text is missing or not a whole number
     */
    private static int parseTaskNumber(String arguments, String action) throws ClearblueException {
        if (arguments.isEmpty()) {
            throw new ClearblueException("Tell me which task to " + action + ". Example: " + action + " 1");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException exception) {
            throw new ClearblueException("The task number must be a whole number. Example: " + action + " 1");
        }
    }

    /**
     * Parses the arguments for an {@code on} command.
     *
     * @param arguments text following the {@code on} command word
     * @return an {@link OnCommand} for the parsed date
     * @throws ClearblueException if the date is missing or not in {@code yyyy-MM-dd} format
     */
    private static Command parseOn(String arguments) throws ClearblueException {
        if (arguments.isEmpty()) {
            throw new ClearblueException("Tell me which date to check. Example: on 2019-06-06");
        }
        LocalDate queryDate = TaskDateTime.parseDate(arguments);
        if (queryDate == null) {
            throw new ClearblueException("The date must be in yyyy-MM-dd format. Example: on 2019-06-06");
        }
        return new OnCommand(queryDate);
    }

    private static Command parseFind(String arguments) throws ClearblueException {
        if (arguments.isEmpty()) {
            throw new ClearblueException("Tell me what to search for. Example: find book");
        }
        return new FindCommand(arguments);
    }

    /**
     * Parses the arguments for a {@code todo} command.
     *
     * @param arguments text following the {@code todo} command word
     * @return an {@link AddCommand} wrapping the new {@code Todo}
     * @throws ClearblueException if the description is empty
     */
    private static Command parseTodo(String arguments) throws ClearblueException {
        if (arguments.isEmpty()) {
            throw new ClearblueException("A todo needs a description after \"todo\".");
        }
        requireSafeForStorage(arguments, "description");
        return new AddCommand(new Todo(arguments));
    }

    /**
     * Parses the arguments for a {@code deadline} command.
     *
     * @param arguments text following the {@code deadline} command word
     * @return an {@link AddCommand} wrapping the new {@code Deadline}
     * @throws ClearblueException if the {@code /by} separator, description, or date/time is missing
     */
    private static Command parseDeadline(String arguments) throws ClearblueException {
        int byIndex = arguments.indexOf("/by");
        if (byIndex < 0) {
            throw new ClearblueException("A deadline needs a /by separator. "
                    + "Example: deadline return book /by Sunday");
        }
        if (arguments.indexOf("/by", byIndex + "/by".length()) >= 0) {
            throw new ClearblueException("A deadline can only have one /by.");
        }

        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();

        if (description.isEmpty()) {
            throw new ClearblueException("A deadline needs a description before /by.");
        }
        if (by.isEmpty()) {
            throw new ClearblueException("A deadline needs a date or time after /by.");
        }
        requireSafeForStorage(description, "description");
        requireSafeForStorage(by, "/by date or time");
        return new AddCommand(new Deadline(description, by));
    }

    /**
     * Parses the arguments for an {@code event} command.
     *
     * @param arguments text following the {@code event} command word
     * @return an {@link AddCommand} wrapping the new {@code Event}
     * @throws ClearblueException if either separator, the description, or a time is missing
     */
    private static Command parseEvent(String arguments) throws ClearblueException {
        int fromIndex = arguments.indexOf("/from");
        if (fromIndex < 0) {
            throw new ClearblueException("An event needs a /from separator. "
                    + "Example: event meeting /from 2pm /to 4pm");
        }

        int toIndex = arguments.indexOf("/to", fromIndex + "/from".length());
        if (toIndex < 0) {
            throw new ClearblueException("An event needs a /to separator. "
                    + "Example: event meeting /from 2pm /to 4pm");
        }
        if (arguments.indexOf("/from", fromIndex + "/from".length()) >= 0) {
            throw new ClearblueException("An event can only have one /from.");
        }
        if (arguments.indexOf("/to", toIndex + "/to".length()) >= 0) {
            throw new ClearblueException("An event can only have one /to.");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();

        if (description.isEmpty()) {
            throw new ClearblueException("An event needs a description before /from.");
        }
        if (from.isEmpty()) {
            throw new ClearblueException("An event needs a start date or time after /from.");
        }
        if (to.isEmpty()) {
            throw new ClearblueException("An event needs an end date or time after /to.");
        }
        requireSafeForStorage(description, "description");
        requireSafeForStorage(from, "/from date or time");
        requireSafeForStorage(to, "/to date or time");

        Event event = new Event(description, from, to);
        requireChronologicalOrder(event);
        return new AddCommand(event);
    }

    /**
     * Rejects an event whose start and end are both real calendar dates but
     * not in chronological order. Free-form times (e.g. {@code 2pm}) can't
     * be compared this way, so this only applies when both sides parsed as
     * an actual {@code yyyy-MM-dd} date.
     *
     * @param event event to check
     * @throws ClearblueException if both dates are real and {@code from} is not before {@code to}
     */
    private static void requireChronologicalOrder(Event event) throws ClearblueException {
        TaskDateTime from = event.getFrom();
        TaskDateTime to = event.getTo();
        if (from.isDate() && to.isDate() && !from.getDate().isBefore(to.getDate())) {
            throw new ClearblueException("An event's start date must be before its end date.");
        }
    }

    /**
     * Parses the arguments for an {@code undo} command.
     *
     * @param arguments text following the {@code undo} command word
     * @return an {@link UndoCommand}
     * @throws ClearblueException if extra arguments were given
     */
    private static Command parseUndo(String arguments) throws ClearblueException {
        if (!arguments.isEmpty()) {
            throw new ClearblueException(UNKNOWN_COMMAND_MESSAGE);
        }
        return new UndoCommand();
    }

    /**
     * Rejects text that would corrupt the save file if stored verbatim.
     *
     * @param text field value to check, e.g. a description or date/time
     * @param fieldName name of the field, for the error message
     * @throws ClearblueException if text contains the storage field separator
     */
    private static void requireSafeForStorage(String text, String fieldName) throws ClearblueException {
        if (Storage.containsFieldSeparator(text)) {
            throw new ClearblueException(
                    "A task's " + fieldName + " can't contain \" | \", since that's used internally "
                            + "to save your data.");
        }
    }
}
