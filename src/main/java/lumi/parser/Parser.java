package lumi.parser;

import java.time.LocalDateTime;

import lumi.command.CommandType;
import lumi.datetime.DateTimeParser;
import lumi.exception.LumiException;
import lumi.task.Deadline;
import lumi.task.Event;
import lumi.task.Task;
import lumi.task.Todo;

/**
 * Interprets raw user input as Lumi commands and command arguments.
 * Separating parsing from execution lets future command objects receive
 * already-validated values instead of processing strings themselves.
 */
public final class Parser {
    /** Separates a deadline description from its due date. */
    private static final String DEADLINE_SEPARATOR = "/by";

    /** Separates an event description from its start date. */
    private static final String EVENT_START_SEPARATOR = "/from";

    /** Introduces an event end date or a new date for a snoozed task. */
    private static final String TO_SEPARATOR = "/to";

    /** Prevents creation of this stateless utility class. */
    private Parser() {
    }

    /**
     * Identifies the command represented by a complete input line.
     *
     * @param input Complete user input.
     * @return Matching command type.
     * @throws LumiException If the input is empty or does not match a command.
     */
    public static CommandType parseCommandType(String input) throws LumiException {
        if (input.isEmpty()) {
            throw new LumiException("please enter a command.");
        }

        for (CommandType commandType : CommandType.values()) {
            if (commandType.matches(input)) {
                assert commandType != CommandType.UNKNOWN
                        : "UNKNOWN must never match a command keyword";
                return commandType;
            }
        }
        throw new LumiException("I don't recognize that command. "
                + "Try todo, deadline, event, list, find, mark, unmark, delete, snooze, or bye.");
    }

    /**
     * Creates the task represented by an add command.
     * Deadline and event dates are parsed into {@code LocalDateTime} values.
     *
     * @param command Complete command entered by the user.
     * @param commandType Type of task creation command.
     * @return A to-do, deadline, or event based on the command type.
     * @throws LumiException If required task details are missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType) throws LumiException {
        switch (commandType) {
            case TODO:
                return parseTodo(extractCommandDetails(command, commandType));
            case DEADLINE:
                return parseDeadline(extractCommandDetails(command, commandType));
            case EVENT:
                return parseEvent(extractCommandDetails(command, commandType));
            default:
                throw new LumiException("I don't recognize that task type.");
        }
    }

    /** Extracts the text following a recognized command keyword. */
    private static String extractCommandDetails(String command, CommandType commandType) {
        return command.substring(commandType.getKeyword().length()).trim();
    }

    /** Creates a to-do after validating its description. */
    private static Todo parseTodo(String description) throws LumiException {
        if (description.isEmpty()) {
            throw new LumiException("a todo needs a description. "
                    + "Try: todo <description>");
        }
        return new Todo(description);
    }

    /** Creates a deadline after validating its description and due date. */
    private static Deadline parseDeadline(String details) throws LumiException {
        int separatorCount = countSeparators(details, DEADLINE_SEPARATOR);
        if (separatorCount > 1) {
            throw new LumiException("the /by parameter can only be used once.");
        }
        int separatorPosition = findSeparator(details, DEADLINE_SEPARATOR, 0);
        if (separatorPosition < 0) {
            throw new LumiException("a deadline needs a due date. "
                    + "Try: deadline <description> /by <when>");
        }

        String description = details.substring(0, separatorPosition).trim();
        String dueDateText = details.substring(
                separatorPosition + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new LumiException("a deadline needs a description before /by.");
        }
        if (dueDateText.isEmpty()) {
            throw new LumiException("the /by value cannot be empty.");
        }
        return new Deadline(description, DateTimeParser.parseUserInput(dueDateText));
    }

    /** Creates an event after validating its description and date range. */
    private static Event parseEvent(String details) throws LumiException {
        int startSeparatorCount = countSeparators(details, EVENT_START_SEPARATOR);
        int endSeparatorCount = countSeparators(details, TO_SEPARATOR);
        if (startSeparatorCount > 1) {
            throw new LumiException("the /from parameter can only be used once.");
        }
        if (endSeparatorCount > 1) {
            throw new LumiException("the /to parameter can only be used once.");
        }

        int startSeparatorPosition = findSeparator(details, EVENT_START_SEPARATOR, 0);
        int endSeparatorPosition = findSeparator(details, TO_SEPARATOR, 0);
        if (startSeparatorPosition < 0 || endSeparatorPosition < 0) {
            throw new LumiException("an event needs start and end details. "
                    + "Try: event <description> /from <start> /to <end>");
        }
        if (endSeparatorPosition < startSeparatorPosition) {
            throw new LumiException("put /from before /to in an event.");
        }

        String description = details.substring(0, startSeparatorPosition).trim();
        String startDateText = details.substring(
                startSeparatorPosition + EVENT_START_SEPARATOR.length(), endSeparatorPosition).trim();
        String endDateText = details.substring(
                endSeparatorPosition + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new LumiException("an event needs a description before /from.");
        }
        if (startDateText.isEmpty()) {
            throw new LumiException("the /from value cannot be empty.");
        }
        if (endDateText.isEmpty()) {
            throw new LumiException("the /to value cannot be empty.");
        }
        LocalDateTime startDate = DateTimeParser.parseUserInput(startDateText);
        LocalDateTime endDate = DateTimeParser.parseUserInput(endDateText);
        if (!endDate.isAfter(startDate)) {
            throw new LumiException("an event's end must be later than its start.");
        }
        return new Event(description, startDate, endDate);
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command Complete find command entered by the user.
     * @return Non-empty keyword to search for.
     * @throws LumiException If the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws LumiException {
        String keyword = command.substring(CommandType.FIND.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new LumiException("tell me what to find. Try: find <keyword>");
        }
        return keyword;
    }

    /**
     * Extracts and validates the task number and new schedule from a snooze command.
     *
     * @param command Complete snooze command entered by the user.
     * @param taskCount Number of tasks currently stored.
     * @return Validated task index and replacement date or time.
     * @throws LumiException If the task number or replacement date is invalid.
     */
    public static SnoozeRequest parseSnoozeRequest(String command, int taskCount)
            throws LumiException {
        String details = extractCommandDetails(command, CommandType.SNOOZE);
        if (details.isEmpty()) {
            throw new LumiException("tell me which task to snooze. "
                    + "Try: snooze <task number> /to <when>");
        }

        if (countSeparators(details, TO_SEPARATOR) > 1) {
            throw new LumiException("the /to parameter can only be used once.");
        }
        int separatorPosition = findSeparator(details, TO_SEPARATOR, 0);
        if (separatorPosition < 0) {
            throw new LumiException("tell me when to snooze the task until. "
                    + "Try: snooze <task number> /to <when>");
        }

        String taskNumberText = details.substring(0, separatorPosition).trim();
        if (taskNumberText.isEmpty()) {
            throw new LumiException("tell me which task to snooze. "
                    + "Try: snooze <task number> /to <when>");
        }

        int taskIndex = parseTaskIndex("snooze " + taskNumberText,
                CommandType.SNOOZE, taskCount);
        String newDateTimeText = details.substring(separatorPosition + TO_SEPARATOR.length()).trim();
        if (newDateTimeText.isEmpty()) {
            throw new LumiException("the /to value cannot be empty.");
        }
        return new SnoozeRequest(taskIndex, DateTimeParser.parseUserInput(newDateTimeText));
    }

    /**
     * Converts a task command's user-facing number to a list index.
     *
     * @param command Complete command entered by the user.
     * @param commandType Type of task command, such as mark, unmark, or delete.
     * @param taskCount Number of tasks currently stored.
     * @return The zero-based index of the selected task.
     * @throws LumiException If the number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws LumiException {
        String action = commandType.getKeyword();
        String argument = command.substring(action.length()).trim();
        if (argument.isEmpty()) {
            throw new LumiException("tell me which task to " + action
                    + ". Try: " + action + " <task number>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException error) {
            throw new LumiException("the task number must be a whole number.");
        }

        if (taskCount == 0) {
            throw new LumiException("there are no tasks to " + action + " yet.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new LumiException("choose a task number from 1 to " + taskCount + ".");
        }
        assert taskNumber >= 1 && taskNumber <= taskCount
                : "Validated task numbers must be within the current task list";
        return taskNumber - 1;
    }

    /**
     * Finds a slash-prefixed separator only when it appears as a complete token.
     *
     * @param text Text containing task details.
     * @param separator Separator to find, such as {@code /by}.
     * @param startIndex Index from which to begin searching.
     * @return The separator index, or -1 if no complete separator token exists.
     */
    private static int findSeparator(String text, String separator, int startIndex) {
        int position = text.indexOf(separator, startIndex);
        while (position >= 0) {
            int afterSeparator = position + separator.length();
            boolean hasStartBoundary = position == 0
                    || Character.isWhitespace(text.charAt(position - 1));
            boolean hasEndBoundary = afterSeparator == text.length()
                    || Character.isWhitespace(text.charAt(afterSeparator));
            if (hasStartBoundary && hasEndBoundary) {
                return position;
            }
            position = text.indexOf(separator, position + 1);
        }
        return -1;
    }

    /** Counts complete separator tokens while ignoring separator-like description text. */
    private static int countSeparators(String text, String separator) {
        int count = 0;
        int position = findSeparator(text, separator, 0);
        while (position >= 0) {
            count++;
            position = findSeparator(text, separator, position + separator.length());
        }
        return count;
    }

    /**
     * Contains the validated details needed to reschedule a task.
     *
     * @param taskIndex Zero-based index of the task to reschedule.
     * @param newDateTime New date or time for the task.
     */
    public record SnoozeRequest(int taskIndex, LocalDateTime newDateTime) {
    }
}
