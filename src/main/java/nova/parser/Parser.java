package nova.parser;

import java.time.LocalDate;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.EditCommand;
import nova.command.EditField;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.FindDateCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.UnmarkCommand;
import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.TaskDateTime;
import nova.task.Todo;

/**
 * Interprets and validates commands entered by the user.
 */
public class Parser {
    private static final String MARKER_DEADLINE = "/by";
    private static final String MARKER_EVENT_START = "/from";
    private static final String MARKER_EVENT_END = "/to";
    private static final String MARKER_DESCRIPTION = "/description";

    /**
     * Converts a complete line of user input into an executable command.
     *
     * @param fullCommand complete command entered by the user.
     * @return command containing the parsed arguments.
     * @throws NovaException if the command or any of its arguments is invalid.
     */
    public Command parse(String fullCommand) throws NovaException {
        CommandType commandType = parseCommandType(fullCommand);
        String arguments = commandType.extractArguments(fullCommand);
        return switch (commandType) {
            case LIST -> new ListCommand();
            case TODO -> new AddCommand(parseTodo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case FIND -> new FindCommand(parseSearchKeyword(arguments));
            case ON -> new FindDateCommand(parseSearchDate(arguments));
            case MARK -> new MarkCommand(parseTaskNumber(arguments, commandType.getKeyword()));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments, commandType.getKeyword()));
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments, commandType.getKeyword()));
            case EDIT -> parseEdit(arguments);
            case BYE -> new ExitCommand();
            default -> throw new IllegalStateException("Unsupported command type: " + commandType);
        };
    }

    /**
     * Identifies the type of a complete command.
     *
     * @param command trimmed command entered by the user.
     * @return matching command type.
     * @throws NovaException if the command is blank or has an unknown form.
     */
    private CommandType parseCommandType(String command) throws NovaException {
        if (command.isEmpty()) {
            throw new NovaException("You entered a blank command. Try todo, deadline, event, find, on, list, "
                    + "mark, unmark, delete, edit, or bye.");
        }

        for (CommandType commandType : CommandType.values()) {
            if (commandType.matches(command)) {
                return commandType;
            }
        }

        throw new NovaException("I don't recognize that command. Start with todo, deadline, event, find, on, "
                + "list, mark, unmark, delete, edit, or bye.");
    }

    /**
     * Creates a todo from a command after validating its description.
     *
     * @param arguments text following the todo keyword.
     * @return the parsed todo.
     * @throws NovaException if the description is empty.
     */
    private Task parseTodo(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("A todo needs a description. Try: todo <description>.");
        }
        return new Todo(arguments);
    }

    /**
     * Creates a deadline from a command after validating its description and due time.
     *
     * @param arguments text following the deadline keyword.
     * @return the parsed deadline.
     * @throws NovaException if a required deadline field is missing.
     */
    private Task parseDeadline(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("A deadline needs a description. "
                    + "Try: deadline <description> /by <date or time>.");
        }

        int bySeparator = findMarker(arguments, MARKER_DEADLINE);
        if (bySeparator < 0) {
            throw new NovaException("A deadline needs a /by date or time. "
                    + "Try: deadline " + arguments + " /by <date or time>.");
        }

        String description = arguments.substring(0, bySeparator).trim();
        String dueDateTimeText = arguments.substring(bySeparator + MARKER_DEADLINE.length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("A deadline needs a description before /by.");
        }
        if (dueDateTimeText.isEmpty()) {
            throw new NovaException("The /by field cannot be empty. Add a date or time after /by.");
        }

        TaskDateTime.ParsedValue dueDateTime = TaskDateTime.parse(dueDateTimeText, MARKER_DEADLINE);
        return new Deadline(description, dueDateTime.dateTime(), dueDateTime.hasTime());
    }

    /**
     * Creates an event from a command after validating its description and time range.
     *
     * @param arguments text following the event keyword.
     * @return the parsed event.
     * @throws NovaException if a required event field is missing or out of order.
     */
    private Task parseEvent(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("An event needs a description and a time range. "
                    + "Try: event <description> /from <start> /to <end>.");
        }

        int fromSeparator = findMarker(arguments, MARKER_EVENT_START);
        int toSeparator = findMarker(arguments, MARKER_EVENT_END);
        if (fromSeparator < 0) {
            throw new NovaException("An event needs a /from start date or time.");
        }
        if (toSeparator < 0) {
            throw new NovaException("An event needs a /to end date or time.");
        }
        if (toSeparator < fromSeparator) {
            throw new NovaException("Put /from before /to. "
                    + "Try: event <description> /from <start> /to <end>.");
        }

        String description = arguments.substring(0, fromSeparator).trim();
        String startDateTimeText = arguments.substring(
                fromSeparator + MARKER_EVENT_START.length(), toSeparator).trim();
        String endDateTimeText = arguments.substring(toSeparator + MARKER_EVENT_END.length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("An event needs a description before /from.");
        }
        if (startDateTimeText.isEmpty()) {
            throw new NovaException("The /from field cannot be empty. Add a start date or time after /from.");
        }
        if (endDateTimeText.isEmpty()) {
            throw new NovaException("The /to field cannot be empty. Add an end date or time after /to.");
        }

        TaskDateTime.ParsedValue start = TaskDateTime.parse(startDateTimeText, MARKER_EVENT_START);
        TaskDateTime.ParsedValue end = TaskDateTime.parse(endDateTimeText, MARKER_EVENT_END);
        if (end.dateTime().isBefore(start.dateTime())) {
            throw new NovaException("An event's /to date/time cannot be before its /from date/time.");
        }
        return new Event(description, start.dateTime(), start.hasTime(),
                end.dateTime(), end.hasTime());
    }

    /**
     * Parses the date supplied to the date-search command.
     *
     * @param arguments text following the {@code on} keyword.
     * @return date whose scheduled tasks should be shown.
     * @throws NovaException if no valid date follows {@code on}.
     */
    private LocalDate parseSearchDate(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("Tell me which date to search. Try: on 2019-12-02.");
        }
        return TaskDateTime.parseDate(arguments);
    }

    /**
     * Parses the keyword supplied to the task-description search command.
     *
     * @param arguments text following the {@code find} keyword.
     * @return keyword to find in task descriptions.
     * @throws NovaException if no keyword follows {@code find}.
     */
    private String parseSearchKeyword(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("Tell me what to find. Try: find <keyword>.");
        }
        return arguments;
    }

    /**
     * Parses the task number in a mark, unmark, or delete command.
     *
     * @param taskNumberText task number text following the command keyword.
     * @param commandName command keyword used in error guidance.
     * @return one-based task number entered by the user.
     * @throws NovaException if the task number is missing or is not a whole number.
     */
    private int parseTaskNumber(String taskNumberText, String commandName) throws NovaException {
        if (taskNumberText.isEmpty()) {
            throw new NovaException("Tell me which task to " + commandName
                    + ". Try: " + commandName + " <task number>.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new NovaException("The task number after " + commandName
                    + " must be a whole number, for example: " + commandName + " 1.");
        }

        return taskNumber;
    }

    /**
     * Parses the selected task, field, and replacement value in an edit command.
     *
     * @param arguments text following the {@code edit} keyword.
     * @return command containing the requested partial edit.
     * @throws NovaException if the task number, field, or value is missing or malformed.
     */
    private Command parseEdit(String arguments) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("Tell me which task to edit. Try: edit <task number> "
                    + "<field> <new value>.");
        }

        int firstSpace = arguments.indexOf(' ');
        if (firstSpace < 0) {
            int taskNumber = parseTaskNumber(arguments, "edit");
            throw new NovaException("Tell me which field to edit in task " + taskNumber
                    + ". Use /description, /by, /from, or /to.");
        }

        int taskNumber = parseTaskNumber(arguments.substring(0, firstSpace), "edit");
        String editArguments = arguments.substring(firstSpace + 1).trim();
        EditField field = parseEditField(editArguments, taskNumber);
        String value = editArguments.substring(field.getMarker().length()).trim();
        if (value.isEmpty()) {
            throw new NovaException("The " + field.getMarker()
                    + " field cannot be empty. Add a new value after " + field.getMarker() + ".");
        }
        if (containsEditMarker(value)) {
            throw new NovaException("Edit one field at a time. Use a separate edit command for each field.");
        }
        return new EditCommand(taskNumber, field, value);
    }

    /**
     * Identifies the field marker at the start of edit arguments.
     *
     * @param editArguments field marker followed by its replacement value.
     * @param taskNumber selected task number used in error guidance.
     * @return matching edit field.
     * @throws NovaException if the field marker is absent or unknown.
     */
    private EditField parseEditField(String editArguments, int taskNumber) throws NovaException {
        if (editArguments.isEmpty()) {
            throw new NovaException("Tell me which field to edit in task " + taskNumber
                    + ". Use /description, /by, /from, or /to.");
        }

        for (EditField field : EditField.values()) {
            String marker = field.getMarker();
            if (editArguments.equals(marker) || editArguments.startsWith(marker + " ")) {
                return field;
            }
        }
        throw new NovaException("I don't recognize that edit field. "
                + "Use /description, /by, /from, or /to.");
    }

    /**
     * Returns whether an edit value contains another field marker as a separate token.
     *
     * @param value proposed replacement value.
     * @return {@code true} if another edit field was supplied.
     */
    private boolean containsEditMarker(String value) {
        return findMarker(value, MARKER_DESCRIPTION) >= 0
                || findMarker(value, MARKER_DEADLINE) >= 0
                || findMarker(value, MARKER_EVENT_START) >= 0
                || findMarker(value, MARKER_EVENT_END) >= 0;
    }

    /**
     * Finds a command marker only when it appears as a separate token.
     *
     * @param text command arguments to search.
     * @param marker marker such as {@code /by}, {@code /from}, or {@code /to}.
     * @return the marker's index, or {@code -1} when it is absent.
     */
    private int findMarker(String text, String marker) {
        int markerIndex = text.indexOf(marker);
        while (markerIndex >= 0) {
            int afterMarker = markerIndex + marker.length();
            boolean hasLeftBoundary = markerIndex == 0
                    || Character.isWhitespace(text.charAt(markerIndex - 1));
            boolean hasRightBoundary = afterMarker == text.length()
                    || Character.isWhitespace(text.charAt(afterMarker));
            if (hasLeftBoundary && hasRightBoundary) {
                return markerIndex;
            }
            markerIndex = text.indexOf(marker, markerIndex + 1);
        }
        return -1;
    }
}
