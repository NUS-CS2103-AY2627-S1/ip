package dennis.parser;

import java.time.LocalDate;

import dennis.DennisException;
import dennis.command.AddCommand;
import dennis.command.Command;
import dennis.command.DeleteCommand;
import dennis.command.ExitCommand;
import dennis.command.FindCommand;
import dennis.command.ListCommand;
import dennis.command.MarkCommand;
import dennis.command.OnCommand;
import dennis.command.UnmarkCommand;
import dennis.task.Deadline;
import dennis.task.Event;
import dennis.task.Task;
import dennis.task.Todo;
import dennis.task.WithinPeriodTask;

/**
 * Makes sense of a raw command line typed by the user and turns it into a
 * {@link Command} that is ready to run.
 *
 * <p>Design note: {@link #parse(String)} is the single entry point. It
 * works out which command was typed, pulls out the pieces that command
 * needs (task number, description, dates), and returns the matching
 * {@code Command} subclass. Anything malformed is reported as a
 * {@link DennisException} carrying a message meant for the user. The task
 * classes still validate their own content (for example rejecting an
 * empty description), so this class only splits the input; it does not
 * judge whether the pieces make a sensible task.</p>
 *
 * <p>Every method is {@code static}: parsing needs no state, so there is
 * nothing to gain from creating a {@code Parser} object.</p>
 */
public final class Parser {

    /** Text that marks the due date in a {@code deadline} command. */
    private static final String BY_MARKER = " /by ";

    /** Text that marks the start time in an {@code event} command. */
    private static final String FROM_MARKER = " /from ";

    /** Text that marks the end time in an {@code event} command. */
    private static final String TO_MARKER = " /to ";

    /** Shown whenever the input cannot be understood at all. */
    private static final String DONT_UNDERSTAND =
            "Meow? I have no idea what that means.";

    /** The two pieces of a {@code deadline} command. */
    private record DeadlineParts(String description, String by) {
    }

    /** The three pieces of an {@code event} command. */
    private record EventParts(String description, String from, String to) {
    }

    /** The three pieces of a {@code within} command. */
    private record WithinParts(String description, String from, String to) {
    }

    /** Prevents instantiation; this is a utility class. */
    private Parser() {
    }

    /**
     * Turns one line of user input into the {@link Command} it describes.
     *
     * @param fullCommand the raw line exactly as the user typed it; leading
     *                    and trailing whitespace is ignored
     * @return the command to run
     * @throws DennisException if the line is empty, unrecognised, or
     *                         missing details the command needs
     */
    public static Command parse(String fullCommand) throws DennisException {
        // Every keyword-argument extractor below assumes the command word
        // starts at index 0 (it does input.substring(keyword.length())), so
        // leading whitespace must be stripped here, once, before any of them
        // run. Without this, a line like " todo read book" would have its
        // description extracted one character short of where it actually
        // starts.
        fullCommand = fullCommand.trim();

        switch (CommandType.from(fullCommand)) {
            case BYE:
                requireBareCommand(fullCommand, "bye");
                return new ExitCommand();
            case LIST:
                requireBareCommand(fullCommand, "list");
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(fullCommand, "mark"));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(fullCommand, "unmark"));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
            case TODO:
                return new AddCommand(new Todo(parseTodo(fullCommand)));
            case DEADLINE:
                DeadlineParts d = parseDeadline(fullCommand);
                return new AddCommand(new Deadline(d.description(), d.by()));
            case EVENT:
                EventParts e = parseEvent(fullCommand);
                return new AddCommand(
                        new Event(e.description(), e.from(), e.to()));
            case ON:
                return new OnCommand(parseOnDate(fullCommand));
            case WITHIN:
                WithinParts w = parseWithinPeriod(fullCommand);
                return new AddCommand(
                        new WithinPeriodTask(w.description(), w.from(), w.to()));
            case FIND:
                return new FindCommand(parseFind(fullCommand));
            case UNKNOWN:
            default:
                throw new DennisException(DONT_UNDERSTAND);
        }
    }

    /**
     * Checks that a keyword-only command (such as {@code bye} or
     * {@code list}) was typed on its own, with no trailing text.
     */
    private static void requireBareCommand(String input, String keyword)
            throws DennisException {
        if (!input.equals(keyword)) {
            throw new DennisException(DONT_UNDERSTAND);
        }
    }

    /**
     * Reads the task-number argument of a
     * {@code mark}/{@code unmark}/{@code delete} command. Only the "is a
     * number present" and "is it an integer" checks happen here; whether
     * the number refers to a real task is checked later by
     * {@link TaskCommand#checkInRange(TaskList)}, once the list size is
     * known.
     *
     * @param input   the raw line, e.g. {@code "delete 2"}
     * @param keyword the command word, e.g. {@code "delete"}
     * @return the number the user typed
     * @throws DennisException if the argument is missing or not an integer
     */
    private static int parseTaskNumber(String input, String keyword)
            throws DennisException {
        String number = input.substring(keyword.length()).trim();

        if (number.isEmpty()) {
            throw new DennisException("Please enter a task number.");
        }

        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new DennisException("The task number must be an integer.");
        }
    }

    /**
     * Checks whether {@code marker} appears a second time in {@code input}
     * after its first occurrence at {@code firstIndex}. Used to give a
     * specific "you used this marker twice" error instead of letting the
     * second occurrence corrupt the field that follows it (for example,
     * silently folding a second {@code /by} into the due-date text, where it
     * would only surface as a confusing "not a valid date" error).
     *
     * @param input      the full command line
     * @param marker     the marker text to look for, e.g. {@code " /by "}
     * @param firstIndex the marker's already-found first occurrence
     * @return {@code true} if {@code marker} occurs again after {@code
     *         firstIndex}
     */
    private static boolean hasDuplicateMarker(String input, String marker, int firstIndex) {
        return input.indexOf(marker, firstIndex + marker.length()) >= 0;
    }

    /**
     * Extracts the description from a {@code todo} command. The text is
     * returned as-is (it may be empty); {@link Todo} decides whether it is
     * acceptable.
     */
    private static String parseTodo(String input) {
        return input.substring("todo".length()).trim();
    }

    /**
     * Splits a {@code deadline} command into its description and due-date
     * text around the {@code /by} marker.
     *
     * @throws DennisException if the {@code /by} marker is missing
     */
    private static DeadlineParts parseDeadline(String input)
            throws DennisException {
        int byIndex = input.indexOf(BY_MARKER);
        if (byIndex < 0) {
            throw new DennisException("Use /by to specify the deadline.");
        }
        if (hasDuplicateMarker(input, BY_MARKER, byIndex)) {
            throw new DennisException("Use /by only once to specify the deadline.");
        }

        String description =
                input.substring("deadline".length(), byIndex).trim();
        String by = input.substring(byIndex + BY_MARKER.length()).trim();
        return new DeadlineParts(description, by);
    }

    /**
     * Splits an {@code event} command into its description, start text and
     * end text around the {@code /from} and {@code /to} markers.
     *
     * @throws DennisException if {@code /from} or {@code /to} is missing,
     *                         or they appear in the wrong order
     */
    private static EventParts parseEvent(String input) throws DennisException {
        int fromIndex = input.indexOf(FROM_MARKER);
        int toIndex = input.indexOf(TO_MARKER);

        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new DennisException("Use /from and /to to specify "
                    + "the duration of the event.");
        }
        if (hasDuplicateMarker(input, FROM_MARKER, fromIndex)
                || hasDuplicateMarker(input, TO_MARKER, toIndex)) {
            throw new DennisException("Use /from and /to only once each "
                    + "to specify the duration of the event.");
        }

        String description =
                input.substring("event".length(), fromIndex).trim();
        String from = input.substring(
                fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = input.substring(toIndex + TO_MARKER.length()).trim();
        return new EventParts(description, from, to);
    }

    /**
     * Reads the date argument of an {@code on} command and parses it.
     *
     * @throws DennisException if no date was given, or it is not a valid
     *                         {@code yyyy-MM-dd} date
     */
    private static LocalDate parseOnDate(String input) throws DennisException {
        String date = input.substring("on".length()).trim();
        if (date.isEmpty()) {
            throw new DennisException(
                    "Please enter a date, e.g. on 2019-12-01.");
        }
        return Task.parseDate(date, "The date");
    }

    /**
     * Splits a {@code within} command into its description, window-start
     * text and window-end text around the {@code /from} and {@code /to}
     * markers.
     *
     * @throws DennisException if {@code /from} or {@code /to} is missing,
     *                         or they appear in the wrong order
     */
    private static WithinParts parseWithinPeriod(String input) throws DennisException {
        int fromIndex = input.indexOf(FROM_MARKER);
        int toIndex = input.indexOf(TO_MARKER);

        if (fromIndex < 0 || toIndex < 0 || toIndex < fromIndex) {
            throw new DennisException("Use /from and /to to specify "
                    + "the period for this task.");
        }
        if (hasDuplicateMarker(input, FROM_MARKER, fromIndex)
                || hasDuplicateMarker(input, TO_MARKER, toIndex)) {
            throw new DennisException("Use /from and /to only once each "
                    + "to specify the period for this task.");
        }

        String description =
                input.substring("within".length(), fromIndex).trim();
        String from = input.substring(
                fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = input.substring(toIndex + TO_MARKER.length()).trim();
        return new WithinParts(description, from, to);
    }

    /**
     * Reads the keyword argument of a {@code find} command.
     *
     * @throws DennisException if no keyword was given
     */
    private static String parseFind(String input) throws DennisException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new DennisException("Please enter a keyword to search for.");
        }
        return keyword;
    }
}
