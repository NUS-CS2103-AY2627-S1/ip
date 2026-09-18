package zhangwei.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import zhangwei.ZhangWeiException;
import zhangwei.command.AddCommand;
import zhangwei.command.Command;
import zhangwei.command.CommandType;
import zhangwei.command.DeleteCommand;
import zhangwei.command.ExitCommand;
import zhangwei.command.FindCommand;
import zhangwei.command.ListCommand;
import zhangwei.command.MarkCommand;
import zhangwei.command.SortCommand;
import zhangwei.command.UnmarkCommand;
import zhangwei.task.Deadline;
import zhangwei.task.Event;
import zhangwei.task.SortCriterion;
import zhangwei.task.Todo;

/**
 * Makes sense of the text the user types, turning one line into the
 * {@link Command} it asks for.
 *
 * <p>Only what can be judged from the text alone is checked here: that the
 * keyword exists, that a task number is a number, that a deadline was given a
 * description and a /by. Whether task 5 exists depends on the task list, so
 * that check belongs to the command instead.
 *
 * <p>The methods are static because parsing needs nothing remembered between
 * calls; the parser has no state of its own to hold.
 */
public class Parser {

    /** Prevents instantiation: this class is only a home for static methods. */
    private Parser() {
    }

    /**
     * Returns the command the given line asks for.
     *
     * @param fullCommand one line exactly as the user typed it.
     * @return the command that line asks for, ready to be executed.
     * @throws ZhangWeiException if the line does not describe a usable command.
     */
    public static Command parse(String fullCommand) throws ZhangWeiException {
        // Stray spaces before the keyword would otherwise make it unrecognisable.
        String command = fullCommand.strip();
        // The first word is the command keyword; the rest is its argument.
        String keyword = command.split(" ")[0];
        // Everything after the keyword, e.g. "return book /by 2019-12-02".
        String arguments = command.substring(keyword.length()).trim();

        CommandType type = CommandType.fromKeyword(keyword);
        return switch (type) {
        case BYE -> new ExitCommand();
        case LIST -> new ListCommand();
        case FIND -> new FindCommand(parseKeyword(arguments));
        case SORT -> new SortCommand(parseSortCriterion(arguments));
        case MARK -> new MarkCommand(parseTaskNumber(arguments, type));
        case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments, type));
        case DELETE -> new DeleteCommand(parseTaskNumber(arguments, type));
        case TODO -> new AddCommand(parseTodo(arguments));
        case DEADLINE -> new AddCommand(parseDeadline(arguments));
        case EVENT -> new AddCommand(parseEvent(arguments));
        };
    }

    /**
     * Returns the 1-based task number the given argument names.
     *
     * @param arguments the text typed after the command word, e.g. "2".
     * @param type the command asking, used to phrase the example.
     * @return the task number as an integer. It may still be out of range;
     *     only the task list can tell.
     * @throws ZhangWeiException if it is missing or is not a number.
     */
    private static int parseTaskNumber(String arguments, CommandType type)
            throws ZhangWeiException {
        String example = "For example: " + type.getKeyword() + " 2";
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("Which task? " + example);
        }

        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new ZhangWeiException("\"" + arguments + "\" is not a task number. "
                    + example);
        }
    }

    /**
     * Returns the keyword the user wants to search for.
     *
     * <p>The keyword is taken whole rather than split into words, so
     * "find return book" looks for the phrase "return book".
     *
     * @param arguments the text typed after "find", e.g. "book".
     * @return the keyword, with the surrounding spaces already removed.
     * @throws ZhangWeiException if no keyword was given, since an empty
     *     keyword matches every task and so answers nothing.
     */
    private static String parseKeyword(String arguments) throws ZhangWeiException {
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("What should I search for? "
                    + "For example: find book");
        }
        return arguments;
    }

    /**
     * Returns the criterion the user wants the list sorted by.
     *
     * @param arguments the text typed after "sort", e.g. "date".
     * @return the criterion that text names.
     * @throws ZhangWeiException if no criterion was given, or the one given is
     *     not a criterion this chatbot knows.
     */
    private static SortCriterion parseSortCriterion(String arguments)
            throws ZhangWeiException {
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("Sort by what? For example: sort date. "
                    + "I can sort by: " + SortCriterion.listKeywords() + ".");
        }
        return SortCriterion.fromKeyword(arguments);
    }

    /**
     * Returns a todo built from the text typed after "todo".
     *
     * @param arguments the text typed after "todo", e.g. "read book".
     * @return the todo described by that text.
     * @throws ZhangWeiException if no description was given.
     */
    private static Todo parseTodo(String arguments) throws ZhangWeiException {
        if (arguments.isEmpty()) {
            throw new ZhangWeiException("A todo needs a description. "
                    + "For example: todo read book");
        }
        rejectSeparator(arguments);
        return new Todo(arguments);
    }

    /**
     * Returns a deadline built from text of the form
     * "description /by yyyy-MM-dd".
     *
     * @param arguments the text typed after "deadline".
     * @return the deadline described by that text.
     * @throws ZhangWeiException if the description or /by date is invalid.
     */
    private static Deadline parseDeadline(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/by", 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new ZhangWeiException("A deadline needs a description and a /by. "
                    + "For example: deadline return book /by 2019-12-02");
        }
        rejectSeparator(arguments);
        LocalDate by = parseDate(parts[1].trim(), "/by");
        return new Deadline(parts[0].trim(), by);
    }

    /**
     * Returns an event built from text of the form
     * "description /from yyyy-MM-dd /to yyyy-MM-dd".
     *
     * @param arguments the text typed after "event".
     * @return the event described by that text.
     * @throws ZhangWeiException if the description or either date is invalid,
     *     or if the event would end before it starts.
     */
    private static Event parseEvent(String arguments) throws ZhangWeiException {
        String[] parts = arguments.split("/from|/to");
        if (parts.length < 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new ZhangWeiException("An event needs a description, a /from and a /to. "
                    + "For example: event project meeting /from 2019-12-03 "
                    + "/to 2019-12-04");
        }
        rejectSeparator(arguments);
        LocalDate from = parseDate(parts[1].trim(), "/from");
        LocalDate to = parseDate(parts[2].trim(), "/to");
        // This also catches "/to ... /from ...", where the split above would
        // otherwise hand the two dates over the wrong way round.
        if (to.isBefore(from)) {
            throw new ZhangWeiException("An event cannot end before it starts. "
                    + "Put /from before /to, for example: event project meeting "
                    + "/from 2019-12-03 /to 2019-12-04");
        }
        return new Event(parts[0].trim(), from, to);
    }

    /**
     * Parses a date written in the ISO {@code yyyy-MM-dd} format.
     *
     * @param text the date as the user typed it, e.g. "2019-12-02".
     * @param marker the marker the date followed, e.g. "/by", used to say
     *     which date the complaint is about.
     * @return the date that text names.
     * @throws ZhangWeiException if the date is missing, malformed, or impossible.
     */
    private static LocalDate parseDate(String text, String marker)
            throws ZhangWeiException {
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ZhangWeiException("The " + marker
                    + " date must use yyyy-MM-dd, for example 2019-12-02.");
        }
    }

    /**
     * Rejects text containing the character that separates fields in the save
     * file. Allowing it would split one task across several fields, so the task
     * would come back wrong (or not at all) the next time the chatbot starts.
     *
     * @param text the text the user typed for a task.
     * @throws ZhangWeiException if the text contains "|".
     */
    private static void rejectSeparator(String text) throws ZhangWeiException {
        if (text.contains("|")) {
            throw new ZhangWeiException("A task cannot contain \"|\", because "
                    + "that character separates the fields in the save file.");
        }
    }
}
