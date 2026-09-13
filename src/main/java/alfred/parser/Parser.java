package alfred.parser;

import java.time.LocalDateTime;

import alfred.command.AddCommand;
import alfred.command.Command;
import alfred.command.DeleteCommand;
import alfred.command.ExitCommand;
import alfred.command.FindCommand;
import alfred.command.ListCommand;
import alfred.command.MarkCommand;
import alfred.command.SortCommand;
import alfred.command.UnmarkCommand;
import alfred.exception.AlfredException;
import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Task;
import alfred.task.TaskDateTime;
import alfred.task.Todo;

/**
 * Interprets user commands and constructs the tasks requested by those commands.
 */
public class Parser {
    private static final String DEADLINE_BY_MARKER = " /by ";
    private static final String DEADLINE_BY_MARKER_WITHOUT_VALUE = " /by";
    private static final String EVENT_FROM_MARKER = " /from ";
    private static final String EVENT_FROM_MARKER_WITHOUT_VALUE = " /from";
    private static final String EVENT_TO_MARKER = " /to ";
    private static final String EVENT_TO_MARKER_WITHOUT_VALUE = " /to";
    private static final String SORT_USAGE_MESSAGE =
            "Alfred only understands `sort`, `sort asc`, or `sort desc`.";

    /** Returns the command type recognized at the start of a user input line. */
    private CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /**
     * Creates an executable command when the user input contains sufficient command details.
     *
     * @param input full command entered by the user
     * @param taskCount number of tasks currently stored
     * @return the executable command described by the input
     * @throws AlfredException if the input has an unknown command or invalid command details
     */
    public Command parseCommand(String input, int taskCount) throws AlfredException {
        assert input != null : "Command input must not be null";
        assert taskCount >= 0 : "Task counts must not be negative";
        input = input.trim();
        CommandType commandType = parseCommandType(input);
        if (isSortCommand(input)) {
            return new SortCommand(parseSortOrder(input));
        }
        if (commandType == CommandType.UNKNOWN) {
            throw new AlfredException("I do not recognise that command. Come again.");
        }
        if (commandType == CommandType.BYE) {
            validateNoArguments(input, commandType);
            return new ExitCommand();
        }
        if (commandType == CommandType.LIST) {
            validateNoArguments(input, commandType);
            return new ListCommand();
        }
        if (commandType == CommandType.FIND) {
            return new FindCommand(parseFindKeyword(input));
        }
        if (commandType == CommandType.MARK) {
            return new MarkCommand(parseTaskIndex(input, commandType.getKeyword(), taskCount));
        }
        if (commandType == CommandType.UNMARK) {
            return new UnmarkCommand(parseTaskIndex(input, commandType.getKeyword(), taskCount));
        }
        if (commandType == CommandType.DELETE) {
            return new DeleteCommand(parseTaskIndex(input, commandType.getKeyword(), taskCount));
        }
        return new AddCommand(parseTask(input, commandType));
    }

    /** Parses the optional direction supplied to a sort command. */
    private boolean parseSortOrder(String input) throws AlfredException {
        if (!input.startsWith(CommandType.SORT.getKeyword())) {
            throw new AlfredException(SORT_USAGE_MESSAGE);
        }

        String direction = extractCommandArguments(input, CommandType.SORT);
        if (direction.isEmpty() || direction.equals("asc")) {
            return true;
        }
        if (direction.equals("desc")) {
            return false;
        }
        throw new AlfredException(SORT_USAGE_MESSAGE);
    }

    /** Returns whether the input begins with the sort command, regardless of letter case. */
    private boolean isSortCommand(String input) {
        String sortKeyword = CommandType.SORT.getKeyword();
        return input.regionMatches(true, 0, sortKeyword, 0, sortKeyword.length())
                && (input.length() == sortKeyword.length() || input.charAt(sortKeyword.length()) == ' ');
    }

    /** Parses the keyword supplied to a find command. */
    private String parseFindKeyword(String input) throws AlfredException {
        String keyword = extractCommandArguments(input, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new AlfredException("Alfred needs a keyword after `find`.");
        }
        return keyword;
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param input full command entered by the user
     * @param commandType recognized command type
     * @return the task described by the command
     * @throws AlfredException if the command is unrecognized or has invalid task details
     */
    private Task parseTask(String input, CommandType commandType) throws AlfredException {
        assert commandType != CommandType.UNKNOWN : "Unknown commands must be rejected before task parsing";
        if (commandType == CommandType.DEADLINE) {
            return parseDeadline(input);
        }
        if (commandType == CommandType.EVENT) {
            return parseEvent(input);
        }
        if (commandType == CommandType.TODO) {
            return parseTodo(input);
        }
        throw new AlfredException("I do not recognise that command. Come again.");
    }

    /**
     * Validates a task number supplied to a command and converts it to an array index.
     *
     * @param input full command entered by the user
     * @param commandName name of the command requesting a task number
     * @param taskCount number of tasks currently stored
     * @return the zero-based index of the requested task
     * @throws AlfredException if the task number is missing, invalid, or not in the task list
     */
    public int parseTaskIndex(String input, String commandName, int taskCount) throws AlfredException {
        String taskNumberText = input.substring(commandName.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new AlfredException("Alfred needs a task number after `" + commandName + "`.");
        }
        if (taskCount == 0) {
            throw new AlfredException("Alfred's task list is empty, so there is nothing to "
                    + commandName + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new AlfredException("Alfred needs a whole-number task number after `" + commandName + "`.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new AlfredException("Alfred cannot find task " + taskNumber + ". Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /** Parses a deadline command into a deadline task. */
    private Task parseDeadline(String input) throws AlfredException {
        int byMarkerIndex = input.indexOf(DEADLINE_BY_MARKER);
        if (byMarkerIndex == -1) {
            if (input.endsWith(DEADLINE_BY_MARKER_WITHOUT_VALUE)) {
                throw new AlfredException("Alfred needs a due time after `/by`.");
            }
            throw new AlfredException("Alfred needs `/by` followed by a due time for a deadline.");
        }
        if (hasMarkerAfter(input, DEADLINE_BY_MARKER, byMarkerIndex + DEADLINE_BY_MARKER.length())) {
            throw new AlfredException("Alfred accepts only one `/by` marker for a deadline.");
        }
        String description = input.substring(CommandType.DEADLINE.getKeyword().length(), byMarkerIndex)
                .trim();
        String by = input.substring(byMarkerIndex + DEADLINE_BY_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new AlfredException("Alfred needs a deadline description before `/by`.");
        }
        if (by.isEmpty()) {
            throw new AlfredException("Alfred needs a due time after `/by`.");
        }
        LocalDateTime byDateTime = TaskDateTime.parse(by);
        return new Deadline(description, byDateTime);
    }

    /** Parses an event command into an event task. */
    private Task parseEvent(String input) throws AlfredException {
        int fromMarkerIndex = input.indexOf(EVENT_FROM_MARKER);
        if (fromMarkerIndex == -1) {
            if (input.endsWith(EVENT_FROM_MARKER_WITHOUT_VALUE)) {
                throw new AlfredException("Alfred needs a start time after `/from`.");
            }
            throw new AlfredException("Alfred needs `/from` followed by a start time for an event.");
        }
        int toMarkerIndex = input.indexOf(EVENT_TO_MARKER, fromMarkerIndex + EVENT_FROM_MARKER.length());
        if (toMarkerIndex == -1) {
            if (input.endsWith(EVENT_TO_MARKER_WITHOUT_VALUE)) {
                throw new AlfredException("Alfred needs an end time after `/to`.");
            }
            throw new AlfredException("Alfred needs `/to` followed by an end time for an event.");
        }
        if (hasMarkerAfter(input, EVENT_FROM_MARKER, fromMarkerIndex + EVENT_FROM_MARKER.length())) {
            throw new AlfredException("Alfred accepts only one `/from` marker for an event.");
        }
        if (hasMarkerAfter(input, EVENT_TO_MARKER, toMarkerIndex + EVENT_TO_MARKER.length())) {
            throw new AlfredException("Alfred accepts only one `/to` marker for an event.");
        }
        String description = input.substring(CommandType.EVENT.getKeyword().length(), fromMarkerIndex).trim();
        String from = input.substring(fromMarkerIndex + EVENT_FROM_MARKER.length(), toMarkerIndex).trim();
        String to = input.substring(toMarkerIndex + EVENT_TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new AlfredException("Alfred needs an event description before `/from`.");
        }
        if (from.isEmpty()) {
            throw new AlfredException("Alfred needs a start time after `/from`.");
        }
        if (to.isEmpty()) {
            throw new AlfredException("Alfred needs an end time after `/to`.");
        }
        LocalDateTime fromDateTime = TaskDateTime.parse(from);
        LocalDateTime toDateTime = TaskDateTime.parse(to);
        if (!fromDateTime.isBefore(toDateTime)) {
            throw new AlfredException("Alfred needs an event end time later than its start time.");
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    /** Parses a to-do command into a to-do task. */
    private Task parseTodo(String input) throws AlfredException {
        String description = extractCommandArguments(input, CommandType.TODO);
        if (description.isEmpty()) {
            throw new AlfredException("Alfred cannot add a to-do without a mission description.");
        }
        return new Todo(description);
    }

    /** Returns the text following a recognized command keyword. */
    private String extractCommandArguments(String input, CommandType commandType) {
        return input.substring(commandType.getKeyword().length()).trim();
    }

    /** Rejects additional text for a command that does not accept parameters. */
    private void validateNoArguments(String input, CommandType commandType) throws AlfredException {
        if (!extractCommandArguments(input, commandType).isEmpty()) {
            throw new AlfredException("Alfred's `" + commandType.getKeyword()
                    + "` command does not take additional arguments.");
        }
    }

    /** Returns whether a syntax marker appears after the marker position already parsed. */
    private boolean hasMarkerAfter(String input, String marker, int nextSearchIndex) {
        return input.indexOf(marker, nextSearchIndex) != -1;
    }
}
