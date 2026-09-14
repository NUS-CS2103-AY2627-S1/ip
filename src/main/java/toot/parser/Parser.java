package toot.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import toot.TootException;
import toot.task.Deadline;
import toot.task.Event;
import toot.task.Task;
import toot.task.Todo;

/**
 * Converts raw user input into commands, tasks, and validated task indexes.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Parses one raw line of user input.
     *
     * @param commandText Raw command entered by the user.
     * @return Recognized command and its trimmed arguments.
     * @throws TootException If the command is empty, unknown, or malformed at the command level.
     */
    public static ParsedCommand parse(String commandText) throws TootException {
        String command = commandText.trim();
        CommandType type = CommandType.from(command);
        return new ParsedCommand(type, type.getArguments(command));
    }

    /**
     * Creates the task described by an add-task command.
     *
     * @param command Parsed todo, deadline, or event command.
     * @return Task represented by the command.
     * @throws TootException If a required field is missing or a deadline date is invalid.
     */
    public static Task parseTask(ParsedCommand command) throws TootException {
        String arguments = command.arguments();
        switch (command.type()) {
            case EVENT:
                return parseEvent(arguments);
            case DEADLINE:
                return parseDeadline(arguments);
            case TODO:
                if (arguments.isEmpty()) {
                    throw new TootException("The todo description cannot be empty. Try: todo DESCRIPTION");
                }
                return new Todo(arguments);
            default:
                throw new IllegalArgumentException("Cannot create a task from command type: " + command.type());
        }
    }

    /**
     * Returns the keyword from a find command.
     *
     * @param command Parsed find command.
     * @return Keyword to search for in task descriptions.
     * @throws TootException If the keyword is empty.
     */
    public static String parseFindKeyword(ParsedCommand command) throws TootException {
        String keyword = command.arguments();
        if (keyword.isEmpty()) {
            throw new TootException("The find keyword cannot be empty. Try: find KEYWORD");
        }
        return keyword;
    }

    /**
     * Converts a user-facing task number to its zero-based list index.
     *
     * @param command Parsed mark, unmark, or delete command.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based index of an existing task.
     * @throws TootException If the number is missing, invalid, or outside the task list.
     */
    public static int parseTaskIndex(ParsedCommand command, int taskCount) throws TootException {
        String taskNumberText = command.arguments();
        String action = command.type().getKeyword();
        if (taskNumberText.isEmpty()) {
            throw new TootException("Toot needs a task number after '" + action + "'. Try: " + action + " 1");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new TootException("The task number for '" + action + "' must be a whole number. "
                    + "Try: " + action + " 1");
        }

        if (taskCount == 0) {
            throw new TootException("The task list is empty, so there is nothing to " + action + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new TootException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Parses an event description and its two time fields.
     */
    private static Event parseEvent(String arguments) throws TootException {
        int fromIndex = findDelimiter(arguments, "/from", 0);
        if (fromIndex < 0) {
            throw new TootException("An event needs '/from' before its start time. "
                    + "Try: event DESCRIPTION /from START /to END");
        }
        int toIndex = findDelimiter(arguments, "/to", fromIndex + "/from".length());
        if (toIndex < 0) {
            throw new TootException("An event needs '/to' before its end time. "
                    + "Try: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + "/from".length(), toIndex).trim();
        String to = arguments.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            throw new TootException("The event description cannot be empty. "
                    + "Try: event DESCRIPTION /from START /to END");
        }
        if (from.isEmpty()) {
            throw new TootException("The event start time cannot be empty after '/from'.");
        }
        if (to.isEmpty()) {
            throw new TootException("The event end time cannot be empty after '/to'.");
        }
        if (findDelimiter(from, "/from", 0) >= 0 || findDelimiter(to, "/to", 0) >= 0) {
            throw new TootException("An event can have only one '/from' and one '/to'.");
        }
        return new Event(description, from, to);
    }

    /**
     * Parses a deadline description and its ISO calendar date.
     */
    private static Deadline parseDeadline(String arguments) throws TootException {
        int byIndex = findDelimiter(arguments, "/by", 0);
        if (byIndex < 0) {
            throw new TootException("A deadline needs '/by' before its due date. "
                    + "Try: deadline DESCRIPTION /by YYYY-MM-DD");
        }
        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            throw new TootException("The deadline description cannot be empty. "
                    + "Try: deadline DESCRIPTION /by YYYY-MM-DD");
        }
        if (by.isEmpty()) {
            throw new TootException("The deadline date cannot be empty after '/by'.");
        }
        if (findDelimiter(by, "/by", 0) >= 0) {
            throw new TootException("A deadline can have only one '/by'.");
        }
        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException exception) {
            throw new TootException("The deadline date must use YYYY-MM-DD and be a real calendar date. "
                    + "Try: deadline DESCRIPTION /by 2019-12-02");
        }
    }

    /**
     * Finds a slash-delimiter that appears as a separate token in command text.
     */
    private static int findDelimiter(String text, String delimiter, int fromIndex) {
        int delimiterIndex = text.indexOf(delimiter, fromIndex);
        while (delimiterIndex >= 0) {
            int afterDelimiter = delimiterIndex + delimiter.length();
            boolean hasTokenStart = delimiterIndex == 0 || Character.isWhitespace(text.charAt(delimiterIndex - 1));
            boolean hasTokenEnd = afterDelimiter == text.length()
                    || Character.isWhitespace(text.charAt(afterDelimiter));
            if (hasTokenStart && hasTokenEnd) {
                return delimiterIndex;
            }
            delimiterIndex = text.indexOf(delimiter, delimiterIndex + 1);
        }
        return -1;
    }
}
