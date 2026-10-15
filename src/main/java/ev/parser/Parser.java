package ev.parser;

import java.time.LocalDate;
import java.util.List;

import ev.DateTimes;
import ev.EvException;
import ev.command.AddCommand;
import ev.command.Command;
import ev.command.DeleteCommand;
import ev.command.ExitCommand;
import ev.command.FindCommand;
import ev.command.ListCommand;
import ev.command.MarkCommand;
import ev.command.OnCommand;
import ev.command.UpdateCommand;
import ev.task.Deadline;
import ev.task.Event;
import ev.task.Task;
import ev.task.Todo;

/**
 * Turns a line the user typed into a command that is ready to run.
 *
 * <p>Everything about the shape of the input lives here: which keyword starts a command,
 * where {@code /by}, {@code /from} and {@code /to} split an argument, and what counts as
 * a task number or a date. A command that comes out of this class needs no further
 * checking, and the messages thrown along the way show the user what to type instead.
 */
public class Parser {

    private static final String DEADLINE_USAGE =
            "e.g. deadline return book /by 2019-12-02 1800";

    private static final String EVENT_USAGE =
            "e.g. event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";

    private static final String ON_USAGE = "e.g. on 2019-12-02";

    private static final String FIND_USAGE = "e.g. find book";

    private static final String UPDATE_USAGE = "e.g. update 2 /by 2019-12-05 1800";

    /** The options an update may name, in the order they are offered to the user. */
    private static final List<String> UPDATABLE_OPTIONS =
            List.of(Task.OPTION_DESC, Deadline.OPTION_BY, Event.OPTION_FROM, Event.OPTION_TO);

    /**
     * Returns the command the given line asks for.
     *
     * @param line one line of input, already trimmed and not empty.
     * @return a command holding everything it needs to run.
     * @throws EvException if the keyword is unknown or the rest of the line is not usable.
     */
    public static Command parse(String line) throws EvException {
        assert line != null && !line.isBlank() : "Blank lines are skipped by the caller, not parsed";

        String[] parts = line.split(" ", 2);
        CommandWord word = CommandWord.fromKeyword(parts[0]);
        String argument = parts.length > 1 ? parts[1].trim() : "";

        return switch (word) {
            case TODO -> new AddCommand(parseTodo(argument));
            case DEADLINE -> new AddCommand(parseDeadline(argument));
            case EVENT -> new AddCommand(parseEvent(argument));
            case LIST -> new ListCommand();
            case ON -> new OnCommand(parseDate(argument));
            case FIND -> new FindCommand(parseKeyword(argument));
            case MARK -> MarkCommand.mark(parseTaskNumber(argument));
            case UNMARK -> MarkCommand.unmark(parseTaskNumber(argument));
            case DELETE -> new DeleteCommand(parseTaskNumber(argument));
            case UPDATE -> parseUpdate(argument);
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Builds a todo from the words that follow the keyword.
     *
     * @param argument everything after {@code todo}.
     * @return the new task.
     * @throws EvException if no description was given.
     */
    public static Todo parseTodo(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("A todo needs a description.",
                    "e.g. todo borrow book");
        }
        Task.requireSavableDescription(argument);
        return new Todo(argument);
    }

    /**
     * Builds a deadline from a description followed by {@code /by} and a due date.
     *
     * @param argument everything after {@code deadline}.
     * @return the new task.
     * @throws EvException if {@code /by} is missing, either part is empty, or the date cannot be read.
     */
    public static Deadline parseDeadline(String argument) throws EvException {
        requireSingleOccurrence(argument, Deadline.OPTION_BY, DEADLINE_USAGE);

        int byIndex = argument.indexOf(Deadline.OPTION_BY);
        if (byIndex < 0) {
            throw new EvException("A deadline needs " + Deadline.OPTION_BY + ".",
                    DEADLINE_USAGE);
        }
        String description = argument.substring(0, byIndex).trim();
        String by = argument.substring(byIndex + Deadline.OPTION_BY.length()).trim();
        if (description.isEmpty()) {
            throw new EvException("Missing description before " + Deadline.OPTION_BY + ".",
                    DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new EvException("Missing date after " + Deadline.OPTION_BY + ".",
                    DEADLINE_USAGE);
        }
        Task.requireSavableDescription(description);
        return new Deadline(description, DateTimes.parse(by));
    }

    /**
     * Builds an event from a description followed by {@code /from} and {@code /to}.
     *
     * @param argument everything after {@code event}.
     * @return the new task.
     * @throws EvException if either option is missing or out of order, a part is empty,
     *     or a date cannot be read.
     */
    public static Event parseEvent(String argument) throws EvException {
        requireSingleOccurrence(argument, Event.OPTION_FROM, EVENT_USAGE);
        requireSingleOccurrence(argument, Event.OPTION_TO, EVENT_USAGE);

        int fromIndex = argument.indexOf(Event.OPTION_FROM);
        int toIndex = argument.indexOf(Event.OPTION_TO);
        if (fromIndex < 0) {
            throw new EvException("An event needs " + Event.OPTION_FROM + ".",
                    EVENT_USAGE);
        }
        if (toIndex < 0) {
            throw new EvException("An event needs " + Event.OPTION_TO + ".",
                    EVENT_USAGE);
        }
        if (toIndex < fromIndex) {
            throw new EvException(Event.OPTION_FROM + " must come before " + Event.OPTION_TO + ".",
                    EVENT_USAGE);
        }
        String description = argument.substring(0, fromIndex).trim();
        String from = argument.substring(fromIndex + Event.OPTION_FROM.length(), toIndex).trim();
        String to = argument.substring(toIndex + Event.OPTION_TO.length()).trim();
        if (description.isEmpty()) {
            throw new EvException("Missing description before " + Event.OPTION_FROM + ".",
                    EVENT_USAGE);
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new EvException("Missing date after " + Event.OPTION_FROM + " or " + Event.OPTION_TO + ".",
                    EVENT_USAGE);
        }
        Task.requireSavableDescription(description);
        return new Event(description, DateTimes.parse(from), DateTimes.parse(to));
    }

    /**
     * Checks that an option was given only once.
     *
     * <p>Without this, a second {@code /by} would be read as part of the date, and the user
     * would be told their date is unreadable rather than that they wrote the option twice.
     *
     * @param argument everything after the keyword.
     * @param option the option to count.
     * @param usage the example to show alongside the complaint.
     * @throws EvException if the option appears more than once.
     */
    private static void requireSingleOccurrence(String argument, String option, String usage)
            throws EvException {
        if (argument.indexOf(option) != argument.lastIndexOf(option)) {
            throw new EvException(option + " may only appear once.", usage);
        }
    }

    /**
     * Reads the task number a command such as {@code mark} was given.
     *
     * <p>Only the shape of the number is checked here. Whether a task with that number
     * exists is for the task list to say.
     *
     * @param argument everything after the keyword.
     * @return the number the user typed, counting from 1.
     * @throws EvException if nothing was given or it is not a whole number.
     */
    public static int parseTaskNumber(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Which task?",
                    "e.g. mark 2");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new EvException("\"" + argument + "\" is not a number.",
                    "e.g. mark 2");
        }
    }

    /**
     * Reads the keyword the {@code find} command was given.
     *
     * <p>Everything after the keyword is taken as one search term, spaces included, so
     * {@code find read book} searches for the phrase rather than two separate words.
     *
     * @param argument everything after {@code find}.
     * @return the text to search for.
     * @throws EvException if nothing was given.
     */
    public static String parseKeyword(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Search for what?", FIND_USAGE);
        }
        return argument;
    }

    /**
     * Reads the task number, the option and the new value the {@code update} command was given.
     *
     * <p>Exactly one option may appear. Whether the task in question actually has that
     * field, and whether the value can be read, are decided when the command runs, by the
     * task itself.
     *
     * @param argument everything after {@code update}.
     * @return a command that will change that one detail.
     * @throws EvException if the number, the option or the value is missing, or more than
     *     one option was given.
     */
    public static UpdateCommand parseUpdate(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Update what?",
                    UPDATE_USAGE);
        }

        String option = findSingleOption(argument);
        int optionIndex = argument.indexOf(option);
        String value = argument.substring(optionIndex + option.length()).trim();
        if (value.isEmpty()) {
            throw new EvException("Missing value after " + option + ".",
                    UPDATE_USAGE);
        }

        int taskNumber = parseTaskNumber(argument.substring(0, optionIndex).trim());
        return new UpdateCommand(taskNumber, option, value);
    }

    /**
     * Returns the one option named in an update, rejecting none and more than one.
     *
     * @param argument everything after {@code update}.
     * @return the option that appears, such as {@code /by}.
     * @throws EvException if no option appears, or several do.
     */
    private static String findSingleOption(String argument) throws EvException {
        List<String> found = UPDATABLE_OPTIONS.stream()
                .filter(argument::contains)
                .toList();

        if (found.isEmpty()) {
            throw new EvException("Name a field: " + Task.OPTION_DESC + ", "
                    + Deadline.OPTION_BY + ", " + Event.OPTION_FROM + " or " + Event.OPTION_TO + ".",
                    UPDATE_USAGE);
        }
        if (found.size() > 1) {
            throw new EvException("One field at a time.", UPDATE_USAGE);
        }
        return found.get(0);
    }

    /**
     * Reads the date the {@code on} command was given, ignoring any time of day.
     *
     * @param argument everything after {@code on}.
     * @return the date asked about.
     * @throws EvException if nothing was given or the date cannot be read.
     */
    public static LocalDate parseDate(String argument) throws EvException {
        if (argument.isEmpty()) {
            throw new EvException("Which date?", ON_USAGE);
        }
        return DateTimes.parse(argument).toLocalDate();
    }
}
