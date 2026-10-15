package yapbot.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import yapbot.command.AddCommand;
import yapbot.command.ByeCommand;
import yapbot.command.Command;
import yapbot.command.DeleteCommand;
import yapbot.command.FindCommand;
import yapbot.command.ListCommand;
import yapbot.command.MarkCommand;
import yapbot.command.PriorityCommand;
import yapbot.command.UnmarkCommand;
import yapbot.exception.YapBotException;
import yapbot.task.Deadline;
import yapbot.task.Event;
import yapbot.task.Priority;
import yapbot.task.Task;
import yapbot.task.Todo;

/**
 * Makes sense of user command text, turning it into a {@link Command} to
 * execute (or throwing a {@link YapBotException} if it can't be understood).
 */
public class Parser {

    private static final String COMMAND_WORD_TODO = "todo";
    private static final String COMMAND_WORD_DEADLINE = "deadline";
    private static final String COMMAND_WORD_EVENT = "event";
    private static final String FLAG_PRIORITY = " /priority";

    /**
     * The remainder of an add command's text once an optional trailing
     * {@code /priority} flag has been stripped off, paired with the
     * priority it carried (or {@code null} if it had none).
     */
    private record TextWithPriority(String text, Priority priority) {
    }

    /**
     * Parses a full line of user input into the matching command.
     *
     * @param fullCommand the full command text as typed by the user.
     * @return the command to execute.
     * @throws YapBotException if the command is empty, unrecognized, or malformed.
     */
    public static Command parse(String fullCommand) throws YapBotException {
        assert fullCommand != null : "Ui.readCommand() throws instead of ever returning null";
        String command = fullCommand.trim();
        if (command.equals("bye")) {
            return new ByeCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (isCommandWord(command, "mark")) {
            return new MarkCommand(command);
        } else if (isCommandWord(command, "unmark")) {
            return new UnmarkCommand(command);
        } else if (isCommandWord(command, "delete")) {
            return new DeleteCommand(command);
        } else if (isCommandWord(command, "priority")) {
            return new PriorityCommand(command);
        } else if (isCommandWord(command, "find")) {
            return new FindCommand(command);
        } else if (isCommandWord(command, COMMAND_WORD_TODO)) {
            return new AddCommand(parseTodo(command));
        } else if (isCommandWord(command, COMMAND_WORD_DEADLINE)) {
            return new AddCommand(parseDeadline(command));
        } else if (isCommandWord(command, COMMAND_WORD_EVENT)) {
            return new AddCommand(parseEvent(command));
        } else if (command.isEmpty()) {
            throw new YapBotException("You didn't type anything. Try 'todo', 'deadline', "
                    + "'event', 'list', 'find', 'mark', 'unmark', 'delete', 'priority' or 'bye'.");
        } else {
            throw new YapBotException(
                    "I'm sorry, but I don't know what that means. "
                            + "Try 'todo', 'deadline', 'event', 'list', 'find', 'mark', "
                            + "'unmark', 'delete', 'priority' or 'bye'.");
        }
    }

    /**
     * Returns whether {@code command} starts with {@code keyword} as a whole
     * word, i.e. the keyword is either the entire command or is immediately
     * followed by whitespace. Plain {@code startsWith} would wrongly match
     * unrelated input that merely happens to share a prefix with a command
     * word, e.g. "markdown" or "findable" would otherwise be misread as
     * "mark"/"find" commands.
     *
     * @param command the (already-trimmed) full command text.
     * @param keyword the command keyword to match, e.g. "mark".
     * @return {@code true} if {@code command} is exactly {@code keyword}, or
     *         starts with {@code keyword} followed by whitespace.
     */
    private static boolean isCommandWord(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Strips an optional trailing {@code /priority <level>} flag off the end
     * of an add command's text (after its keyword has already been
     * removed), before any type-specific splitting (e.g. on {@code /by}).
     *
     * @param text the add command's text, keyword already stripped.
     * @return the text with the flag removed (unchanged if there was none),
     *         paired with the priority it specified (or {@code null}).
     * @throws YapBotException if the flag is present but names no level, or
     *         names one that isn't {@code high}/{@code medium}/{@code low}.
     */
    private static TextWithPriority extractPriority(String text) throws YapBotException {
        int flagIndex = text.indexOf(FLAG_PRIORITY);
        if (flagIndex == -1) {
            return new TextWithPriority(text, null);
        }
        String remainder = text.substring(0, flagIndex);
        String levelInput = text.substring(flagIndex + FLAG_PRIORITY.length()).trim();
        if (levelInput.isEmpty()) {
            throw new YapBotException("Please specify a priority: high, medium or low, "
                    + "e.g. '/priority high'.");
        }
        return new TextWithPriority(remainder, Priority.fromInput(levelInput));
    }

    /**
     * Parses a "todo" command into a {@link Todo}.
     *
     * @param command the full command text.
     * @return the parsed task.
     * @throws YapBotException if the description is empty or contains '|',
     *         or the optional {@code /priority} flag is invalid.
     */
    private static Task parseTodo(String command) throws YapBotException {
        TextWithPriority extracted = extractPriority(command.substring(COMMAND_WORD_TODO.length()).trim());
        String description = extracted.text();
        if (description.isEmpty()) {
            throw new YapBotException("The description of a todo cannot be empty. "
                    + "Usage: todo <description>");
        }
        Task task = new Todo(checkNoDelimiter(description, "description of a todo"));
        task.setPriority(extracted.priority());
        return task;
    }

    /**
     * Parses a "deadline" command into a {@link Deadline}.
     *
     * @param command the full command text.
     * @return the parsed task.
     * @throws YapBotException if the description/date is missing, contains
     *         '|', the {@code /by} flag is given more than once, the date is
     *         not a valid {@code yyyy-mm-dd} date, or the optional
     *         {@code /priority} flag is invalid.
     */
    private static Task parseDeadline(String command) throws YapBotException {
        String rawDetails = command.length() > COMMAND_WORD_DEADLINE.length()
                ? command.substring(COMMAND_WORD_DEADLINE.length()).trim()
                : "";
        TextWithPriority extracted = extractPriority(rawDetails);
        String details = extracted.text();
        if (details.isEmpty()) {
            throw new YapBotException("The description of a deadline cannot be empty. "
                    + "Usage: deadline <description> /by <date>");
        }
        // No limit on split, so a duplicate '/by' yields 3+ parts instead of
        // silently folding the second occurrence into the date field.
        String[] parts = details.split(" /by ", -1);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new YapBotException("A deadline needs both a description and a '/by' date. "
                    + "Usage: deadline <description> /by <date>");
        }
        if (parts.length > 2) {
            throw new YapBotException("A deadline can only have one '/by' date.");
        }
        String description = checkNoDelimiter(parts[0].trim(), "description of a deadline");
        String byInput = checkNoDelimiter(parts[1].trim(), "'/by' date of a deadline");
        LocalDate by = parseDeadlineDate(byInput);
        Task task = new Deadline(description, by);
        task.setPriority(extracted.priority());
        return task;
    }

    /**
     * Parses an "event" command into an {@link Event}.
     *
     * @param command the full command text.
     * @return the parsed task.
     * @throws YapBotException if the description/from/to are missing,
     *         either flag is given more than once, a field contains '|', or
     *         the optional {@code /priority} flag is invalid.
     */
    private static Task parseEvent(String command) throws YapBotException {
        String rawDetails = command.length() > COMMAND_WORD_EVENT.length()
                ? command.substring(COMMAND_WORD_EVENT.length()).trim()
                : "";
        TextWithPriority extracted = extractPriority(rawDetails);
        String details = extracted.text();
        if (details.isEmpty()) {
            throw new YapBotException("The description of an event cannot be empty. "
                    + "Usage: event <description> /from <start> /to <end>");
        }
        String[] parts = details.split(" /from | /to ", -1);
        if (parts.length < 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new YapBotException(
                    "An event needs a description, a '/from' time and a '/to' time. "
                            + "Usage: event <description> /from <start> /to <end>");
        }
        if (parts.length > 3) {
            // Without this check, a repeated '/from' or '/to' would silently discard the
            // real '/to' value instead of failing loudly (e.g. it would previously read
            // "event x /from A /from B /to C" as from="A", to="B", quietly losing "C").
            throw new YapBotException("An event can only have one '/from' time and one '/to' time.");
        }
        String description = checkNoDelimiter(parts[0].trim(), "description of an event");
        String from = checkNoDelimiter(parts[1].trim(), "'/from' time of an event");
        String to = checkNoDelimiter(parts[2].trim(), "'/to' time of an event");
        checkChronologicalOrder(from, to);
        Task task = new Event(description, from, to);
        task.setPriority(extracted.priority());
        return task;
    }

    /**
     * Checks that an event's start is strictly before its end, but only when
     * both {@code from} and {@code to} are valid {@code yyyy-mm-dd} dates.
     * An event's from/to are free text (e.g. "Mon 2pm"), so this is a
     * best-effort check: anything that isn't an unambiguous date on both
     * sides is left alone rather than rejected.
     *
     * @param from the event's start time, already validated as non-blank.
     * @param to   the event's end time, already validated as non-blank.
     * @throws YapBotException if both parse as dates and {@code from} is not
     *         strictly before {@code to}.
     */
    private static void checkChronologicalOrder(String from, String to) throws YapBotException {
        LocalDate fromDate = tryParseDate(from);
        LocalDate toDate = tryParseDate(to);
        if (fromDate == null || toDate == null) {
            return;
        }
        if (!fromDate.isBefore(toDate)) {
            throw new YapBotException(
                    "An event's '/from' date cannot be later than or the same as its '/to' date.");
        }
    }

    /**
     * Parses {@code value} as a {@code yyyy-mm-dd} date, without throwing.
     *
     * @param value the text to attempt to parse.
     * @return the parsed date, or {@code null} if {@code value} is not a valid date.
     */
    private static LocalDate tryParseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Rejects a task field that contains the '|' character, since it is the
     * delimiter used by the save file format and would corrupt it if saved.
     *
     * @param value     the field value to check.
     * @param fieldName a human-readable name for the field, used in the error message.
     * @return {@code value} unchanged, if it passed the check.
     * @throws YapBotException if {@code value} contains '|'.
     */
    private static String checkNoDelimiter(String value, String fieldName) throws YapBotException {
        if (value.contains("|")) {
            throw new YapBotException("The " + fieldName + " cannot contain the '|' character, "
                    + "as it is reserved for the save file format.");
        }
        return value;
    }

    /**
     * Parses a deadline's '/by' input as a date in {@code yyyy-mm-dd} format.
     *
     * @param value the raw '/by' text entered by the user.
     * @return the parsed date.
     * @throws YapBotException if {@code value} is not a valid {@code yyyy-mm-dd} date.
     */
    private static LocalDate parseDeadlineDate(String value) throws YapBotException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new YapBotException("Please enter the deadline date in yyyy-mm-dd format, "
                    + "e.g. 2019-10-15.");
        }
    }
}