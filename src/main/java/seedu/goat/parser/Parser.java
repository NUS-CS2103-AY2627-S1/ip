package seedu.goat.parser;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.goat.DateFormats;
import seedu.goat.GoatException;
import seedu.goat.command.Command;
import seedu.goat.task.Deadline;
import seedu.goat.task.Event;
import seedu.goat.task.Todo;

/**
 * Turns the raw text the user typed into the pieces the rest of GOAT works with.
 * <p>
 * Parsing here is deliberately ignorant of the task list: it reports what the user
 * appears to have asked for, and whether the request is well formed on its own terms.
 * Whether the request makes sense against the current list, such as whether task 7
 * exists, is a question only the list can answer and is checked by the caller.
 */
public class Parser {

    /** Introduces the due date in a deadline command. */
    private static final String BY_MARKER = "/by";

    /** Introduces the start date in an event command. */
    private static final String FROM_MARKER = "/from";

    /** Introduces the end date in an event command. */
    private static final String TO_MARKER = "/to";

    /**
     * A command together with the text that followed it.
     *
     * @param command what the user asked for.
     * @param arguments the rest of the line, trimmed, empty if there was none.
     */
    public record ParsedCommand(Command command, String arguments) {
    }

    /** Utility class: never instantiated. */
    private Parser() {
    }

    /**
     * Splits a line into its command and the arguments after it.
     *
     * @param input one line as typed.
     * @return the command and its arguments.
     * @throws GoatException if the first word matches no known command.
     */
    public static ParsedCommand parse(String input) throws GoatException {
        if (input == null || input.isBlank()) {
            throw new GoatException("Please enter a command, such as list or todo read book.");
        }
        if (input.contains("\n") || input.contains("\r") || input.contains(" | ")) {
            throw new GoatException("Use one command per line and avoid the reserved separator ' | '.");
        }
        String[] parts = input.trim().split("\\s+", 2);
        String arguments = parts.length > 1 ? parts[1].trim() : "";
        Command command = Command.fromKeyword(parts[0]);
        if ((command == Command.LIST || command == Command.BYE) && !arguments.isEmpty()) {
            throw new GoatException(command.getKeyword() + " takes no arguments. Remove the extra text.");
        }
        return new ParsedCommand(command, arguments);
    }

    /**
     * Reads the task number given to a command such as {@code mark}.
     * <p>
     * Only checks that a number was supplied and that it is a number. Whether it refers
     * to an existing task depends on the list, so the caller checks that.
     *
     * @param arguments text following the command.
     * @param command the command being run, named in the error messages.
     * @return the number the user typed, counting from 1.
     * @throws GoatException if the number is missing or is not a number.
     */
    public static int parseTaskNumber(String arguments, Command command)
            throws GoatException {
        String name = command.getKeyword();
        if (arguments.isBlank()) {
            throw new GoatException(name + " needs a task number, as in \""
                    + name + " 2\".");
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new GoatException("\"" + arguments + "\" is not a number. Give me a task"
                    + " number instead, as in \"" + name + " 2\".");
        }
    }

    /**
     * Reads the date given to the {@code on} command.
     *
     * @param arguments the date as typed.
     * @return the parsed date.
     * @throws GoatException if the date is missing or cannot be read.
     */
    public static LocalDate parseDate(String arguments) throws GoatException {
        if (arguments.isBlank()) {
            throw new GoatException("on needs a date, as in \"on 2019-10-15\".");
        }
        return DateFormats.parse(arguments);
    }

    /**
     * Reads the keyword given to the {@code find} command.
     *
     * @param arguments the keyword as typed.
     * @return the keyword to search for.
     * @throws GoatException if no keyword was given.
     */
    public static String parseKeyword(String arguments) throws GoatException {
        if (arguments.isBlank()) {
            throw new GoatException("find needs something to look for, as in"
                    + " \"find book\".");
        }
        return arguments;
    }

    /**
     * Builds a to-do from the text following the {@code todo} command.
     *
     * @param arguments the description.
     * @return the parsed to-do.
     * @throws GoatException if the description is missing.
     */
    public static Todo parseTodo(String arguments) throws GoatException {
        if (arguments.isBlank()) {
            throw new GoatException("A todo needs a description, as in"
                    + " \"todo borrow book\".");
        }
        return new Todo(arguments);
    }

    /**
     * Builds a deadline from the text following the {@code deadline} command.
     *
     * @param arguments text of the form {@code DESCRIPTION /by WHEN}
     * @return the parsed deadline.
     * @throws GoatException if the description, the {@code /by}, or the date is missing.
     */
    public static Deadline parseDeadline(String arguments) throws GoatException {
        String example = "\"deadline return book /by 2019-12-02\"";
        int byIndex = findMarker(arguments, BY_MARKER);
        if (byIndex < 0) {
            throw new GoatException("A deadline needs a /by to say when it is due, as in "
                    + example + ".");
        }

        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new GoatException("A deadline needs a description before the /by, as in "
                    + example + ".");
        }
        if (by.isEmpty()) {
            throw new GoatException("The /by is empty. Tell me when it is due, as in "
                    + example + ".");
        }
        return new Deadline(description, DateFormats.parse(by));
    }

    /**
     * Builds an event from the text following the {@code event} command.
     *
     * @param arguments text of the form {@code DESCRIPTION /from START /to END}
     * @return the parsed event.
     * @throws GoatException if a part is missing, unreadable, or the dates are reversed.
     */
    public static Event parseEvent(String arguments) throws GoatException {
        String example = "\"event project meeting /from 2019-10-15 /to 2019-10-16\"";
        int fromIndex = findMarker(arguments, FROM_MARKER);
        if (fromIndex < 0) {
            throw new GoatException("An event needs a /from to say when it starts, as in "
                    + example + ".");
        }

        // Search after /from so that a /to written before it is not mistaken for the end.
        int toIndex = findMarker(arguments, TO_MARKER);
        if (toIndex < fromIndex + FROM_MARKER.length()) {
            throw new GoatException("An event needs a /to after the /from, as in "
                    + example + ".");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new GoatException("An event needs a description before the /from, as in "
                    + example + ".");
        }
        if (from.isEmpty()) {
            throw new GoatException("The /from is empty. Tell me when it starts, as in "
                    + example + ".");
        }
        if (to.isEmpty()) {
            throw new GoatException("The /to is empty. Tell me when it ends, as in "
                    + example + ".");
        }

        LocalDate start = DateFormats.parse(from);
        LocalDate end = DateFormats.parse(to);
        if (end.isBefore(start)) {
            throw new GoatException("An event cannot end before it starts."
                    + " " + DateFormats.format(end) + " is earlier than "
                    + DateFormats.format(start) + ".");
        }
        return new Event(description, start, end);
    }
    /** Finds a unique parameter marker, rejecting repeated parameters. */
    private static int findMarker(String arguments, String marker) throws GoatException {
        Matcher matches = Pattern.compile(Pattern.quote(marker)).matcher(arguments);
        if (!matches.find()) {
            return -1;
        }
        int index = matches.start();
        if (matches.find()) {
            throw new GoatException("Use " + marker + " only once. Remove the repeated parameter.");
        }
        return index;
    }

}
