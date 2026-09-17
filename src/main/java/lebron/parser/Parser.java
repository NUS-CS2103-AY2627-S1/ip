package lebron.parser;

import lebron.exception.LebronException;
import lebron.task.DateTime;
import lebron.task.Deadline;
import lebron.task.Event;
import lebron.task.RecurrencePeriod;
import lebron.task.Task;

/**
 * Turns a raw line typed by the user into a {@link ParsedCommand}.
 *
 * <p>All the "does this make sense?" checks live here: unknown keywords,
 * missing descriptions, non-numeric task numbers, and unparseable dates are
 * all reported by throwing {@link LebronException} with a message meant to
 * be shown to the user.
 */
public class Parser {
    /** This is a utility class and is not meant to be instantiated. */
    private Parser() {
    }

    /**
     * Parses one full command line into the command it represents.
     *
     * @param fullCommand the raw line the user typed
     * @return the parsed command, ready to be carried out
     * @throws LebronException if the line is not a command the chatbot
     *     understands, or a required part is missing or malformed
     */
    public static ParsedCommand parse(String fullCommand) throws LebronException {
        String trimmed = fullCommand.trim();
        int spaceIndex = trimmed.indexOf(' ');
        String keyword = spaceIndex == -1 ? trimmed : trimmed.substring(0, spaceIndex);
        String arguments = spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();

        switch (keyword) {
            case "list":
                return ParsedCommand.list();
            case "bye":
                return ParsedCommand.bye();
            case "todo":
                if (arguments.isEmpty()) {
                    throw new LebronException("OOPS!!! A todo needs a description, e.g. todo read book");
                }
                requireNoPipe(arguments);
                return ParsedCommand.todo(arguments);
            case "find":
                if (arguments.isEmpty()) {
                    throw new LebronException("OOPS!!! Tell me what to search for, e.g. find book");
                }
                return ParsedCommand.find(arguments);
            case "deadline":
                return ParsedCommand.ofTask(ParsedCommand.Type.DEADLINE, parseDeadline(arguments));
            case "event":
                return ParsedCommand.ofTask(ParsedCommand.Type.EVENT, parseEvent(arguments));
            case "mark":
                return ParsedCommand.ofIndex(ParsedCommand.Type.MARK, parseTaskIndex(arguments, "mark"));
            case "unmark":
                return ParsedCommand.ofIndex(ParsedCommand.Type.UNMARK, parseTaskIndex(arguments, "unmark"));
            case "delete":
                return ParsedCommand.ofIndex(ParsedCommand.Type.DELETE, parseTaskIndex(arguments, "delete"));
            default:
                throw new LebronException("OOPS!!! I don't understand that command. "
                        + "Try: list, todo, deadline, event, mark, unmark, delete, find, or bye.");
        }
    }

    /**
     * Parses a 1-based task index out of the given arguments.
     *
     * @param arguments the text after the command keyword
     * @param keyword the command keyword, used only in the error message
     * @return the task number as typed (range checking is left to {@code TaskList})
     * @throws LebronException if the argument is missing or not a number
     */
    private static int parseTaskIndex(String arguments, String keyword) throws LebronException {
        if (arguments.isEmpty()) {
            throw new LebronException("OOPS!!! Tell me which task number to " + keyword
                    + ", e.g. " + keyword + " 2");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new LebronException("OOPS!!! '" + arguments + "' doesn't look like a task number.");
        }
    }

    /**
     * Builds a {@link Deadline} from {@code <description> /by <date> [/every <period>]}.
     *
     * @param arguments the text after the {@code deadline} keyword
     * @return the deadline task
     * @throws LebronException if the description or date is missing, the
     *     date cannot be understood, or {@code /every} is malformed
     */
    private static Task parseDeadline(String arguments) throws LebronException {
        String usage = "OOPS!!! A deadline needs a description and a /by date, "
                + "e.g. deadline return book /by 2019-12-02 1800 [/every day|week|month]";
        int byIndex = arguments.indexOf("/by");
        if (byIndex == -1) {
            throw new LebronException(usage);
        }
        requireAtMostOnce(arguments, "/by");
        requireAtMostOnce(arguments, "/every");
        int everyIndex = arguments.indexOf("/every");
        if (everyIndex != -1 && everyIndex < byIndex) {
            throw new LebronException("OOPS!!! /every must come after /by.");
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = (everyIndex == -1
                ? arguments.substring(byIndex + "/by".length())
                : arguments.substring(byIndex + "/by".length(), everyIndex)).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new LebronException(usage);
        }
        requireNoPipe(description);
        RecurrencePeriod recurrence = parsePeriodIfPresent(arguments, everyIndex);
        try {
            return new Deadline(description, DateTime.parse(by), recurrence);
        } catch (IllegalArgumentException e) {
            throw new LebronException("OOPS!!! I don't understand the date '" + by
                    + "'. Try e.g. 2019-12-02 or 2019-12-02 1800.");
        }
    }

    /**
     * Builds an {@link Event} from
     * {@code <description> /from <start> /to <end> [/every <period>]}.
     *
     * @param arguments the text after the {@code event} keyword
     * @return the event task
     * @throws LebronException if a part is missing, a date cannot be
     *     understood, or {@code /every} is malformed
     */
    private static Task parseEvent(String arguments) throws LebronException {
        String usage = "OOPS!!! An event needs a description, a /from and a /to date, "
                + "e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600 [/every day|week|month]";
        int fromIndex = arguments.indexOf("/from");
        int toIndex = arguments.indexOf("/to");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new LebronException(usage);
        }
        requireAtMostOnce(arguments, "/from");
        requireAtMostOnce(arguments, "/to");
        requireAtMostOnce(arguments, "/every");
        int everyIndex = arguments.indexOf("/every");
        if (everyIndex != -1 && everyIndex < toIndex) {
            throw new LebronException("OOPS!!! /every must come after /to.");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = (everyIndex == -1
                ? arguments.substring(toIndex + "/to".length())
                : arguments.substring(toIndex + "/to".length(), everyIndex)).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new LebronException(usage);
        }
        requireNoPipe(description);
        RecurrencePeriod recurrence = parsePeriodIfPresent(arguments, everyIndex);
        DateTime fromDateTime;
        DateTime toDateTime;
        try {
            fromDateTime = DateTime.parse(from);
            toDateTime = DateTime.parse(to);
        } catch (IllegalArgumentException e) {
            throw new LebronException("OOPS!!! I don't understand one of those dates. "
                    + "Try e.g. 2019-12-02 or 2019-12-02 1800.");
        }
        if (toDateTime.compareTo(fromDateTime) <= 0) {
            throw new LebronException("OOPS!!! An event's /to must be after its /from.");
        }
        return new Event(description, fromDateTime, toDateTime, recurrence);
    }

    /**
     * Parses an optional trailing {@code /every <period>}. The caller has
     * already confirmed {@code everyIndex} (if not -1) comes after the
     * command's date flag(s).
     *
     * @param arguments the full argument text
     * @param everyIndex the index of {@code /every} in {@code arguments}, or -1 if absent
     * @return the period, or {@code null} if {@code arguments} had no {@code /every}
     * @throws LebronException if {@code /every} is present but has no value,
     *     or the value isn't day/week/month
     */
    private static RecurrencePeriod parsePeriodIfPresent(String arguments, int everyIndex) throws LebronException {
        if (everyIndex == -1) {
            return null;
        }
        String periodText = arguments.substring(everyIndex + "/every".length()).trim();
        if (periodText.isEmpty()) {
            throw new LebronException("OOPS!!! Tell me how often, e.g. /every week (day, week, or month).");
        }
        try {
            return RecurrencePeriod.parse(periodText);
        } catch (IllegalArgumentException e) {
            throw new LebronException("OOPS!!! '" + periodText
                    + "' isn't a recurrence I understand. Try day, week, or month.");
        }
    }

    /**
     * Rejects a description containing {@code |}, the character the data
     * file uses as a field separator -- letting it through would silently
     * corrupt that task on the next save/load round trip.
     *
     * @param description the description as the user typed it
     * @throws LebronException if it contains {@code |}
     */
    private static void requireNoPipe(String description) throws LebronException {
        if (description.contains("|")) {
            throw new LebronException("OOPS!!! A task description can't contain '|' "
                    + "(it's used internally to store tasks) -- please remove it.");
        }
    }

    /**
     * Rejects a command that gives the same flag more than once (e.g. two
     * {@code /by}s), instead of letting the later occurrence silently get
     * swallowed into whatever field is parsed next.
     *
     * @param arguments the full argument text
     * @param flag the flag to check, e.g. {@code "/by"}
     * @throws LebronException if {@code flag} appears more than once
     */
    private static void requireAtMostOnce(String arguments, String flag) throws LebronException {
        int first = arguments.indexOf(flag);
        if (first != -1 && arguments.indexOf(flag, first + 1) != -1) {
            throw new LebronException("OOPS!!! Give " + flag + " only once.");
        }
    }
}
