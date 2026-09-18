package benjamin.parser;

import java.time.LocalDate;

import benjamin.BenjaminException;
import benjamin.command.AddCommand;
import benjamin.command.Command;
import benjamin.command.DeleteCommand;
import benjamin.command.ExitCommand;
import benjamin.command.FindCommand;
import benjamin.command.ListCommand;
import benjamin.command.MarkCommand;
import benjamin.command.OnCommand;
import benjamin.command.TagCommand;
import benjamin.command.UnmarkCommand;
import benjamin.command.UntagCommand;
import benjamin.task.Deadline;
import benjamin.task.Event;
import benjamin.task.Task;
import benjamin.task.TaskDateTime;
import benjamin.task.Todo;

/**
 * Makes sense of what the user typed.
 *
 * <p>Every method here turns raw text into something the rest of the program
 * can use, and reports a clear problem when the text does not make sense.
 */
public class Parser {
    private static final String KEYWORD_TODO = "todo";
    private static final String KEYWORD_DEADLINE = "deadline";
    private static final String KEYWORD_EVENT = "event";
    private static final String KEYWORD_MARK = "mark";
    private static final String KEYWORD_UNMARK = "unmark";
    private static final String KEYWORD_DELETE = "delete";
    private static final String KEYWORD_FIND = "find";
    private static final String KEYWORD_ON = "on";
    private static final String KEYWORD_TAG = "tag";
    private static final String KEYWORD_UNTAG = "untag";

    /** What a tag may be made of, once its leading hash has been dropped. */
    private static final String TAG_PATTERN = "[A-Za-z0-9_-]+";

    private static final String MARKER_BY = "/by";
    private static final String MARKER_FROM = "/from";
    private static final String MARKER_TO = "/to";

    private static final String MESSAGE_UNKNOWN_COMMAND =
            "I don't know that one. I answer to list, todo, deadline, event, mark, "
            + "unmark, delete, tag, untag, find, on and bye.";

    /**
     * Returns the command described by a line of user input.
     *
     * <p>Only the wording is checked here. Anything that depends on the tasks
     * currently in the list, such as whether a task number exists, is checked
     * when the command runs.
     *
     * @throws BenjaminException if the line cannot be understood.
     */
    public static Command parse(String input) throws BenjaminException {
        CommandType commandType = CommandType.from(input);

        switch (commandType) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case FIND:
                return new FindCommand(parseKeyword(input));
            case ON:
                return new OnCommand(parseDate(input));
            case MARK:
                return new MarkCommand(parseTaskNumber(input, KEYWORD_MARK));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(input, KEYWORD_UNMARK));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(input, KEYWORD_DELETE));
            case TAG:
                return newTagCommand(input);
            case UNTAG:
                return newUntagCommand(input);
            case TODO:
            case DEADLINE:
            case EVENT:
                return new AddCommand(parseTask(input, commandType));
            default:
                throw new BenjaminException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Returns the task described by an add command.
     *
     * @param input the full line typed by the user.
     * @param commandType which of the three task types to build.
     * @throws BenjaminException if a required part of the command is missing.
     */
    public static Task parseTask(String input, CommandType commandType) throws BenjaminException {
        if (commandType == CommandType.TODO) {
            return parseTodo(input);
        }
        if (commandType == CommandType.DEADLINE) {
            return parseDeadline(input);
        }
        if (commandType == CommandType.EVENT) {
            return parseEvent(input);
        }

        throw new BenjaminException(MESSAGE_UNKNOWN_COMMAND);
    }

    /**
     * Returns the one based task number named by a command such as {@code mark 2}.
     *
     * @param input the full line typed by the user.
     * @param keyword the command word, used both to find the number and to
     *     word any problem message.
     * @throws BenjaminException if the number is missing or is not a whole number.
     */
    public static int parseTaskNumber(String input, String keyword) throws BenjaminException {
        // parse() routes here only after CommandType matched this keyword, so
        // the input is always at least as long as the keyword itself.
        assert input.length() >= keyword.length() : "input must start with its keyword";

        String taskNumberText = input.substring(keyword.length()).trim();

        if (taskNumberText.isEmpty()) {
            throw new BenjaminException("Please provide a task number after " + keyword + ".");
        }
        if (taskNumberText.matches(".*\\s.*")) {
            throw new BenjaminException("Please give just one task number after " + keyword + ".");
        }

        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            // Digits that still fail to parse can only mean the value overflows an
            // int, which is a different complaint from "that is not a number".
            if (taskNumberText.matches("-?\\d+")) {
                throw new BenjaminException("That task number is far too large.");
            }

            throw new BenjaminException("The task number after " + keyword
                    + " must be a whole number.");
        }
    }

    /**
     * Returns the description unchanged, provided it can survive a trip through
     * the save file.
     *
     * @throws BenjaminException if it holds the character that separates saved fields.
     */
    private static String requireUsableDescription(String description) throws BenjaminException {
        if (description.contains("|")) {
            throw new BenjaminException("A description cannot contain the | character, "
                    + "because that is what separates fields in the save file.");
        }

        return description;
    }

    /**
     * Checks that a marker such as {@code /by} was given exactly once.
     *
     * @throws BenjaminException if it appears more than once.
     */
    private static void requireSingleMarker(String arguments, String marker)
            throws BenjaminException {
        if (arguments.indexOf(marker) != arguments.lastIndexOf(marker)) {
            throw new BenjaminException("You gave " + marker + " more than once. "
                    + "Please use it just once.");
        }
    }

    /**
     * Returns the text to search for, named by a {@code find} command.
     *
     * @throws BenjaminException if no keyword was given.
     */
    public static String parseKeyword(String input) throws BenjaminException {
        String keyword = input.substring(KEYWORD_FIND.length()).trim();

        if (keyword.isEmpty()) {
            throw new BenjaminException("Please provide a keyword after " + KEYWORD_FIND + ".");
        }

        return keyword;
    }

    /**
     * Returns the date named by an {@code on} command.
     *
     * @throws BenjaminException if no date was given or it cannot be read.
     */
    public static LocalDate parseDate(String input) throws BenjaminException {
        String dateText = input.substring(KEYWORD_ON.length()).trim();

        if (dateText.isEmpty()) {
            throw new BenjaminException("Please provide a date after " + KEYWORD_ON + ".");
        }

        return TaskDateTime.parse(dateText).getDate();
    }

    /**
     * The two things a tag or untag command names: which task, and which tag.
     *
     * @param taskNumber the one based position the user typed.
     * @param tag the label, already stripped of any leading hash.
     */
    public record TagArguments(int taskNumber, String tag) { }

    /**
     * Returns the task number and tag named by a tag or untag command.
     *
     * @param input the full line typed by the user.
     * @param keyword the command word, used to word any problem message.
     * @throws BenjaminException if either part is missing or malformed.
     */
    public static TagArguments parseTagArguments(String input, String keyword)
            throws BenjaminException {
        String arguments = input.substring(keyword.length()).trim();

        if (arguments.isEmpty()) {
            throw new BenjaminException("Please provide a task number and a tag after "
                    + keyword + ".");
        }

        String[] parts = arguments.split("\\s+", 2);

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[0]);
        } catch (NumberFormatException exception) {
            throw new BenjaminException("The task number after " + keyword
                    + " must be a whole number.");
        }

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new BenjaminException("Please provide a tag after the task number.");
        }

        return new TagArguments(taskNumber, requireValidTag(parts[1].trim()));
    }

    /**
     * Returns the stored form of a tag the user typed, without its leading hash.
     *
     * @throws BenjaminException if the tag is empty, holds more than one tag,
     *     or uses characters that would not survive the save file.
     */
    private static String requireValidTag(String tag) throws BenjaminException {
        String withoutHash = tag.startsWith("#") ? tag.substring(1) : tag;

        if (withoutHash.isEmpty()) {
            throw new BenjaminException("A tag needs a name after the #.");
        }
        if (withoutHash.matches(".*\\s.*")) {
            throw new BenjaminException("Please add one tag at a time.");
        }
        if (!withoutHash.matches(TAG_PATTERN)) {
            throw new BenjaminException(
                    "A tag can only contain letters, digits, hyphens and underscores.");
        }

        // Fold case here as well as in Task, so a message about an existing tag
        // shows the same spelling the task list does.
        return withoutHash.toLowerCase();
    }

    private static Command newTagCommand(String input) throws BenjaminException {
        TagArguments arguments = parseTagArguments(input, KEYWORD_TAG);

        return new TagCommand(arguments.taskNumber(), arguments.tag());
    }

    private static Command newUntagCommand(String input) throws BenjaminException {
        TagArguments arguments = parseTagArguments(input, KEYWORD_UNTAG);

        return new UntagCommand(arguments.taskNumber(), arguments.tag());
    }

    /** Returns the todo described by a {@code todo} command. */
    private static Task parseTodo(String input) throws BenjaminException {
        String description = input.substring(KEYWORD_TODO.length()).trim();

        if (description.isEmpty()) {
            throw new BenjaminException("The description of a todo cannot be empty.");
        }

        return new Todo(requireUsableDescription(description));
    }

    /** Returns the deadline described by a {@code deadline} command. */
    private static Task parseDeadline(String input) throws BenjaminException {
        String arguments = input.substring(KEYWORD_DEADLINE.length()).trim();
        requireSingleMarker(arguments, MARKER_BY);

        int byIndex = arguments.indexOf(MARKER_BY);

        if (byIndex < 0) {
            throw new BenjaminException("A deadline needs a " + MARKER_BY + " date or time.");
        }

        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + MARKER_BY.length()).trim();

        if (description.isEmpty()) {
            throw new BenjaminException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new BenjaminException("The " + MARKER_BY
                    + " date or time of a deadline cannot be empty.");
        }

        return new Deadline(requireUsableDescription(description), TaskDateTime.parse(by));
    }

    /** Returns the event described by an {@code event} command. */
    private static Task parseEvent(String input) throws BenjaminException {
        String arguments = input.substring(KEYWORD_EVENT.length()).trim();
        requireSingleMarker(arguments, MARKER_FROM);
        requireSingleMarker(arguments, MARKER_TO);

        int fromIndex = arguments.indexOf(MARKER_FROM);

        if (fromIndex < 0) {
            throw new BenjaminException("An event needs a " + MARKER_FROM + " date or time.");
        }

        int toIndex = arguments.indexOf(MARKER_TO, fromIndex + MARKER_FROM.length());

        if (toIndex < 0) {
            throw new BenjaminException("An event needs a " + MARKER_TO + " date or time.");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + MARKER_FROM.length(), toIndex).trim();
        String to = arguments.substring(toIndex + MARKER_TO.length()).trim();

        if (description.isEmpty()) {
            throw new BenjaminException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new BenjaminException("The " + MARKER_FROM
                    + " date or time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new BenjaminException("The " + MARKER_TO
                    + " date or time of an event cannot be empty.");
        }

        return new Event(requireUsableDescription(description),
                TaskDateTime.parse(from), TaskDateTime.parse(to));
    }
}
