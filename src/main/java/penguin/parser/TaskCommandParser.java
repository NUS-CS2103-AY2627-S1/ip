package penguin.parser;

import java.time.LocalDateTime;
import java.util.Locale;

import penguin.enums.CommandType;
import penguin.exception.PenguinException;
import penguin.task.Deadline;
import penguin.task.Event;
import penguin.task.Task;
import penguin.task.Todo;
import penguin.util.DateTimeUtil;

/**
 * Parses task-creation commands into validated task objects.
 */
public final class TaskCommandParser {
    /** Keyword that separates a deadline description from its due date/time. */
    private static final String BY_KEYWORD = "/by";

    /** Keyword that separates an event description from its start date/time. */
    private static final String FROM_KEYWORD = "/from";

    /** Keyword that separates an event start from its end date/time. */
    private static final String TO_KEYWORD = "/to";

    /** Deadline separator including its required leading space. */
    private static final String BY_SEPARATOR = " " + BY_KEYWORD;

    /** Event-start separator including its required leading space. */
    private static final String FROM_SEPARATOR = " " + FROM_KEYWORD;

    /** Event-end separator including its required leading space. */
    private static final String TO_SEPARATOR = " " + TO_KEYWORD;

    /**
     * Prevents instantiation of this parser utility class.
     */
    private TaskCommandParser() {
    }

    /**
     * Parses a task-creation command.
     *
     * @param command complete task-creation command.
     * @return validated task.
     * @throws PenguinException if the command is null or invalid.
     */
    public static Task parse(String command) throws PenguinException {
        if (command == null) {
            throw new PenguinException("Please input a task.");
        }

        String lowerCaseCommand = command.toLowerCase(Locale.ROOT);
        if (isCommand(lowerCaseCommand, CommandType.TODO)) {
            return parseTodo(command);
        }
        if (isCommand(lowerCaseCommand, CommandType.DEADLINE)) {
            return parseDeadline(command);
        }
        if (isCommand(lowerCaseCommand, CommandType.EVENT)) {
            return parseEvent(command);
        }
        throw new PenguinException("I don't understand that command.");
    }

    /**
     * Checks whether input is a command or starts with the command and a space.
     *
     * @param input normalized command input.
     * @param commandType command type whose keyword should be checked.
     * @return true if the input equals the command keyword or starts with it
     *         followed by a space.
     */
    private static boolean isCommand(String input, CommandType commandType) {
        String keyword = commandType.getKeyword();
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }

    /**
     * Parses a to-do command.
     *
     * @param command complete to-do command.
     * @return newly created to-do task.
     * @throws PenguinException if the description is empty or contains a
     *                          forbidden character.
     */
    private static Task parseTodo(String command) throws PenguinException {
        String keyword = CommandType.TODO.getKeyword();
        String rawDescription = command.length() <= keyword.length()
                ? "" : command.substring(keyword.length()).trim();
        String description = rawDescription.replaceAll("\\s+", " ");

        if (description.isEmpty()) {
            throw new PenguinException("The description of a todo cannot be empty.");
        }
        validateDescriptionDelimiter(description);

        return new Todo(description);
    }

    /**
     * Parses a deadline command.
     *
     * @param command complete deadline command.
     * @return newly created deadline task.
     * @throws PenguinException if the format, description, or date/time is invalid.
     */
    private static Task parseDeadline(String command) throws PenguinException {
        String keyword = CommandType.DEADLINE.getKeyword();
        String content = extractTaskContent(command, keyword);
        String lowerCaseContent = content.toLowerCase(Locale.ROOT);

        validateDeadlineContent(content, lowerCaseContent);

        int separatorIndex = lowerCaseContent.indexOf(BY_SEPARATOR);
        String description = content.substring(0, separatorIndex).trim();
        String dateTimeInput = content.substring(
                separatorIndex + BY_SEPARATOR.length()).trim();

        if (description.isEmpty()) {
            throw new PenguinException("A deadline must have a description before " + BY_KEYWORD + ".");
        }

        if (dateTimeInput.isEmpty()) {
            throw new PenguinException("A deadline must have a date or time after " + BY_KEYWORD + ".");
        }
        validateDescriptionDelimiter(description);

        return new Deadline(description, parseFutureDateTime(dateTimeInput, keyword));
    }

    /**
     * Parses an event command.
     *
     * @param command complete event command.
     * @return newly created event task.
     * @throws PenguinException if the format, description, date/time, or ordering is invalid.
     */
    private static Task parseEvent(String command) throws PenguinException {
        String keyword = CommandType.EVENT.getKeyword();
        String content = extractTaskContent(command, keyword);
        String lowerCaseContent = content.toLowerCase(Locale.ROOT);

        validateEventDescriptionStart(lowerCaseContent);

        int fromIndex = lowerCaseContent.indexOf(FROM_SEPARATOR);
        int toIndex = lowerCaseContent.indexOf(TO_SEPARATOR);
        validateEventSeparators(lowerCaseContent, fromIndex, toIndex);

        String description = content.substring(0, fromIndex).trim();
        String fromInput = content.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String toInput = content.substring(toIndex + TO_SEPARATOR.length()).trim();

        validateEventDescription(description);
        validateDescriptionDelimiter(description);

        if (fromInput.isEmpty() || toInput.isEmpty()) {
            throw new PenguinException("An event must have both a start and an end time.");
        }

        LocalDateTime from = DateTimeUtil.parseDateTime(fromInput);
        LocalDateTime to = parseFutureDateTime(toInput, "event end");
        if (!to.isAfter(from)) {
            throw new PenguinException("The start time must be before the end time.");
        }

        return new Event(description, from, to);
    }

    /**
     * Extracts and normalizes the content after a task keyword.
     *
     * @param command complete task command.
     * @param keyword task command keyword.
     * @return normalized task content.
     */
    private static String extractTaskContent(String command, String keyword) {
        return command.length() <= keyword.length()
                ? "" : command.substring(keyword.length()).replaceAll("\\s+", " ");
    }

    /**
     * Validates that an event has exactly one ordered pair of separators.
     *
     * @param content lowercase event content.
     * @param fromIndex position of the from separator.
     * @param toIndex position of the to separator.
     * @throws PenguinException if the separators are missing, duplicated, or out of order.
     */
    private static void validateEventSeparators(
            String content, int fromIndex, int toIndex) throws PenguinException {
        boolean hasDuplicateSeparator = fromIndex != content.lastIndexOf(FROM_SEPARATOR)
                || toIndex != content.lastIndexOf(TO_SEPARATOR);
        if (fromIndex < 0 || toIndex < 0 || fromIndex > toIndex || hasDuplicateSeparator) {
            throw new PenguinException("An event must contain one " + FROM_KEYWORD
                    + " and one " + TO_KEYWORD + " separator.");
        }
    }

    /**
     * Parses a date/time and rejects dates before today.
     *
     * @param input date/time text.
     * @param itemName name used in validation errors.
     * @return validated date/time.
     * @throws PenguinException if the date/time is invalid or in the past.
     */
    private static LocalDateTime parseFutureDateTime(
            String input, String itemName) throws PenguinException {
        LocalDateTime dateTime = DateTimeUtil.parseDateTime(input);
        DateTimeUtil.validateNotBeforeToday(dateTime, itemName);
        return dateTime;
    }

    /**
     * Validates deadline content and separator rules.
     *
     * @param content deadline command content after the command keyword.
     * @param lowerCaseContent lowercase version of the command content.
     * @throws PenguinException if the description or separator rules are invalid.
     */
    private static void validateDeadlineContent(String content, String lowerCaseContent)
            throws PenguinException {
        String trimmedContent = lowerCaseContent.trim();
        if (content.trim().isEmpty() || trimmedContent.equals(BY_KEYWORD)
                || trimmedContent.startsWith(BY_KEYWORD + " ")) {
            throw new PenguinException("The description of a deadline cannot be empty.");
        }
        if (!lowerCaseContent.contains(BY_SEPARATOR)) {
            throw new PenguinException("A deadline must contain " + BY_KEYWORD
                    + " followed by a date or time.");
        }
        if (lowerCaseContent.indexOf(BY_SEPARATOR) != lowerCaseContent.lastIndexOf(BY_SEPARATOR)) {
            throw new PenguinException("A deadline must contain only one " + BY_KEYWORD
                    + " separator.");
        }
    }

    /**
     * Validates that an event does not begin with a separator.
     *
     * @param content lowercase event command content.
     * @throws PenguinException if the event description is empty.
     */
    private static void validateEventDescriptionStart(String content) throws PenguinException {
        if (content.startsWith(FROM_KEYWORD + " ") || content.startsWith(TO_KEYWORD + " ")) {
            throw new PenguinException("The description of an event cannot be empty.");
        }
    }

    /**
     * Validates that an event description is not empty.
     *
     * @param description event description.
     * @throws PenguinException if the description is empty.
     */
    private static void validateEventDescription(String description) throws PenguinException {
        if (description.isEmpty()) {
            throw new PenguinException("The description of an event cannot be empty.");
        }
    }

    /**
     * Rejects the delimiter reserved for persistent storage.
     *
     * @param description task description to validate.
     * @throws PenguinException if the description contains the storage delimiter.
     */
    private static void validateDescriptionDelimiter(String description)
            throws PenguinException {
        if (description.contains(Task.STORAGE_DELIMITER)) {
            throw new PenguinException("Task descriptions cannot contain the "
                    + Task.STORAGE_DELIMITER + " character.");
        }
    }

}
