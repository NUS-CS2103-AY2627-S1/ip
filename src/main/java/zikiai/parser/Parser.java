package zikiai.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import zikiai.exception.ZikiaiException;
import zikiai.task.Deadline;
import zikiai.task.Event;
import zikiai.task.Priority;
import zikiai.task.Todo;

/**
 * Recognizes commands and converts their text into validated values and tasks.
 */
public class Parser {
    private static final String BYE_COMMAND = "bye";
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String PRIORITY_COMMAND = "priority";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final String TODO_FORMAT_MESSAGE =
            "Use todo DESCRIPTION, for example todo read book.";
    private static final String DEADLINE_FORMAT_MESSAGE =
            "Use deadline DESCRIPTION /by yyyy-MM-dd, for example "
                    + "deadline submit report /by 2026-09-30.";
    private static final String EVENT_FORMAT_MESSAGE =
            "Use event DESCRIPTION /from START /to END, for example "
                    + "event meeting /from 2pm /to 4pm.";
    private static final String FIND_FORMAT_MESSAGE =
            "Use find KEYWORD, for example find book.";

    /**
     * Creates a parser for recognizing and validating Zikiai commands.
     */
    public Parser() {
    }

    /**
     * Returns whether the user wants to exit.
     *
     * @param input complete user input.
     * @return true for the bye command.
     */
    public boolean isByeCommand(String input) {
        return input.equals(BYE_COMMAND);
    }

    /**
     * Returns whether the input begins a mark command.
     *
     * @param input complete user input.
     * @return true for a mark command, including a malformed one.
     */
    public boolean isMarkCommand(String input) {
        return isCommandWithOptionalArguments(input, MARK_COMMAND);
    }

    /**
     * Returns whether the input begins an unmark command.
     *
     * @param input complete user input.
     * @return true for an unmark command, including a malformed one.
     */
    public boolean isUnmarkCommand(String input) {
        return isCommandWithOptionalArguments(input, UNMARK_COMMAND);
    }

    /**
     * Returns whether the input begins a delete command.
     *
     * @param input complete user input.
     * @return true for a delete command, including a malformed one.
     */
    public boolean isDeleteCommand(String input) {
        return isCommandWithOptionalArguments(input, DELETE_COMMAND);
    }

    /**
     * Returns whether the user requested the task list.
     *
     * @param input complete user input.
     * @return true for the list command.
     */
    public boolean isListCommand(String input) {
        return input.equals(LIST_COMMAND);
    }

    /**
     * Returns whether the input begins a find command.
     *
     * @param input complete user input.
     * @return true for a find command, including an empty one.
     */
    public boolean isFindCommand(String input) {
        return isCommandWithOptionalArguments(input, FIND_COMMAND);
    }

    /**
     * Returns whether the input begins a todo command.
     *
     * @param input complete user input.
     * @return true for a todo command, including an empty one.
     */
    public boolean isTodoCommand(String input) {
        return isCommandWithOptionalArguments(input, TODO_COMMAND);
    }

    /**
     * Returns whether the input begins a deadline command.
     *
     * @param input complete user input.
     * @return true for a deadline command, including an empty one.
     */
    public boolean isDeadlineCommand(String input) {
        return isCommandWithOptionalArguments(input, DEADLINE_COMMAND);
    }

    /**
     * Returns whether the input begins an event command.
     *
     * @param input complete user input.
     * @return true for an event command, including an empty one.
     */
    public boolean isEventCommand(String input) {
        return isCommandWithOptionalArguments(input, EVENT_COMMAND);
    }

    /**
     * Returns whether the input begins a priority command.
     *
     * @param input complete user input.
     * @return true for a priority command, including an incomplete one.
     */
    public boolean isPriorityCommand(String input) {
        return isCommandWithOptionalArguments(input, PRIORITY_COMMAND);
    }

    /**
     * Extracts and validates the one-based task number in a command.
     *
     * @param input command containing a task number.
     * @param taskCount current number of tasks.
     * @return corresponding zero-based task index.
     * @throws ZikiaiException if the number is too large or does not identify a task.
     */
    public int parseTaskIndex(String input, int taskCount) throws ZikiaiException {
        assert input != null
                && (isMarkCommand(input) || isUnmarkCommand(input) || isDeleteCommand(input))
                : "Input must be a task command requiring a number";
        assert taskCount >= 0 : "Task count must not be negative";

        String[] parts = input.trim().split("\\s+");
        String command = parts[0];
        if (parts.length != 2 || !parts[1].matches("\\d+")) {
            throw invalidNumberedCommand(command);
        }
        return parseTaskNumber(parts[1], taskCount);
    }

    /**
     * Extracts and validates the one-based task number in a priority command.
     *
     * @param input complete priority command.
     * @param taskCount current number of tasks.
     * @return corresponding zero-based task index.
     * @throws ZikiaiException if the command or task number is invalid.
     */
    public int parsePriorityTaskIndex(String input, int taskCount) throws ZikiaiException {
        assert taskCount >= 0 : "Task count must not be negative";
        String[] parts = parsePriorityParts(input);
        if (!parts[1].matches("\\d+")) {
            throw invalidPriorityCommand();
        }
        return parseTaskNumber(parts[1], taskCount);
    }

    /**
     * Extracts and validates the priority level in a priority command.
     *
     * @param input complete priority command.
     * @return requested priority.
     * @throws ZikiaiException if the command contains an unknown priority.
     */
    public Priority parsePriority(String input) throws ZikiaiException {
        String[] parts = parsePriorityParts(input);
        try {
            return Priority.valueOf(parts[2].toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException exception) {
            throw invalidPriorityCommand();
        }
    }

    /**
     * Converts a one-based task number into a validated zero-based index.
     */
    private int parseTaskNumber(String numberText, int taskCount) throws ZikiaiException {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ZikiaiException("That task number is too large.");
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new ZikiaiException("That task number does not exist.");
        }
        return taskIndex;
    }

    /**
     * Splits a priority command into its command, task number, and level.
     */
    private String[] parsePriorityParts(String input) throws ZikiaiException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length != 3 || !parts[0].equals(PRIORITY_COMMAND)) {
            throw invalidPriorityCommand();
        }
        return parts;
    }

    /**
     * Creates the consistent error used for malformed priority commands.
     */
    private ZikiaiException invalidPriorityCommand() {
        return new ZikiaiException(
                "Use priority TASK_NUMBER with high, medium, low, or none.");
    }

    /**
     * Creates an actionable format error for a command requiring one task number.
     */
    private ZikiaiException invalidNumberedCommand(String command) {
        return new ZikiaiException(
                "Use " + command + " TASK_NUMBER, for example " + command + " 1.");
    }

    /**
     * Extracts and validates the keyword in a find command.
     *
     * @param input complete find command.
     * @return keyword to search for.
     * @throws ZikiaiException if the keyword is empty.
     */
    public String parseFindKeyword(String input) throws ZikiaiException {
        String keyword = input.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new ZikiaiException(FIND_FORMAT_MESSAGE);
        }
        return keyword;
    }

    /**
     * Creates a todo from a validated command.
     *
     * @param input complete todo command.
     * @return parsed todo.
     * @throws ZikiaiException if its description is empty or cannot be stored safely.
     */
    public Todo parseTodo(String input) throws ZikiaiException {
        if (input.equals(TODO_COMMAND)) {
            throw new ZikiaiException(TODO_FORMAT_MESSAGE);
        }

        String description = input.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new ZikiaiException(TODO_FORMAT_MESSAGE);
        }
        validateStorageText(description);
        return new Todo(description);
    }

    /**
     * Creates a deadline from a validated command.
     *
     * @param input complete deadline command.
     * @return parsed deadline task.
     * @throws ZikiaiException if required fields or a valid ISO date are missing.
     */
    public Deadline parseDeadline(String input) throws ZikiaiException {
        if (input.equals(DEADLINE_COMMAND)) {
            throw new ZikiaiException(DEADLINE_FORMAT_MESSAGE);
        }

        String details = input.substring(DEADLINE_COMMAND.length()).trim();
        int byIndex = details.indexOf(BY_MARKER);
        if (byIndex == -1 || byIndex != details.lastIndexOf(BY_MARKER)) {
            throw new ZikiaiException(DEADLINE_FORMAT_MESSAGE);
        }

        String description = details.substring(0, byIndex).trim();
        String deadlineText = details.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || deadlineText.isEmpty()) {
            throw new ZikiaiException(DEADLINE_FORMAT_MESSAGE);
        }
        validateStorageText(description, deadlineText);
        return new Deadline(description, parseDeadlineDate(deadlineText));
    }

    /**
     * Creates an event from a validated command.
     *
     * @param input complete event command.
     * @return parsed event task.
     * @throws ZikiaiException if required fields are missing or cannot be stored safely.
     */
    public Event parseEvent(String input) throws ZikiaiException {
        if (input.equals(EVENT_COMMAND)) {
            throw new ZikiaiException(EVENT_FORMAT_MESSAGE);
        }

        String details = input.substring(EVENT_COMMAND.length()).trim();
        int fromIndex = details.indexOf(FROM_MARKER);
        int toIndex = details.indexOf(TO_MARKER);
        boolean hasInvalidMarkers = fromIndex == -1 || toIndex == -1
                || fromIndex > toIndex
                || fromIndex != details.lastIndexOf(FROM_MARKER)
                || toIndex != details.lastIndexOf(TO_MARKER);
        if (hasInvalidMarkers) {
            throw new ZikiaiException(EVENT_FORMAT_MESSAGE);
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new ZikiaiException(EVENT_FORMAT_MESSAGE);
        }
        validateStorageText(description, from, to);
        return new Event(description, from, to);
    }

    /**
     * Returns whether the input is a command with or without arguments.
     */
    private boolean isCommandWithOptionalArguments(String input, String command) {
        return input.equals(command)
                || (input.startsWith(command)
                        && input.length() > command.length()
                        && Character.isWhitespace(input.charAt(command.length())));
    }

    /**
     * Parses a deadline written in the ISO yyyy-MM-dd format.
     *
     * @param deadlineText deadline supplied by the user.
     * @return parsed deadline date.
     * @throws ZikiaiException if the text is not a valid ISO date.
     */
    private LocalDate parseDeadlineDate(String deadlineText) throws ZikiaiException {
        try {
            return LocalDate.parse(deadlineText);
        } catch (DateTimeParseException exception) {
            throw new ZikiaiException(
                    "Please enter the deadline as yyyy-MM-dd, for example 2026-08-23.",
                    exception);
        }
    }

    /**
     * Rejects the field separator because it would make saved data ambiguous.
     * Varargs lets the same check accept one todo field, two deadline fields,
     * or three event fields without separate overloads or caller-created arrays.
     *
     * @param values task fields to validate; every supplied field is checked.
     * @throws ZikiaiException if any field contains the reserved separator.
     */
    private void validateStorageText(String... values) throws ZikiaiException {
        for (String value : values) {
            if (value.contains("|")) {
                throw new ZikiaiException("Task details cannot contain the | character.");
            }
        }
    }
}
