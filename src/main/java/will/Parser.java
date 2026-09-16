package will;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import will.command.AddCommand;
import will.command.Command;
import will.command.DeleteCommand;
import will.command.ExitCommand;
import will.command.FindCommand;
import will.command.ListCommand;
import will.command.MarkCommand;
import will.command.OnCommand;
import will.command.UnmarkCommand;
import will.task.Deadline;
import will.task.Event;
import will.task.FlexibleDate;
import will.task.Todo;

/**
 * Deals with making sense of the user command: turns one line of raw
 * input into the Command object that knows how to carry it out.
 *
 * Note: this only validates what can be known from the text alone
 * (numeric format, presence of required parts). A task-number argument
 * (mark/unmark/delete) can't be bounds-checked here, since Parser never
 * sees the TaskList — that check happens in the Command's execute().
 */
public class Parser {
    // Named as constants (rather than repeated string literals) so
    // that both the parsing logic below and the "Try: ..." usage
    // messages always agree on the exact delimiter text, and so the
    // parsing code never has to hardcode a delimiter's length as a
    // magic number (e.g. "+ 3" for "/by") to skip past it.
    private static final String DELIMITER_BY = "/by";
    private static final String DELIMITER_FROM = "/from";
    private static final String DELIMITER_TO = "/to";

    /**
     * Turns one line of raw user input into the {@link Command} that
     * knows how to carry it out.
     *
     * @param fullCommand The raw command line, e.g. "mark 2".
     * @return The Command to execute.
     * @throws WillException If the command isn't recognized, or is
     *                       missing/has malformed arguments.
     */
    public static Command parse(String fullCommand) throws WillException {
        String command = fullCommand.split(" ", 2)[0].toLowerCase();
        String rest = fullCommand.length() > command.length()
                ? fullCommand.substring(command.length()).trim()
                : "";

        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (command.equals("on")) {
            return parseOn(rest);
        } else if (command.equals("mark")) {
            return new MarkCommand(parseTaskIndex(command, rest));
        } else if (command.equals("unmark")) {
            return new UnmarkCommand(parseTaskIndex(command, rest));
        } else if (command.equals("delete")) {
            return new DeleteCommand(parseTaskIndex(command, rest));
        } else if (command.equals("todo")) {
            return parseTodo(rest);
        } else if (command.equals("deadline")) {
            return parseDeadline(rest);
        } else if (command.equals("event")) {
            return parseEvent(rest);
        } else if (command.equals("find")) {
            return parseFind(rest);
        } else {
            throw new WillException("I don't recognize that command. "
                    + "Try: todo, deadline, event, list, on, find, mark, unmark, delete, or bye.");
        }
    }

    /** Parses the argument for "on " into an {@link OnCommand}. */
    private static Command parseOn(String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me which date! Try: on <yyyy-MM-dd>, e.g. on 2019-10-15");
        }
        LocalDate queryDate;
        try {
            queryDate = FlexibleDate.parseExact(rest);
        } catch (DateTimeParseException e) {
            throw new WillException("\"" + rest + "\" isn't a date in yyyy-MM-dd format. "
                    + "Try: on <yyyy-MM-dd>, e.g. on 2019-10-15");
        }
        return new OnCommand(queryDate);
    }

    /** Parses the argument for "find" into a FindCommand. */
    private static Command parseFind(String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me what to search for! Try: find <keyword>");
        }
        return new FindCommand(rest);
    }

    /**
     * Parses the task number argument for mark/unmark/delete, validating
     * that it is present and numeric. Whether it refers to an existing
     * task can't be checked here (see class javadoc) — Command#execute
     * does that via Command#requireValidIndex.
     */
    private static int parseTaskIndex(String command, String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("Tell me which task number! Try: " + command + " <task number>");
        }
        try {
            return Integer.parseInt(rest) - 1;
        } catch (NumberFormatException e) {
            throw new WillException("\"" + rest + "\" isn't a valid task number.");
        }
    }

    /** Parses the argument for "todo" into an AddCommand wrapping a Todo. */
    private static Command parseTodo(String rest) throws WillException {
        if (rest.isEmpty()) {
            throw new WillException("A todo needs a description! Try: todo <what you need to do>");
        }
        requireNoPipe(rest, "description");
        return new AddCommand(new Todo(rest));
    }

    /**
     * Parses the argument for "deadline" (a description and a /by
     * time) into an AddCommand wrapping a Deadline.
     */
    private static Command parseDeadline(String rest) throws WillException {
        if (rest.isEmpty() || !rest.contains(DELIMITER_BY)) {
            throw new WillException("A deadline needs a description and a " + DELIMITER_BY + " time! "
                    + "Try: deadline <what you need to do> " + DELIMITER_BY + " <when it's due>");
        }
        int byIndex = rest.indexOf(DELIMITER_BY);
        String description = rest.substring(0, byIndex).trim();
        String by = rest.substring(byIndex + DELIMITER_BY.length()).trim();
        if (description.isEmpty()) {
            throw new WillException("A deadline needs a description before " + DELIMITER_BY + "! "
                    + "Try: deadline <what you need to do> " + DELIMITER_BY + " <when it's due>");
        }
        if (by.isEmpty()) {
            throw new WillException("Tell me when this deadline is due! "
                    + "Try: deadline <what you need to do> " + DELIMITER_BY + " <when it's due>");
        }
        requireNoPipe(description, "description");
        requireNoPipe(by, DELIMITER_BY + " time");
        return new AddCommand(new Deadline(description, by));
    }

    /**
     * Parses the argument for "event" (a description, a /from time,
     * and a /to time) into an AddCommand wrapping an Event.
     */
    private static Command parseEvent(String rest) throws WillException {
        if (rest.isEmpty() || !rest.contains(DELIMITER_FROM) || !rest.contains(DELIMITER_TO)) {
            throw new WillException("An event needs a description, a " + DELIMITER_FROM + " time and a "
                    + DELIMITER_TO + " time! "
                    + "Try: event <what's happening> " + DELIMITER_FROM + " <start> " + DELIMITER_TO + " <end>");
        }
        int fromIndex = rest.indexOf(DELIMITER_FROM);
        int toIndex = rest.indexOf(DELIMITER_TO);
        if (fromIndex > toIndex) {
            throw new WillException("Your " + DELIMITER_FROM + " time needs to come before " + DELIMITER_TO + "! "
                    + "Try: event <what's happening> " + DELIMITER_FROM + " <start> " + DELIMITER_TO + " <end>");
        }
        String description = rest.substring(0, fromIndex).trim();
        String from = rest.substring(fromIndex + DELIMITER_FROM.length(), toIndex).trim();
        String to = rest.substring(toIndex + DELIMITER_TO.length()).trim();
        if (description.isEmpty()) {
            throw new WillException("An event needs a description before " + DELIMITER_FROM + "! "
                    + "Try: event <what's happening> " + DELIMITER_FROM + " <start> " + DELIMITER_TO + " <end>");
        }
        if (from.isEmpty()) {
            throw new WillException("Tell me when this event starts! "
                    + "Try: event <what's happening> " + DELIMITER_FROM + " <start> " + DELIMITER_TO + " <end>");
        }
        if (to.isEmpty()) {
            throw new WillException("Tell me when this event ends! "
                    + "Try: event <what's happening> " + DELIMITER_FROM + " <start> " + DELIMITER_TO + " <end>");
        }
        requireNoPipe(description, "description");
        requireNoPipe(from, DELIMITER_FROM + " time");
        requireNoPipe(to, DELIMITER_TO + " time");
        return new AddCommand(new Event(description, from, to));
    }

    /**
     * Rejects {@code field} if it contains a "|" character. The save
     * file format is pipe-delimited, so a "|" typed into a
     * description/by/from/to field would silently corrupt it (it'd be
     * misread as an extra field on the next load). Reject it up front
     * with a clear message instead of accepting input we can't save
     * correctly.
     */
    private static void requireNoPipe(String field, String label) throws WillException {
        if (field.contains("|")) {
            throw new WillException("Sorry, the " + label + " can't contain a \"|\" character. "
                    + "Try rephrasing without it.");
        }
    }
}
