package chud.parser;

import chud.command.AddDeadlineCommand;
import chud.command.AddEventCommand;
import chud.command.AddTodoCommand;
import chud.command.Command;
import chud.command.DeleteCommand;
import chud.command.ExitCommand;
import chud.command.FindCommand;
import chud.command.ListCommand;
import chud.command.MarkCommand;
import chud.command.OnCommand;
import chud.command.UnknownCommand;
import chud.command.UnmarkCommand;
import chud.exception.ChudException;
import chud.task.SortDirection;
import chud.task.SortKey;
import chud.task.TaskDateTime;

/**
 * Makes sense of a raw line of user input: splits it into a command word and arguments, and
 * further interprets each command's arguments into the well-typed pieces its handler needs
 * (a task index; a todo's description; a deadline's description and {@code /by} date/time; an
 * event's description and {@code /from}/{@code /to} date/times; an {@code on} query date).
 *
 * <p>Every parsing failure is reported as a {@link ChudException} carrying a specific,
 * correctable message, so the caller can show it to the user without knowing the reason itself.
 */
public class Parser {
    /** Returns the command word (e.g. "todo") from a raw input line. */
    public static String parseCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    /** Returns the trimmed arguments text (e.g. "borrow book") from a raw input line. */
    public static String parseArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses a full raw input line into the Command it represents, ready to execute. An
     * unrecognized command word yields an UnknownCommand rather than failing here, so the
     * "I don't know what '...' means" message is reported the same way as any other command's
     * execution failure, via ChudException from Command.execute.
     */
    public static Command parse(String input) {
        String commandWord = parseCommandWord(input);
        String arguments = parseArguments(input);
        switch (CommandWord.fromCommandWord(commandWord)) {
            case LIST:
                return new ListCommand(arguments);
            case MARK:
                return new MarkCommand(arguments);
            case UNMARK:
                return new UnmarkCommand(arguments);
            case DELETE:
                return new DeleteCommand(arguments);
            case TODO:
                return new AddTodoCommand(arguments);
            case DEADLINE:
                return new AddDeadlineCommand(arguments);
            case EVENT:
                return new AddEventCommand(arguments);
            case ON:
                return new OnCommand(arguments);
            case FIND:
                return new FindCommand(arguments);
            case BYE:
                return new ExitCommand();
            case UNKNOWN:
            default:
                return new UnknownCommand(commandWord);
        }
    }

    /**
     * Parses a 1-based task number typed by the user and returns the matching 0-based list index.
     *
     * @param arguments The raw text typed after the command word (expected to be a task number).
     * @param taskCount The current number of tasks, used to validate the number is in range.
     * @param commandWord The command word this index was typed for (e.g. "mark"), used only to
     *      compose an example in the error message if arguments is empty.
     * @return The 0-based index into the task list.
     * @throws ChudException If arguments is empty, not a number, or out of range.
     */
    public static int parseTaskIndex(String arguments, int taskCount, String commandWord)
            throws ChudException {
        if (arguments.isEmpty()) {
            throw new ChudException("Tell me which task number, e.g. " + commandWord + " 2");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            throw new ChudException("'" + arguments + "' is not a valid task number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new ChudException("There is no task number " + taskNumber + ". "
                    + (taskCount == 0 ? "Your task list is empty." : "You have " + taskCount + " task(s)."));
        }
        int index = taskNumber - 1;
        // Every out-of-range taskNumber was rejected above, so the 0-based index handed back
        // must fall inside the list -- callers (Mark/Unmark/DeleteCommand) rely on this to index
        // into TaskList without re-checking bounds themselves.
        assert index >= 0 && index < taskCount : "parseTaskIndex must return a valid 0-based index";
        return index;
    }

    /**
     * Validates a todo's arguments and returns its description.
     *
     * @throws ChudException If arguments is empty.
     */
    public static String parseTodoDescription(String arguments) throws ChudException {
        if (arguments.isEmpty()) {
            throw new ChudException("The description of a todo cannot be empty. Try: todo borrow book");
        }
        return arguments;
    }

    /** A deadline's parsed description and {@code /by} date/time. */
    public static class DeadlineArgs {
        public final String description;
        public final TaskDateTime by;

        private DeadlineArgs(String description, TaskDateTime by) {
            this.description = description;
            this.by = by;
        }
    }

    /**
     * Parses a deadline's arguments (description and {@code /by} date/time).
     *
     * @throws ChudException If the {@code /by} marker is missing, the description or {@code /by}
     *      value is empty, or the {@code /by} value isn't a date/time in a recognized format.
     */
    public static DeadlineArgs parseDeadlineArgs(String arguments) throws ChudException {
        int byIndex = arguments.indexOf("/by ");
        if (byIndex == -1) {
            throw new ChudException("A deadline needs a '/by' date/time. "
                    + "Try: deadline return book /by 2019-10-15 1800");
        }
        String description = arguments.substring(0, byIndex).trim();
        String byText = arguments.substring(byIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChudException("The description of a deadline cannot be empty. "
                    + "Try: deadline return book /by 2019-10-15 1800");
        }
        if (byText.isEmpty()) {
            throw new ChudException("The '/by' date/time of a deadline cannot be empty. "
                    + "Try: deadline return book /by 2019-10-15 1800");
        }
        try {
            return new DeadlineArgs(description, TaskDateTime.parse(byText));
        } catch (IllegalArgumentException e) {
            throw new ChudException(e.getMessage());
        }
    }

    /** An event's parsed description and {@code /from}/{@code /to} date/times. */
    public static class EventArgs {
        public final String description;
        public final TaskDateTime from;
        public final TaskDateTime to;

        private EventArgs(String description, TaskDateTime from, TaskDateTime to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Parses an event's arguments (description and {@code /from}/{@code /to} date/times).
     *
     * @throws ChudException If the {@code /from}/{@code /to} markers are missing or out of
     *      order, the description or a date/time value is empty, or a date/time value isn't in
     *      a recognized format.
     */
    public static EventArgs parseEventArgs(String arguments) throws ChudException {
        int fromIndex = arguments.indexOf("/from ");
        int toIndex = arguments.indexOf("/to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new ChudException(
                    "An event needs both '/from' and '/to' date/times, in that order. "
                            + "Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String fromText = arguments.substring(fromIndex + 6, toIndex).trim();
        String toText = arguments.substring(toIndex + 4).trim();
        if (description.isEmpty()) {
            throw new ChudException("The description of an event cannot be empty. "
                    + "Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        if (fromText.isEmpty() || toText.isEmpty()) {
            throw new ChudException("The '/from' and '/to' date/times of an event cannot be empty. "
                    + "Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        TaskDateTime from;
        TaskDateTime to;
        try {
            from = TaskDateTime.parse(fromText);
            to = TaskDateTime.parse(toText);
        } catch (IllegalArgumentException e) {
            throw new ChudException(e.getMessage());
        }
        // The /from and /to markers being in the right order in the text (checked above) says
        // nothing about whether the dates they carry are -- "/from 2026-09-22 /to 2026-09-21" is
        // marker-order-correct but still an event that ends before it starts.
        if (to.isBefore(from)) {
            throw new ChudException("An event's '/to' date/time can't be before its '/from' date/time. "
                    + "Try: event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600");
        }
        return new EventArgs(description, from, to);
    }

    /**
     * Parses the date typed after {@code on} into the date/time to filter tasks by.
     *
     * @throws ChudException If arguments is empty, or isn't a date/time in a recognized format.
     */
    public static TaskDateTime parseOnDate(String arguments) throws ChudException {
        if (arguments.isEmpty()) {
            throw new ChudException("Tell me which date, e.g. on 2019-10-15");
        }
        try {
            return TaskDateTime.parse(arguments);
        } catch (IllegalArgumentException e) {
            throw new ChudException(e.getMessage());
        }
    }

    /** Validates a find command's arguments and returns the keyword to search for. */
    public static String parseFindKeyword(String arguments) throws ChudException {
        if (arguments.isEmpty()) {
            throw new ChudException("Tell me what keyword to search for, e.g. find book");
        }
        return arguments;
    }

    /** A list command's parsed {@code /sort} key and direction. */
    public static class ListSortArgs {
        public final SortKey key;
        public final SortDirection direction;

        private ListSortArgs(SortKey key, SortDirection direction) {
            this.key = key;
            this.direction = direction;
        }
    }

    /**
     * Parses a list command's optional {@code /sort <key> [asc|desc]} argument.
     *
     * @return null if arguments is empty, meaning no sort was requested.
     * @throws ChudException If arguments is non-empty but isn't a well-formed {@code /sort}
     *      option: missing the {@code /sort} marker, an unrecognized key, an unrecognized
     *      direction, or extra trailing text.
     */
    public static ListSortArgs parseListSortArgs(String arguments) throws ChudException {
        if (arguments.isEmpty()) {
            return null;
        }
        String usage = "Try: list /sort date, list /sort description, list /sort type, "
                + "or list /sort done (add 'desc' at the end to reverse, e.g. list /sort date desc).";
        if (!arguments.startsWith("/sort ")) {
            throw new ChudException("Unknown list option '" + arguments + "'. " + usage);
        }
        String[] tokens = arguments.substring("/sort ".length()).trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            throw new ChudException("Tell me what to sort by. " + usage);
        }
        SortKey key = SortKey.fromArgument(tokens[0]);
        if (key == null) {
            throw new ChudException("'" + tokens[0] + "' isn't a sort key I understand. " + usage);
        }
        if (tokens.length > 2) {
            throw new ChudException("Too many arguments after '/sort " + tokens[0] + "'. " + usage);
        }
        SortDirection direction = SortDirection.ASC;
        if (tokens.length == 2) {
            direction = SortDirection.fromArgument(tokens[1]);
            if (direction == null) {
                throw new ChudException("'" + tokens[1] + "' isn't 'asc' or 'desc'. " + usage);
            }
        }
        return new ListSortArgs(key, direction);
    }
}
