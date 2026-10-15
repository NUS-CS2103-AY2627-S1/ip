package xiaozhi.parser;

import java.time.LocalDate;

import xiaozhi.command.AddCommand;
import xiaozhi.command.Command;
import xiaozhi.command.DeleteCommand;
import xiaozhi.command.ExitCommand;
import xiaozhi.command.FindCommand;
import xiaozhi.command.ListCommand;
import xiaozhi.command.MarkCommand;
import xiaozhi.command.UndoCommand;
import xiaozhi.command.UnmarkCommand;
import xiaozhi.exception.XiaoZhiException;
import xiaozhi.task.Deadline;
import xiaozhi.task.Event;
import xiaozhi.task.Todo;
import xiaozhi.util.Dates;

/**
 * Turns one line of raw user input into a {@link Command}.
 * <p>
 * Only checks that a command is syntactically well-formed (e.g. that a
 * deadline has a {@code /by} date, or that a task number is a number at
 * all); whether that task number actually exists is left to the resulting
 * command's own {@code execute()}, since only it has access to the task list.
 * <p>
 * The character(s) that separate the command word from the rest of the line
 * (and any run of extra spaces a user might type there) are collapsed down
 * to a single delimiter before anything else is parsed, so {@code "todo   read
 * book"} and {@code "todo read book"} are treated identically; the same is
 * true of accidental leading whitespace before the command word itself, e.g.
 * {@code "  todo read book"}. Whitespace inside a command's own arguments
 * (a description, a {@code /by} date) is left untouched beyond a final trim,
 * since it is free text the user typed on purpose.
 */
public class Parser {
    // The set of command words XiaoZhi understands
    private enum CommandWord {
        LIST, MARK, UNMARK, DELETE, FIND, TODO, DEADLINE, EVENT, UNDO, BYE, UNKNOWN
    }

    private Parser() {
    }

    /**
     * Parses one full line of user input into the {@link Command} it represents.
     * <p>
     * Dispatches to a per-command-word helper that knows that command's own
     * syntax; this method itself only identifies which command word was used.
     *
     * @param fullCommand The raw line of input the user typed.
     * @return The command the input represents.
     * @throws XiaoZhiException If the input is blank, is not a recognised command, or is
     *         missing a part a recognised command requires (e.g. a task number,
     *         or a deadline's {@code /by} date).
     */
    public static Command parse(String fullCommand) throws XiaoZhiException {
        assert fullCommand != null
                : "Input line should never be null; it comes from Scanner.nextLine() or a GUI text "
                + "field, neither of which returns null here.";
        // Drop leading whitespace so " todo read book" isn't mistaken for an
        // unknown command word (an empty string before the first real space).
        String withoutLeadingSpace = fullCommand.stripLeading();
        if (withoutLeadingSpace.isBlank()) {
            throw new XiaoZhiException("Please enter a command.");
        }
        // Split on the FIRST run of whitespace only, so "todo   read book"
        // collapses that run to nothing while any later spacing (inside the
        // description, or around a /by /from /to marker) is left for the
        // per-command helpers below to handle in their own way.
        String[] firstSplit = withoutLeadingSpace.split("\\s+", 2);
        String commandWord = firstSplit[0];
        String rest = firstSplit.length > 1 ? firstSplit[1] : "";

        CommandWord commandType;
        try {
            commandType = CommandWord.valueOf(commandWord.toUpperCase());
        } catch (IllegalArgumentException e) {
            commandType = CommandWord.UNKNOWN;
        }

        return switch (commandType) {
            case LIST -> new ListCommand();
            case MARK -> new MarkCommand(parseTaskIndex(rest, "mark"));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(rest, "unmark"));
            case DELETE -> new DeleteCommand(parseTaskIndex(rest, "delete"));
            case FIND -> new FindCommand(parseFindKeyword(rest));
            case TODO -> new AddCommand(parseTodo(rest));
            case DEADLINE -> new AddCommand(parseDeadline(rest));
            case EVENT -> new AddCommand(parseEvent(rest));
            case UNDO -> new UndoCommand();
            case BYE -> new ExitCommand();
            default -> throw new XiaoZhiException("I have not learned the word \"" + commandWord + "\".");
        };
    }

    /**
     * Parses the task number out of a {@code mark}/{@code unmark}/{@code delete}
     * command, shared since all three take nothing but a task number.
     *
     * @param rest Everything on the line after the command word.
     * @param action Name of the action, used to phrase the missing-number error, e.g. {@code "mark"}.
     * @return The 0-based index of the target task.
     * @throws XiaoZhiException If no task number was given, more than one token was given,
     *         or the token given isn't a number.
     */
    private static int parseTaskIndex(String rest, String action) throws XiaoZhiException {
        String[] tokens = rest.isBlank() ? new String[0] : rest.trim().split("\\s+");
        if (tokens.length < 1) {
            throw new XiaoZhiException("Please specify which task number to " + action + ".");
        }
        if (tokens.length > 1) {
            throw new XiaoZhiException(
                    "Please give exactly one task number to " + action + " -- got " + tokens.length + ".");
        }
        try {
            return Integer.parseInt(tokens[0]) - 1; // convert to 0-based index
        } catch (NumberFormatException e) {
            throw new XiaoZhiException("\"" + tokens[0] + "\" isn't a valid task number.");
        }
    }

    /**
     * Parses the keyword out of a {@code find} command.
     *
     * @param rest Everything on the line after the command word.
     * @return The keyword to search task descriptions for.
     * @throws XiaoZhiException If no keyword was given.
     */
    private static String parseFindKeyword(String rest) throws XiaoZhiException {
        String keyword = rest.trim();
        if (keyword.isBlank()) {
            throw new XiaoZhiException("Please specify a keyword to search for.");
        }
        return keyword;
    }

    /**
     * Parses a {@code todo} command into the {@link Todo} it describes.
     *
     * @param rest Everything on the line after the command word.
     * @return The todo the input describes.
     * @throws XiaoZhiException If the todo has no description, or its description contains
     *         the {@code |} character used internally to save tasks to disk.
     */
    private static Todo parseTodo(String rest) throws XiaoZhiException {
        String description = rest.trim();
        if (description.isBlank()) {
            throw new XiaoZhiException("The description of a todo cannot be empty.");
        }
        requireNoSaveDelimiter(description);
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline} command into the {@link Deadline} it describes.
     *
     * @param rest Everything on the line after the command word.
     * @return The deadline the input describes.
     * @throws XiaoZhiException If the deadline is missing its description or its {@code /by}
     *         date, has more than one {@code /by} marker, or its description contains the
     *         {@code |} character used internally to save tasks to disk.
     */
    private static Deadline parseDeadline(String rest) throws XiaoZhiException {
        if (rest.isBlank()) {
            throw new XiaoZhiException("The description of a deadline cannot be empty.");
        }
        String padded = " " + rest;
        if (!padded.contains(" /by ")) {
            throw new XiaoZhiException(
                    "A deadline needs a /by date. Try: deadline <description> /by <date>");
        }
        String[] parts = padded.split(" /by ", 2);
        String description = parts[0].trim();
        if (description.isBlank()) {
            throw new XiaoZhiException("The description of a deadline cannot be empty.");
        }
        requireNoSaveDelimiter(description);
        String byText = parts[1];
        if (byText.contains(" /by ")) {
            throw new XiaoZhiException("A deadline can only have one /by date.");
        }
        if (byText.isBlank()) {
            throw new XiaoZhiException("The /by date of a deadline cannot be empty.");
        }
        return new Deadline(description, Dates.parse(byText.trim()));
    }

    /**
     * Parses an {@code event} command into the {@link Event} it describes.
     *
     * @param rest Everything on the line after the command word.
     * @return The event the input describes.
     * @throws XiaoZhiException If the event is missing its description, its {@code /from} time,
     *         or its {@code /to} time; has more than one {@code /from} or {@code /to} marker;
     *         its {@code /from} date is later than its {@code /to} date; or its description
     *         contains the {@code |} character used internally to save tasks to disk.
     */
    private static Event parseEvent(String rest) throws XiaoZhiException {
        if (rest.isBlank()) {
            throw new XiaoZhiException("The description of an event cannot be empty.");
        }
        String padded = " " + rest;
        if (!padded.contains(" /from ")) {
            throw new XiaoZhiException(
                    "An event needs a /from time. Try: event <description> /from <start> /to <end>");
        }
        String[] fromParts = padded.split(" /from ", 2);
        String description = fromParts[0].trim();
        if (description.isBlank()) {
            throw new XiaoZhiException("The description of an event cannot be empty.");
        }
        requireNoSaveDelimiter(description);
        String afterFrom = " " + fromParts[1];
        if (afterFrom.contains(" /from ")) {
            throw new XiaoZhiException("An event can only have one /from time.");
        }
        if (!afterFrom.contains(" /to ")) {
            throw new XiaoZhiException(
                    "An event needs a /to time. Try: event <description> /from <start> /to <end>");
        }
        String[] toParts = afterFrom.split(" /to ", 2);
        String fromText = toParts[0];
        if (fromText.isBlank()) {
            throw new XiaoZhiException("The /from time of an event cannot be empty.");
        }
        String toText = toParts[1];
        if (toText.contains(" /to ")) {
            throw new XiaoZhiException("An event can only have one /to time.");
        }
        if (toText.isBlank()) {
            throw new XiaoZhiException("The /to time of an event cannot be empty.");
        }
        LocalDate fromDate = Dates.parse(fromText.trim());
        LocalDate toDate = Dates.parse(toText.trim());
        if (fromDate.isAfter(toDate)) {
            throw new XiaoZhiException(
                    "An event's /from date cannot be later than its /to date.");
        }
        return new Event(description, fromDate, toDate);
    }

    /**
     * Rejects a task description that contains the {@code |} character.
     * <p>
     * Tasks are saved to disk as {@code type | isDone | description | ...}
     * (see {@link xiaozhi.storage.Storage}), so a description containing that
     * exact delimiter would corrupt the save file and be silently truncated
     * when read back, rather than failing loudly at the point the bad input
     * was actually typed.
     *
     * @param description The description to check.
     * @throws XiaoZhiException If the description contains a {@code |} character.
     */
    private static void requireNoSaveDelimiter(String description) throws XiaoZhiException {
        if (description.contains("|")) {
            throw new XiaoZhiException(
                    "A task's description cannot contain the '|' character, "
                    + "since it is used internally to save your tasks to disk.");
        }
    }
}
