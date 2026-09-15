package choo.parser;

import java.time.format.DateTimeParseException;

import choo.exception.ChooException;
import choo.task.Deadline;
import choo.task.Event;
import choo.task.Todo;

/**
 * Converts raw user input into structured commands.
 */
public class Parser {
    private Parser() {
    }

    /**
     * Parses a complete command line.
     *
     * @param command raw user command.
     * @return structured command.
     * @throws ChooException if the command or its details are invalid.
     */
    public static ParsedCommand parse(String command) throws ChooException {
        String trimmedCommand = command == null ? "" : command.trim();
        if (trimmedCommand.isEmpty()) {
            throw new ChooException("A command cannot be empty.");
        }
        if (isCommand(trimmedCommand, "bye")) {
            return parseCommandWithoutDetails(trimmedCommand, "bye", CommandType.BYE);
        }
        if (isCommand(trimmedCommand, "list")) {
            return parseCommandWithoutDetails(trimmedCommand, "list", CommandType.LIST);
        }
        if (isCommand(trimmedCommand, "sort")) {
            return parseCommandWithoutDetails(trimmedCommand, "sort", CommandType.SORT);
        }
        if (isCommand(trimmedCommand, "mark")) {
            return parseTaskNumber(trimmedCommand, "mark", CommandType.MARK);
        }
        if (isCommand(trimmedCommand, "unmark")) {
            return parseTaskNumber(trimmedCommand, "unmark", CommandType.UNMARK);
        }
        if (isCommand(trimmedCommand, "delete")) {
            return parseTaskNumber(trimmedCommand, "delete", CommandType.DELETE);
        }
        if (isCommand(trimmedCommand, "find")) {
            return parseFind(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "todo")) {
            return parseTodo(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "deadline")) {
            return parseDeadline(trimmedCommand);
        }
        if (isCommand(trimmedCommand, "event")) {
            return parseEvent(trimmedCommand);
        }
        throw new ChooException("I don't recognize that command.");
    }

    private static boolean isCommand(String command, String keyword) {
        return command.equals(keyword) || command.startsWith(keyword + " ");
    }

    /**
     * Parses a command that does not accept any arguments.
     *
     * @param command Complete trimmed command.
     * @param keyword Expected command keyword.
     * @param type Command type to include in the result.
     * @return Parsed command without details.
     * @throws ChooException If the command contains extra details.
     */
    private static ParsedCommand parseCommandWithoutDetails(String command, String keyword,
            CommandType type) throws ChooException {
        if (!command.equals(keyword)) {
            throw new ChooException("The " + keyword + " command does not accept extra details.");
        }
        return ParsedCommand.withoutDetails(type);
    }

    /**
     * Parses the one-based task position following a command keyword.
     *
     * @param command Complete trimmed command.
     * @param keyword Command keyword preceding the position.
     * @param type Command type to include in the result.
     * @return Parsed task-targeting command.
     * @throws ChooException If the position is not a whole number.
     */
    private static ParsedCommand parseTaskNumber(String command, String keyword,
            CommandType type) throws ChooException {
        String taskNumberText = command.substring(keyword.length()).trim();
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            return ParsedCommand.forTaskNumber(type, taskNumber);
        } catch (NumberFormatException exception) {
            throw new ChooException(
                    "Enter a whole-number task position after " + keyword + ".");
        }
    }

    /**
     * Parses a todo command and validates its description.
     *
     * @param command Complete trimmed todo command.
     * @return Parsed add command carrying a todo.
     * @throws ChooException If the description is empty.
     */
    private static ParsedCommand parseTodo(String command) throws ChooException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new ChooException("A todo needs a description.");
        }
        return ParsedCommand.forNewTask(new Todo(description));
    }

    /**
     * Parses a find command and validates its keyword.
     *
     * @param command Complete trimmed find command.
     * @return Parsed find command carrying a keyword.
     * @throws ChooException If the keyword is empty.
     */
    private static ParsedCommand parseFind(String command) throws ChooException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new ChooException("A find command needs a keyword.");
        }
        return ParsedCommand.forKeyword(keyword);
    }

    /**
     * Parses a deadline command and validates its description and due value.
     *
     * @param command Complete trimmed deadline command.
     * @return Parsed add command carrying a deadline.
     * @throws ChooException If required details or a valid date are missing.
     */
    private static ParsedCommand parseDeadline(String command) throws ChooException {
        String taskDetails = command.substring("deadline".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }

        int byDelimiterIndex = findDelimiter(taskDetails, "/by", 0);
        if (byDelimiterIndex < 0) {
            throw new ChooException("A deadline needs a /by date or time.");
        }
        if (findDelimiter(taskDetails, "/by", byDelimiterIndex + 3) >= 0) {
            throw new ChooException("A deadline needs exactly one /by value.");
        }

        String description = taskDetails.substring(0, byDelimiterIndex).trim();
        String dueDateTimeText = taskDetails.substring(byDelimiterIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("A deadline needs a description.");
        }
        if (dueDateTimeText.isEmpty()) {
            throw new ChooException("A deadline needs a /by date or time.");
        }
        try {
            return ParsedCommand.forNewTask(new Deadline(description, dueDateTimeText));
        } catch (DateTimeParseException exception) {
            throw new ChooException(
                    "Use yyyy-MM-dd or yyyy-MM-dd HHmm for a deadline date.");
        }
    }

    /**
     * Parses an event command and validates its description and time range.
     *
     * @param command Complete trimmed event command.
     * @return Parsed add command carrying an event.
     * @throws ChooException If required details are missing or misplaced.
     */
    private static ParsedCommand parseEvent(String command) throws ChooException {
        String taskDetails = command.substring("event".length()).trim();
        if (taskDetails.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }

        int fromDelimiterIndex = findDelimiter(taskDetails, "/from", 0);
        int toDelimiterIndex = findDelimiter(taskDetails, "/to", 0);
        if (fromDelimiterIndex < 0 || toDelimiterIndex < 0) {
            throw new ChooException("An event needs both /from and /to values.");
        }
        boolean hasRepeatedFrom = findDelimiter(taskDetails, "/from", fromDelimiterIndex + 5) >= 0;
        boolean hasRepeatedTo = findDelimiter(taskDetails, "/to", toDelimiterIndex + 3) >= 0;
        if (hasRepeatedFrom || hasRepeatedTo || toDelimiterIndex <= fromDelimiterIndex) {
            throw new ChooException(
                    "An event needs exactly one /from followed by exactly one /to value.");
        }

        String description = taskDetails.substring(0, fromDelimiterIndex).trim();
        String startText = taskDetails.substring(fromDelimiterIndex + 5, toDelimiterIndex).trim();
        String endText = taskDetails.substring(toDelimiterIndex + 3).trim();
        if (description.isEmpty()) {
            throw new ChooException("An event needs a description.");
        }
        if (startText.isEmpty() || endText.isEmpty()) {
            throw new ChooException("An event needs both /from and /to values.");
        }
        return ParsedCommand.forNewTask(new Event(description, startText, endText));
    }

    /**
     * Finds a standalone delimiter at or after a given position.
     *
     * @param text Text to search.
     * @param delimiter Reserved delimiter token.
     * @param startIndex Position at which to start searching.
     * @return Index of the delimiter, or -1 if it is absent.
     */
    private static int findDelimiter(String text, String delimiter, int startIndex) {
        int candidateIndex = text.indexOf(delimiter, startIndex);
        while (candidateIndex >= 0) {
            int delimiterEndIndex = candidateIndex + delimiter.length();
            boolean hasLeftBoundary = candidateIndex == 0
                    || Character.isWhitespace(text.charAt(candidateIndex - 1));
            boolean hasRightBoundary = delimiterEndIndex == text.length()
                    || Character.isWhitespace(text.charAt(delimiterEndIndex));
            if (hasLeftBoundary && hasRightBoundary) {
                return candidateIndex;
            }
            candidateIndex = text.indexOf(delimiter, delimiterEndIndex);
        }
        return -1;
    }
}
