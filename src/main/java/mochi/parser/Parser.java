package mochi.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mochi.command.Command;
import mochi.exception.MochiException;
import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.Todo;

/**
 * Interprets user input and validates command parameters.
 */
public class Parser {
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";

    /**
     * Identifies the command represented by the user's input.
     *
     * @param input complete input entered by the user
     * @return parsed command type
     */
    public static Command parseCommand(String input) {
        return Command.fromInput(input);
    }

    /**
     * Converts a task-creation command into the corresponding task type.
     *
     * @param input complete command entered by the user
     * @param command type of task-creation command
     * @return parsed todo, deadline, or event
     * @throws MochiException if the command or any required field is invalid
     */
    public static Task parseTask(String input, Command command) throws MochiException {
        input = input.trim();
        switch (command) {
            case TODO:
                return parseTodo(input);
            case DEADLINE:
                return parseDeadline(input);
            case EVENT:
                return parseEvent(input);
            default:
                throw new MochiException("That command does not create a task.");
        }
    }

    /**
     * Parses and validates the details of a todo command.
     *
     * @param input complete task command
     * @return parsed task
     * @throws MochiException if required details are invalid
     */
    private static Task parseTodo(String input) throws MochiException {
        String description = getCommandDetails(input, Command.TODO);
        if (description.isEmpty()) {
            throw new MochiException("A todo needs a description.");
        }
        validateDescription(description);
        return new Todo(description);
    }

    /**
     * Parses and validates the details of a deadline command.
     *
     * @param input complete task command
     * @return parsed task
     * @throws MochiException if required details are invalid
     */
    private static Task parseDeadline(String input) throws MochiException {
        int byIndex = findMarker(input, DEADLINE_SEPARATOR, 0);
        if (byIndex < 0) {
            throw new MochiException("A deadline needs '/by' followed by a date or time.");
        }
        String description = input.substring(Command.DEADLINE.getKeyword().length(), byIndex).trim();
        String by = input.substring(byIndex + DEADLINE_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new MochiException("A deadline needs a description before '/by'.");
        }
        if (by.isEmpty()) {
            throw new MochiException("A deadline needs a date or time after '/by'.");
        }
        if (findMarker(input, DEADLINE_SEPARATOR, byIndex + DEADLINE_SEPARATOR.length()) >= 0) {
            throw new MochiException("A deadline must contain '/by' only once.");
        }
        validateDescription(description);
        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException e) {
            throw new MochiException("Use yyyy-MM-dd for deadline dates, for example: 2026-08-30.");
        }
    }

    /**
     * Parses and validates the details of a event command.
     *
     * @param input complete task command
     * @return parsed task
     * @throws MochiException if required details are invalid
     */
    private static Task parseEvent(String input) throws MochiException {
        int fromIndex = findMarker(input, EVENT_FROM_SEPARATOR, 0);
        int toIndex = findMarker(input, EVENT_TO_SEPARATOR, 0);
        if (fromIndex < 0 || toIndex < fromIndex) {
            throw new MochiException("An event needs both '/from' and '/to' date or time values.");
        }
        String description = input.substring(Command.EVENT.getKeyword().length(), fromIndex).trim();
        String from = input.substring(fromIndex + EVENT_FROM_SEPARATOR.length(), toIndex).trim();
        String to = input.substring(toIndex + EVENT_TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new MochiException("An event needs a description before '/from'.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new MochiException("An event needs values after both '/from' and '/to'.");
        }
        if (findMarker(input, EVENT_FROM_SEPARATOR, fromIndex + EVENT_FROM_SEPARATOR.length()) >= 0
                || findMarker(input, EVENT_TO_SEPARATOR, toIndex + EVENT_TO_SEPARATOR.length()) >= 0) {
            throw new MochiException("An event must contain '/from' and '/to' only once each.");
        }
        validateDescription(description);
        validateField(from, "Event times");
        validateField(to, "Event times");
        validateEventOrder(from, to);
        return new Event(description, from, to);
    }

    /** Finds a complete marker token, allowing spaces or tabs on either side. */
    private static int findMarker(String input, String marker, int startIndex) {
        Matcher matcher = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?=\\s|$)").matcher(input);
        return matcher.find(startIndex) ? matcher.start() : -1;
    }

    /** Checks calendar dates when supplied, while preserving free-form event times. */
    private static void validateEventOrder(String from, String to) throws MochiException {
        if (from.equalsIgnoreCase(to)) {
            throw new MochiException("An event must end after it starts.");
        }
        LocalDate start = parseEventDate(from);
        LocalDate end = parseEventDate(to);
        if (start != null && end != null && !end.isAfter(start)) {
            throw new MochiException("An event must end after it starts.");
        }
    }

    /** Validates ISO-shaped event dates; other event values remain free-form text. */
    private static LocalDate parseEventDate(String value) throws MochiException {
        if (!value.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new MochiException("Use a real yyyy-MM-dd date for events, for example: 2026-09-18.");
        }
    }

    /**
     * Rejects descriptions that cannot be stored safely or displayed clearly.
     *
     * @param description task description to validate
     * @throws MochiException if the description contains unsupported content
     */
    private static void validateDescription(String description) throws MochiException {
        validateField(description, "Task descriptions");
    }

    /** Rejects characters that would split a stored record into extra fields or lines. */
    private static void validateField(String value, String fieldName) throws MochiException {
        if (value.indexOf('|') >= 0) {
            throw new MochiException(fieldName + " cannot contain the '|' character.");
        }
        if (value.chars().anyMatch(Character::isISOControl)) {
            throw new MochiException(fieldName + " cannot contain control characters.");
        }
    }

    private static String getCommandDetails(String input, Command command) {
        return input.substring(command.getKeyword().length()).trim();
    }

    /**
     * Extracts and validates the task number in a mark, unmark, or delete command.
     *
     * @param input complete command entered by the user
     * @param command command type: mark, unmark, or delete
     * @param taskCount number of tasks currently stored
     * @return zero-based index of the selected task
     * @throws MochiException if the task number is missing, nonnumeric, or outside the list
     */
    public static int parseTaskIndex(String input, Command command, int taskCount) throws MochiException {
        input = input.trim();
        assert taskCount >= 0 : "The task count must never be negative";
        assert command == Command.MARK || command == Command.UNMARK || command == Command.DELETE
                : "Task-index parsing requires a mark, unmark, or delete command";
        String commandWord = command.getKeyword();
        String taskNumber = input.substring(commandWord.length()).trim();
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException e) {
            throw new MochiException(
                    "Tell me which task to " + commandWord + " using a number, for example: " + commandWord + " 2");
        }
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new MochiException("That task number is not in your list.");
        }
        return taskIndex;
    }

    /**
     * Extracts the required search keyword from a find command.
     *
     * @param input complete find command entered by the user
     * @return nonempty search keyword
     * @throws MochiException if no keyword was supplied
     */
    public static String parseFindKeyword(String input) throws MochiException {
        input = input.trim();
        String keyword = input.substring(Command.FIND.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new MochiException("Tell me what to find, for example: find book");
        }
        return keyword;
    }
}
