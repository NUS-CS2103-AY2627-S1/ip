package emma;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

import emma.command.AddDeadlineCommand;
import emma.command.AddEventCommand;
import emma.command.AddTodoCommand;
import emma.command.ByeCommand;
import emma.command.Command;
import emma.command.DeleteCommand;
import emma.command.FilterCommand;
import emma.command.FindCommand;
import emma.command.ListCommand;
import emma.command.MarkCommand;

/**
 * Turns a line the user typed into the command it asks for, checking the arguments
 * so that a command is only built once its arguments are known to be good.
 */
public class Parser {

    private static final String TYPE_FLAG = "/type ";
    private static final String DUE_BY_FLAG = "/due-by";
    private static final String AT_FLAG = "/at";

    /**
     * Reads one line of input.
     *
     * @param input the line the user typed.
     * @return the command that line asks for.
     * @throws EmmaException if the line names no command Emma knows, or its arguments are wrong
     */
    public static Command parse(String input) throws EmmaException {
        String[] parts = input.trim().split(" ", 2);
        String command = parts[0];
        String arguments = parts.length > 1 ? parts[1] : "";
        return switch (command) {
            case "bye" -> parseBye(arguments);
            case "list" -> parseList(arguments);
            case "mark" -> new MarkCommand(parseTaskNumber(arguments, "mark"), true);
            case "unmark" -> new MarkCommand(parseTaskNumber(arguments, "unmark"), false);
            case "delete" -> new DeleteCommand(parseTaskNumber(arguments, "delete"));
            case "todo" -> parseTodo(arguments);
            case "deadline" -> parseDeadline(arguments);
            case "event" -> parseEvent(arguments);
            case "filter" -> parseFilter(arguments);
            case "find" -> parseFind(arguments);
            default -> throw new EmmaException("Sorry, I don't know what that means!");
        };
    }

    /**
     * Reads a "bye" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if anything was typed after the command word
     */
    private static Command parseBye(String arguments) throws EmmaException {
        requireNoArguments("bye", arguments);
        return new ByeCommand();
    }

    /**
     * Reads a "list" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if anything was typed after the command word
     */
    private static Command parseList(String arguments) throws EmmaException {
        requireNoArguments("list", arguments);
        return new ListCommand();
    }

    /**
     * Rejects anything typed after a command that takes nothing.
     *
     * <p>Commands that read an argument reject trailing text on their own, since the
     * text has to be a number, a date or a flag. The commands that read nothing would
     * otherwise carry on regardless, so that a mistyped line was silently obeyed as
     * though the extra words had never been typed.
     *
     * @param command the command word, used to word the error message.
     * @param arguments whatever followed it.
     * @throws EmmaException if anything other than whitespace followed the command word
     */
    private static void requireNoArguments(String command, String arguments)
            throws EmmaException {
        String extra = arguments.trim();
        if (!extra.isEmpty()) {
            throw new EmmaException("\"" + command + "\" takes nothing after it, "
                    + "but I got \"" + extra + "\".");
        }
    }

    /**
     * Reads a "todo" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if the description is missing
     */
    private static Command parseTodo(String arguments) throws EmmaException {
        String description = arguments.trim();
        if (description.isEmpty()) {
            throw new EmmaException("A todo needs a description, like \"todo read book\".");
        }
        return new AddTodoCommand(description);
    }

    /**
     * Reads a "find" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if there is nothing to look for
     */
    private static Command parseFind(String arguments) throws EmmaException {
        String keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new EmmaException("A find needs something to look for, like \"find book\".");
        }
        return new FindCommand(keyword);
    }

    /**
     * Reads a "deadline" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if the description or the date is missing, or is not a real date
     */
    private static Command parseDeadline(String arguments) throws EmmaException {
        String usage = "A deadline needs a description and a date, "
                + "like \"deadline return book /by 2019-10-15\".";
        String[] parts = arguments.split(" /by ", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        assert parts.length == 2 : "the check above should have rejected a missing /by";
        LocalDate by = parseDate(parts[1].trim(), "a due date");
        return new AddDeadlineCommand(parts[0].trim(), by);
    }

    /**
     * Reads an "event" command.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if a part is missing, is not a real date, or the event ends
     *     before it starts
     */
    private static Command parseEvent(String arguments) throws EmmaException {
        String usage = "An event needs a description, a start date and an end date, "
                + "like \"event project meeting /from 2019-10-15 /to 2019-10-16\".";
        String[] fromParts = arguments.split(" /from ", 2);
        if (fromParts.length < 2 || fromParts[0].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        String[] toParts = fromParts[1].split(" /to ", 2);
        if (toParts.length < 2 || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new EmmaException(usage);
        }
        LocalDate from = parseDate(toParts[0].trim(), "a start date");
        LocalDate to = parseDate(toParts[1].trim(), "an end date");
        if (from.isAfter(to)) {
            throw new EmmaException("An event has to end on or after it starts, but "
                    + Dates.format(to) + " is before " + Dates.format(from) + ".");
        }
        return new AddEventCommand(fromParts[0].trim(), from, to);
    }

    /**
     * Reads a "filter" command, working out the test its results must pass.
     *
     * @param arguments the arguments after the command word.
     * @return the command.
     * @throws EmmaException if the type is missing or unknown, or an option does not
     *     belong to that type or is not followed by a real date
     */
    private static Command parseFilter(String arguments) throws EmmaException {
        String trimmed = arguments.trim();
        if (!trimmed.startsWith(TYPE_FLAG)) {
            throw new EmmaException("A filter needs a type, like \"filter /type deadline\".");
        }
        String[] parts = trimmed.substring(TYPE_FLAG.length()).trim().split(" ", 2);
        String type = parts[0];
        String option = parts.length > 1 ? parts[1].trim() : "";

        Predicate<Task> matches = parseFilterType(type);
        if (!option.isEmpty()) {
            // Reached only once the type is known, so a bad option is reported
            // against a type Emma recognises rather than masking an unknown one.
            matches = parseFilterOption(type, option);
        }
        return new FilterCommand(matches);
    }

    /**
     * Reads the type a filter names, as in "filter /type deadline".
     *
     * @param type the word after "/type".
     * @return the test that keeps only tasks of that type.
     * @throws EmmaException if the type is not one Emma tracks
     */
    private static Predicate<Task> parseFilterType(String type) throws EmmaException {
        return switch (type) {
            case "todo" -> task -> task instanceof Todo;
            case "deadline" -> task -> task instanceof Deadline;
            case "event" -> task -> task instanceof Event;
            default -> throw new EmmaException("I can only filter by todo, deadline or event.");
        };
    }

    /**
     * Reads the date option that narrows a filter, as in "/due-by 2019-10-15".
     *
     * @param type the type the filter already named, which decides the options allowed.
     * @param option the rest of the command, starting at the option's flag.
     * @return the test that keeps only the tasks of that type matching the date.
     * @throws EmmaException if the flag is unknown, does not belong to that type, or is
     *     not followed by a real date
     */
    private static Predicate<Task> parseFilterOption(String type, String option)
            throws EmmaException {
        String[] parts = option.split(" ", 2);
        String flag = parts[0];
        String date = parts.length > 1 ? parts[1].trim() : "";

        if (flag.equals(DUE_BY_FLAG) && type.equals("deadline")) {
            LocalDate dueBy = parseDate(date, "a cutoff date");
            return task -> task instanceof Deadline deadline && deadline.isDueBy(dueBy);
        }
        if (flag.equals(AT_FLAG) && type.equals("event")) {
            LocalDate at = parseDate(date, "an event date");
            return task -> task instanceof Event event && event.isOn(at);
        }
        if (flag.equals(DUE_BY_FLAG) || flag.equals(AT_FLAG)) {
            String owner = flag.equals(DUE_BY_FLAG) ? "a deadline" : "an event";
            throw new EmmaException("Only " + owner + " filter takes \"" + flag + "\".");
        }
        throw new EmmaException("I don't know what \"" + flag + "\" means in a filter.");
    }

    /**
     * Reads the task number that a command like "mark 2" or "delete 2" was given.
     *
     * @param arguments the arguments after the command word.
     * @param command the command word, used to word the error message.
     * @return the number the user typed, not yet checked against the list.
     * @throws EmmaException if the argument is not a whole number
     */
    private static int parseTaskNumber(String arguments, String command) throws EmmaException {
        try {
            return Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new EmmaException("I need a task number, like \"" + command + " 1\".");
        }
    }

    /**
     * Reads a date written as yyyy-mm-dd, rejecting anything that is not a real date.
     *
     * @param text the date the user typed.
     * @param field the part of the command it came from, used to word the error message.
     * @return the date the user typed.
     * @throws EmmaException if the text is not a real date in yyyy-mm-dd form
     */
    private static LocalDate parseDate(String text, String field) throws EmmaException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new EmmaException("I need " + field + " as a date like 2019-10-15, "
                    + "but I got \"" + text + "\".");
        }
    }
}
