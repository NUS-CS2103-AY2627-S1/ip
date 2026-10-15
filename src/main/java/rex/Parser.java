package rex;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import rex.command.AddCommand;
import rex.command.Command;
import rex.command.CommandType;
import rex.command.DeleteCommand;
import rex.command.ExitCommand;
import rex.command.FindCommand;
import rex.command.ListCommand;
import rex.command.MarkCommand;
import rex.command.OnCommand;
import rex.command.UnknownCommand;
import rex.command.UnmarkCommand;
import rex.task.Deadline;
import rex.task.Event;
import rex.task.Task;
import rex.task.TaskDateTime;
import rex.task.ToDo;
import rex.task.WithinPeriod;

/**
 * Makes sense of what the user typed.
 *
 * This is the only class that knows the shape of a command: that the first
 * word names it, that a deadline's date follows "/by", that an event's times
 * are separated by "/from" and "/to". Everything it hands back is already
 * checked, so the rest of the program works with tasks and numbers rather
 * than with raw text.
 *
 * Anything the user got wrong is reported as a RexException carrying a
 * sentence meant to be shown to them as-is.
 */
public class Parser {
    /** Shown with both event errors, so the example is written down once. */
    private static final String EVENT_EXAMPLE =
            "e.g. event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600.";

    /** Shown with both within errors, for the same reason as EVENT_EXAMPLE. */
    private static final String WITHIN_EXAMPLE =
            "e.g. within collect certificate /from 2026-01-15 /to 2026-01-25.";

    /**
     * Turns a line the user typed into the command it asks for.
     *
     * Note that the command is only built here, not carried out: nothing in
     * this class looks at the task list, so a command that names a task by
     * number keeps the number and checks it when it runs.
     *
     * @param input the whole line the user typed.
     * @return the command it asks for, ready to be run.
     * @throws RexException if the line names a command but describes it
     *     wrongly, e.g. a deadline with no date.
     */
    public static Command parse(String input) throws RexException {
        String argument = parseArgument(input);
        switch (parseCommandType(input)) {
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(argument));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(argument));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(argument));
            case TODO:
                return new AddCommand(parseTodo(argument));
            case DEADLINE:
                return new AddCommand(parseDeadline(argument));
            case EVENT:
                return new AddCommand(parseEvent(argument));
            case WITHIN:
                return new AddCommand(parseWithin(argument));
            case ON:
                return new OnCommand(parseQueryDate(argument));
            case FIND:
                return new FindCommand(parseKeyword(argument));
            case BYE:
                return new ExitCommand();
            case UNKNOWN:
            default:
                return new UnknownCommand();
        }
    }

    /**
     * Returns which command the input names, or UNKNOWN if it names none.
     *
     * @param input the whole line the user typed.
     */
    public static CommandType parseCommandType(String input) {
        try {
            return CommandType.valueOf(commandWordOf(input).toUpperCase());
        } catch (IllegalArgumentException e) {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Returns everything after the command word, or "" if nothing follows it.
     *
     * @param input the whole line the user typed.
     */
    public static String parseArgument(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1);
    }

    /**
     * Reads the argument of a todo command.
     *
     * @param argument everything after the word "todo".
     * @return the task it describes.
     * @throws RexException if no description was given.
     */
    public static ToDo parseTodo(String argument) throws RexException {
        return new ToDo(requireDescription(argument, "todo"));
    }

    /**
     * Reads the argument of a deadline command, e.g.
     * "return book /by 2019-10-15".
     *
     * @param argument everything after the word "deadline".
     * @return the task it describes.
     * @throws RexException if the description or the /by date is missing, or
     *     the date cannot be read.
     */
    public static Deadline parseDeadline(String argument) throws RexException {
        String[] parts = argument.split(" /by ", 2);
        String description = requireDescription(parts[0], "deadline");
        String by = requireFollowing(parts, "Ruff! A deadline needs a '/by' date, "
                + "e.g. deadline return book /by 2019-10-15.");

        return new Deadline(description, parseDateTime(by));
    }

    /**
     * Reads the argument of an event command, e.g.
     * "project meeting /from 2019-10-15 1400 /to 2019-10-15 1600".
     *
     * @param argument everything after the word "event".
     * @return the task it describes.
     * @throws RexException if the description, the /from time or the /to time
     *     is missing, either date cannot be read, or the event ends before it
     *     starts.
     */
    public static Event parseEvent(String argument) throws RexException {
        String[] fromParts = argument.split(" /from ", 2);
        String description = requireDescription(fromParts[0], "event");
        String times = requireFollowing(fromParts,
                "Ruff! An event needs a '/from' time, " + EVENT_EXAMPLE);

        String[] toParts = times.split(" /to ", 2);
        String to = requireFollowing(toParts,
                "Ruff! An event needs a '/to' time, " + EVENT_EXAMPLE);

        TaskDateTime start = parseDateTime(toParts[0]);
        TaskDateTime end = parseDateTime(to);
        requireInOrder(start, end, "Ruff! An event can't end before it starts.");

        return new Event(description, start, end);
    }

    /**
     * Reads the argument of a within command, e.g.
     * "collect certificate /from 2026-01-15 /to 2026-01-25".
     *
     * The same /from and /to markers as an event, so that there is one way of
     * writing a pair of dates rather than one way per command.
     *
     * @param argument everything after the word "within".
     * @return the task it describes.
     * @throws RexException if the description, the /from date or the /to date
     *     is missing, either date cannot be read, or the period ends before it
     *     starts.
     */
    public static WithinPeriod parseWithin(String argument) throws RexException {
        String[] fromParts = argument.split(" /from ", 2);
        String description = requireDescription(fromParts[0], "within-period task");
        String dates = requireFollowing(fromParts,
                "Ruff! A within task needs a '/from' date, " + WITHIN_EXAMPLE);

        String[] toParts = dates.split(" /to ", 2);
        String to = requireFollowing(toParts,
                "Ruff! A within task needs a '/to' date, " + WITHIN_EXAMPLE);

        TaskDateTime start = parseDateTime(toParts[0]);
        TaskDateTime end = parseDateTime(to);
        requireInOrder(start, end, "Ruff! A within task's period can't end before it starts.");

        return new WithinPeriod(description, start, end);
    }

    /**
     * Reads a task number as the user typed it, counting from 1.
     *
     * Only whether it is a number at all is checked here. Whether it names a
     * task depends on how many tasks there are, which the task list knows and
     * this class does not.
     *
     * @param argument the number the user typed, e.g. "3".
     * @throws RexException if the argument is not a number.
     */
    public static int parseTaskNumber(String argument) throws RexException {
        try {
            return Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            throw new RexException("Woof! \"" + argument.trim() + "\" isn't a valid task number.");
        }
    }

    /**
     * Reads the day the user asked about with the on command.
     *
     * Unlike a task's own date this is always a whole day, so a time of day is
     * refused rather than quietly ignored: "what's on the 15th at 6pm" is not
     * a question this command answers, and dropping the time would hide that.
     *
     * @param argument everything after the word "on".
     * @throws RexException if it is not a plain date.
     */
    public static LocalDate parseQueryDate(String argument) throws RexException {
        try {
            return LocalDate.parse(argument.trim());
        } catch (DateTimeParseException e) {
            throw new RexException("Woof! Tell me which day to look at, written as yyyy-mm-dd, "
                    + "e.g. on 2019-10-15.");
        }
    }

    /**
     * Reads the keyword the user asked to search for.
     *
     * The keyword is taken whole rather than split into words, so that
     * "find return book" looks for that phrase instead of for either word.
     *
     * @param argument everything after the word "find".
     * @throws RexException if no keyword was given.
     */
    public static String parseKeyword(String argument) throws RexException {
        String keyword = argument.trim();
        if (keyword.isEmpty()) {
            throw new RexException("Woof! Tell me what to sniff out, e.g. find book.");
        }
        return keyword;
    }

    /**
     * Returns the description in front of the first marker, refusing one that
     * is empty or that holds the save file's separator.
     *
     * Every kind of task begins the same way, with a description, so the
     * checks and their wording live here rather than once per kind.
     *
     * The separator is refused as a single "|" rather than only with the
     * spaces around it. Only the spaced form actually breaks a saved line, but
     * "no |" is a rule the user can remember, where "no | between two spaces"
     * is not.
     *
     * @param text everything before the first marker, e.g. "/by".
     * @param taskKind how the kind of task is named in the message shown to
     *     the user, e.g. "deadline".
     * @throws RexException if nothing but spaces was given, or the description
     *     contains the separator.
     */
    private static String requireDescription(String text, String taskKind) throws RexException {
        String description = text.trim();
        if (description.isEmpty()) {
            throw new RexException("Ruff! The description of a " + taskKind + " cannot be empty.");
        }

        String separator = Task.FIELD_SEPARATOR.trim();
        if (description.contains(separator)) {
            throw new RexException("Ruff! A description can't contain '" + separator
                    + "' — I use it to keep your tasks apart in the save file.");
        }
        return description;
    }

    /**
     * Returns what followed a marker such as "/by", refusing input where the
     * marker is missing or nothing comes after it.
     *
     * @param parts the result of splitting on the marker, at most two long.
     * @param message the complete sentence to show if the part is not there.
     * @throws RexException if the marker was missing or followed by nothing.
     */
    private static String requireFollowing(String[] parts, String message) throws RexException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new RexException(message);
        }
        return parts[1];
    }

    /**
     * Refuses a pair of dates whose end comes before its start.
     *
     * A period like that describes no stretch of time at all, and one whose
     * days are the wrong way round falls on no day, so the on command would
     * never find it.
     *
     * @param start the date after /from.
     * @param end the date after /to.
     * @param message the complete sentence to show if the dates are the wrong
     *     way round.
     * @throws RexException if the end comes before the start.
     */
    private static void requireInOrder(TaskDateTime start, TaskDateTime end, String message)
            throws RexException {
        if (end.isBefore(start)) {
            throw new RexException(message);
        }
    }

    /** Returns the first space-separated word, e.g. "todo" from "todo borrow book". */
    private static String commandWordOf(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    /**
     * Reads a date the user typed after /by, /from or /to.
     *
     * TaskDateTime reports a date it cannot read as an IllegalArgumentException,
     * which would end the program. It is turned into a RexException here so that
     * it is shown to the user and the session carries on, the same as any
     * other mistake in a command.
     */
    private static TaskDateTime parseDateTime(String argument) throws RexException {
        try {
            return TaskDateTime.parse(argument);
        } catch (IllegalArgumentException e) {
            throw new RexException("Woof! I don't understand the date \"" + argument.trim()
                    + "\". Write it as yyyy-mm-dd, e.g. 2019-10-15, "
                    + "optionally with a 24-hour time, e.g. 2019-10-15 1800.");
        }
    }
}
