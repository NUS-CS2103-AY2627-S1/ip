package panda.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import panda.exception.ErrorType;
import panda.exception.PandaException;
import panda.model.CommandType;
import panda.model.Deadline;
import panda.model.Event;
import panda.model.Task;
import panda.model.Todo;
import panda.util.DateTimeUtil;

/**
 * Converts user input into Panda commands and task objects.
 */
public class Parser {
    /**
     * Parses a command string and returns the matching task type.
     *
     * @param command raw user input
     * @return the recognized command type
     */
    public static CommandType getCommandType(String command) {
        if (command == null) {
            return CommandType.UNKNOWN;
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            return CommandType.TODO;
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        }
        if (command.equals("event") || command.startsWith("event ")) {
            return CommandType.EVENT;
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (command.equals("mark") || command.startsWith("mark ")) {
            return CommandType.MARK;
        }
        if (command.equals("unmark") || command.startsWith("unmark ")) {
            return CommandType.UNMARK;
        }
        if (command.equals("delete") || command.startsWith("delete ")) {
            return CommandType.DELETE;
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return CommandType.FIND;
        }
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Builds a task from a complete create-task command.
     *
     * @param command raw user input
     * @return the task described by the command
     * @throws PandaException if the task data is invalid
     */
    public static Task createTask(String command) throws PandaException {
        validateCommandFormat(command);
        switch (getCommandType(command)) {
        case TODO:
            String description = getArguments(command, "todo");
            if (description.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_TODO_DESCRIPTION);
            }
            validateDescription(description);
            rejectUnexpectedParameter(description, "/by");
            rejectUnexpectedParameter(description, "/from");
            rejectUnexpectedParameter(description, "/to");
            return new Todo(description);
        case DEADLINE:
            String details = getArguments(command, "deadline");
            if (details.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_DEADLINE_DESCRIPTION);
            }

            int byIndex = findMarker(details, "/by");
            if (byIndex < 0) {
                throw new PandaException(ErrorType.MISSING_DEADLINE_BY);
            }
            rejectDuplicateParameter(details, "/by");
            rejectUnexpectedParameter(details, "/from");
            rejectUnexpectedParameter(details, "/to");

            String deadlineDescription = details.substring(0, byIndex).trim();
            String by = details.substring(byIndex + 3).trim();
            if (deadlineDescription.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_DEADLINE_DESCRIPTION);
            }
            validateDescription(deadlineDescription);
            if (by.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_DEADLINE_BY);
            }
            return new Deadline(deadlineDescription, parseDateTime(by));
        case EVENT:
            String eventDetails = getArguments(command, "event");
            if (eventDetails.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_EVENT_DESCRIPTION);
            }

            int fromIndex = findMarker(eventDetails, "/from");
            int toIndex = findMarker(eventDetails, "/to", Math.max(0, fromIndex + 5));
            if (fromIndex < 0 || toIndex < 0) {
                throw new PandaException(ErrorType.MISSING_EVENT_TIMES);
            }
            rejectDuplicateParameter(eventDetails, "/from");
            rejectDuplicateParameter(eventDetails, "/to");
            rejectUnexpectedParameter(eventDetails, "/by");

            String eventDescription = eventDetails.substring(0, fromIndex).trim();
            String from = eventDetails.substring(fromIndex + 5, toIndex).trim();
            String to = eventDetails.substring(toIndex + 3).trim();
            if (eventDescription.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_EVENT_DESCRIPTION);
            }
            validateDescription(eventDescription);
            if (from.isEmpty() || to.isEmpty()) {
                throw new PandaException(ErrorType.EMPTY_EVENT_TIME);
            }
            LocalDateTime eventStart = parseDateTime(from);
            LocalDateTime eventEnd = parseDateTime(to);
            if (!eventEnd.isAfter(eventStart)) {
                throw new PandaException(ErrorType.EVENT_END_NOT_AFTER_START);
            }
            assert eventEnd.isAfter(eventStart)
                    : "An event's end date/time must be after its start date/time";
            return new Event(eventDescription, eventStart, eventEnd);
        default:
            throw new PandaException(ErrorType.UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses a date/time value from the user input.
     *
     * @param value the raw date/time string
     * @return a parsed LocalDateTime
     * @throws PandaException if the date/time is invalid
     */
    public static LocalDateTime parseDateTime(String value) throws PandaException {
        try {
            return DateTimeUtil.parse(value);
        } catch (DateTimeParseException e) {
            throw new PandaException(ErrorType.INVALID_DATE_TIME);
        }
    }

    /**
     * Parses the zero-based index for a task operation.
     *
     * @param command raw command text
     * @param action command action name
     * @param taskCount number of tasks currently in the list
     * @return the validated task index
     * @throws PandaException if the index is invalid
     */
    public static int getTaskIndex(String command, String action, int taskCount) throws PandaException {
        validateCommandFormat(command);
        String taskNumber = getArguments(command, action);
        if (taskNumber.isEmpty()) {
            throw new PandaException(ErrorType.MISSING_TASK_NUMBER, action);
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException e) {
            throw new PandaException(ErrorType.NON_NUMERIC_TASK_NUMBER);
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new PandaException(ErrorType.TASK_NOT_FOUND);
        }
        assert taskIndex >= 0 && taskIndex < taskCount
                : "A validated task index must identify an existing task";
        return taskIndex;
    }

    /**
     * Extracts the text that follows the command name.
     *
     * @param command raw command text
     * @param name command word
     * @return the arguments, trimmed of leading/trailing spaces
     */
    public static String getArguments(String command, String name) {
        return command.length() == name.length() ? "" : command.substring(name.length()).trim();
    }

    /**
     * Rejects empty commands and command lines that do not use single-space separation.
     *
     * @param command raw user input
     * @throws PandaException if the command format is invalid
     */
    public static void validateCommandFormat(String command) throws PandaException {
        if (command == null || command.isEmpty()) {
            throw new PandaException(ErrorType.EMPTY_COMMAND);
        }
        if (!command.equals(command.trim()) || command.matches(".* {2,}.*")
                || command.matches(".*[\\t\\n\\x0B\\f\\r].*")) {
            throw new PandaException(ErrorType.INVALID_COMMAND_FORMAT);
        }
    }

    private static void validateDescription(String description) throws PandaException {
        if (description.contains("|")) {
            throw new PandaException(ErrorType.INVALID_DESCRIPTION);
        }
    }

    private static void rejectDuplicateParameter(String text, String parameter) throws PandaException {
        if (countMarker(text, parameter) > 1) {
            throw new PandaException(ErrorType.DUPLICATE_PARAMETER, parameter);
        }
    }

    private static void rejectUnexpectedParameter(String text, String parameter) throws PandaException {
        if (findMarker(text, parameter) >= 0) {
            throw new PandaException(ErrorType.UNEXPECTED_PARAMETER, parameter);
        }
    }

    private static int countMarker(String text, String marker) {
        int count = 0;
        int index = findMarker(text, marker);
        while (index >= 0) {
            count++;
            index = findMarker(text, marker, index + marker.length());
        }
        return count;
    }

    /**
     * Finds a marker token in a phrase while preserving the expected whitespace boundaries.
     *
     * @param text string to search
     * @param marker marker to search for
     * @return the index of the marker, or -1 if absent
     */
    public static int findMarker(String text, String marker) {
        return findMarker(text, marker, 0);
    }

    /**
     * Finds a marker token in a phrase while preserving the expected whitespace boundaries.
     *
     * @param text string to search
     * @param marker marker to search for
     * @param startIndex index to begin scanning from
     * @return the index of the marker, or -1 if absent
     */
    public static int findMarker(String text, String marker, int startIndex) {
        int index = text.indexOf(marker, startIndex);
        while (index >= 0) {
            int markerEnd = index + marker.length();
            boolean hasBoundaryBefore = index == 0 || Character.isWhitespace(text.charAt(index - 1));
            boolean hasBoundaryAfter = markerEnd == text.length()
                    || Character.isWhitespace(text.charAt(markerEnd));
            if (hasBoundaryBefore && hasBoundaryAfter) {
                return index;
            }
            index = text.indexOf(marker, markerEnd);
        }
        return -1;
    }
}
