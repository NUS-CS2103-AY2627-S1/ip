package verity.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import verity.VerityException;
import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.OnCommand;
import verity.command.UndoCommand;
import verity.command.Undoable;
import verity.command.UnmarkCommand;
import verity.task.Deadline;
import verity.task.Event;
import verity.task.Todo;

/**
 * Turns a full line of user input into the {@link Command} it represents.
 */
public class Parser {
    private static final String BY_MARKER = "(?i)(?:^|\\s+)/by\\b";
    private static final String FROM_MARKER = "(?i)(?:^|\\s+)/from\\b";
    private static final String TO_MARKER = "(?i)(?:^|\\s+)/to\\b";

    /**
     * Parses a full line of user input into the {@link Command} it represents, with nothing
     * available to undo.
     *
     * @param fullCommand Full line of user input.
     * @return Command to execute.
     * @throws VerityException If the input isn't a recognized command, or its arguments are
     *                          malformed (e.g. an empty description, a missing marker, or an
     *                          invalid date). A task index that doesn't exist is <em>not</em>
     *                          caught here - that's only knowable once the command executes
     *                          against the actual task list.
     */
    public static Command parse(String fullCommand) throws VerityException {
        return parse(fullCommand, null);
    }

    /**
     * Parses a full line of user input into the {@link Command} it represents.
     *
     * @param fullCommand Full line of user input.
     * @param lastUndoableCommand Most recently executed undoable command, used if the input
     *                            is {@code undo}; {@code null} if there is nothing to undo.
     * @return Command to execute.
     * @throws VerityException If the input isn't a recognized command, or its arguments are
     *                          malformed (e.g. an empty description, a missing marker, or an
     *                          invalid date). A task index that doesn't exist is <em>not</em>
     *                          caught here - that's only knowable once the command executes
     *                          against the actual task list.
     */
    public static Command parse(String fullCommand, Undoable lastUndoableCommand) throws VerityException {
        if (fullCommand == null || fullCommand.isBlank()) {
            throw new VerityException("I didn't catch a command. Try " + CommandWord.describeAll() + ". :)");
        }

        String input = fullCommand.trim();
        String command = input.toLowerCase(Locale.ROOT);
        CommandWord matched = CommandWord.match(command);
        if (matched == null) {
            throw new VerityException(
                    "That's an invalid command! >:[\nTry " + CommandWord.describeAll() + ". :)");
        }
        switch (matched) {
            case LIST:
                return new ListCommand();
            case ON:
                return new OnCommand(parseOnDate(input));
            case FIND:
                return new FindCommand(parseFindKeyword(input));
            case UNMARK:
                return new UnmarkCommand(parseTaskIndex(input, CommandWord.UNMARK));
            case MARK:
                return new MarkCommand(parseTaskIndex(input, CommandWord.MARK));
            case DELETE:
                return new DeleteCommand(parseTaskIndex(input, CommandWord.DELETE));
            case UNDO:
                return new UndoCommand(lastUndoableCommand);
            case TODO:
                return new AddCommand(parseTodo(input));
            case DEADLINE:
                return new AddCommand(parseDeadline(input));
            case EVENT:
                return new AddCommand(parseEvent(input));
            case BYE:
                // Fallthrough
            default:
                return new ExitCommand();
        }
    }

    /**
     * Parses a {@code todo} command into a {@link Todo}.
     *
     * @param input Full line of user input, starting with "todo".
     * @return Todo built from the input.
     * @throws VerityException If the description is empty.
     */
    private static Todo parseTodo(String input) throws VerityException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new VerityException("The description of a todo can't be empty. Try `todo <what you want to do>`. ;)");
        }
        return new Todo(description);
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline}.
     *
     * @param input Full line of user input, starting with "deadline".
     * @return Deadline built from the input.
     * @throws VerityException If the description is empty, the {@code /by} marker is missing,
     *                          the due date after it is empty, or isn't a valid {@code yyyy-MM-dd} date.
     */
    private static Deadline parseDeadline(String input) throws VerityException {
        String rest = input.substring("deadline".length()).trim();
        String[] parts = rest.split(BY_MARKER, 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new VerityException(
                    "The description of a deadline can't be empty... :( \nTry `deadline <what to do> /by <when>`.");
        }
        if (parts.length < 2) {
            throw new VerityException(
                    "A deadline needs a due date! >:( \nAdd `/by <when>` after the task description.");
        }
        String by = parts[1].trim();
        if (by.isEmpty()) {
            throw new VerityException("The due time after `/by` can't be empty. Tell me when it's due.");
        }
        if (hasMarker(by, BY_MARKER)) {
            throw new VerityException(
                    "You've given `/by` more than once. Use it just once, e.g. `deadline <what to do> /by <when>`.");
        }
        return new Deadline(description, parseDate(by, "/by"));
    }

    /**
     * Returns whether the given text still contains the given marker as a separate word,
     * i.e. preceded by whitespace or the start of the text and followed by a word boundary.
     * Used to detect a marker (e.g. {@code /by}) being given more than once.
     *
     * @param text Text to check.
     * @param markerPattern Marker's word-boundary regex, e.g. {@link #BY_MARKER}.
     * @return {@code true} if the marker appears in the text.
     */
    private static boolean hasMarker(String text, String markerPattern) {
        return text.split(markerPattern, 2).length > 1;
    }

    /**
     * Parses a date string in {@code yyyy-MM-dd} format, e.g. "2019-10-15".
     *
     * @param text Date text to parse.
     * @param marker Marker the date followed (e.g. "/by"), used to word the error message.
     * @return Parsed date.
     * @throws VerityException If the text isn't a valid date in {@code yyyy-MM-dd} format.
     */
    private static LocalDate parseDate(String text, String marker) throws VerityException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new VerityException("The date after `" + marker
                    + "` must be in yyyy-MM-dd format (e.g. 2019-10-15), not '" + text + "'.");
        }
    }

    /**
     * Parses an {@code on} command into the date to filter tasks by.
     *
     * @param input Full line of user input, starting with "on".
     * @return Date to filter deadlines/events by.
     * @throws VerityException If no date is given, or it isn't a valid {@code yyyy-MM-dd} date.
     */
    private static LocalDate parseOnDate(String input) throws VerityException {
        String text = input.substring("on".length()).trim();
        if (text.isEmpty()) {
            throw new VerityException("Tell me which date to look up! Try `on <yyyy-MM-dd>`, e.g. `on 2019-10-15`.");
        }
        return parseDate(text, "on");
    }

    /**
     * Parses a {@code find} command into the keyword to search for.
     *
     * @param input Full line of user input, starting with "find".
     * @return Keyword to search task descriptions for.
     * @throws VerityException If no keyword is given.
     */
    private static String parseFindKeyword(String input) throws VerityException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new VerityException("Tell me what to search for! Try `find <keyword>`, e.g. `find book`.");
        }
        return keyword;
    }

    /**
     * Parses an {@code event} command into an {@link Event}.
     *
     * @param input Full line of user input, starting with "event".
     * @return Event built from the input.
     * @throws VerityException If the description is empty, the {@code /from} or {@code /to}
     *                          marker is missing, either date after them is empty, or isn't a
     *                          valid {@code yyyy-MM-dd} date.
     */
    private static Event parseEvent(String input) throws VerityException {
        String rest = input.substring("event".length()).trim();
        String[] parts = rest.split(FROM_MARKER, 2);
        String description = parts[0].trim();
        if (description.isEmpty()) {
            throw new VerityException(
                    "The description of an event can't be empty... :( \n"
                            + "Try `event <what's happening> /from <start> /to <end>`.");
        }
        if (hasMarker(description, TO_MARKER)) {
            throw new VerityException(
                    "`/from` must come before `/to`. Try `event <what's happening> /from <start> /to <end>`.");
        }
        if (parts.length < 2) {
            throw new VerityException("An event needs a start time! :| \nAdd `/from <when>` after the description.");
        }
        String[] fromTo = parts[1].split(TO_MARKER, 2);
        if (fromTo.length < 2) {
            throw new VerityException("An event needs an end time. :| \nAdd `/to <when>` after the start time.");
        }
        String from = fromTo[0].trim();
        if (from.isEmpty()) {
            throw new VerityException("The start time after `/from` can't be empty! \nTell me when it begins.");
        }
        if (hasMarker(from, FROM_MARKER)) {
            throw new VerityException("You've given `/from` more than once. Use it just once.");
        }
        String to = fromTo[1].trim();
        if (to.isEmpty()) {
            throw new VerityException("The end time after `/to` can't be empty! \nTell me when it ends.");
        }
        if (hasMarker(to, FROM_MARKER) || hasMarker(to, TO_MARKER)) {
            throw new VerityException("You've given `/from` or `/to` more than once. Each should appear just once.");
        }
        LocalDate startDate = parseDate(from, "/from");
        LocalDate endDate = parseDate(to, "/to");
        if (endDate.isBefore(startDate)) {
            throw new VerityException("An event can't end before it starts. Check the `/from` and `/to` dates.");
        }
        return new Event(description, startDate, endDate);
    }

    /**
     * Parses the task number following a {@code mark}/{@code unmark}/{@code delete} command.
     * Only checks that it's a well-formed number - whether a task actually exists at that
     * index is checked later, by the resulting command, once it has access to the task list.
     *
     * @param input Full line of user input, starting with the command keyword.
     * @param command Command the number was given for (its keyword is used to word the
     *                error messages and to know how many characters of {@code input}
     *                are the command word).
     * @return 0-based index the number refers to.
     * @throws VerityException If no number was given, or it isn't a number.
     */
    private static int parseTaskIndex(String input, CommandWord command) throws VerityException {
        String keyword = command.getKeyword();
        String rest = input.substring(keyword.length()).trim();
        if (rest.isEmpty()) {
            throw new VerityException(
                    "Tell me which task number you want to " + keyword + "! For example: `" + keyword + " 2`.");
        }
        int number;
        try {
            number = Integer.parseInt(rest);
        } catch (NumberFormatException e) {
            throw new VerityException(
                    "'" + rest + "' isn't a valid task number.");
        }
        if (number <= 0) {
            throw new VerityException("Task numbers start at 1. Try `" + keyword + " 1`.");
        }
        return number - 1;
    }
}
