package larry.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import larry.command.AddCommand;
import larry.command.Command;
import larry.command.DateQueryCommand;
import larry.command.DeleteCommand;
import larry.command.EditCommand;
import larry.command.EditField;
import larry.command.ExitCommand;
import larry.command.FindCommand;
import larry.command.ListCommand;
import larry.command.MarkCommand;
import larry.command.UnmarkCommand;
import larry.exception.LarryException;
import larry.task.Deadline;
import larry.task.Event;
import larry.task.Task;
import larry.task.TaskDateTime;
import larry.task.Todo;

/**
 * Parses user commands into values that Larry can act on.
 */
public class Parser {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
    private static final String TODO_FORMAT_ERROR = "EVIL LARRY cannot bind a nameless task. "
            + "Use: todo DESCRIPTION.";
    private static final String DEADLINE_FORMAT_ERROR = "EVIL LARRY demands proper tribute. "
            + "Use: deadline DESCRIPTION /by DATE TIME.";
    private static final String DEADLINE_DATE_ERROR = "EVIL LARRY rejects that deadline date. "
            + "Try: 06-09-2026 1800.";
    private static final String EVENT_FORMAT_ERROR = "EVIL LARRY demands a complete scheme. "
            + "Use: event DESCRIPTION /from DATE TIME /to DATE TIME.";
    private static final String EVENT_DATE_ERROR = "EVIL LARRY rejects that event date. "
            + "Try: 06-09-2026 1400.";
    private static final String EVENT_ORDER_ERROR = "EVIL LARRY refuses to bend time. "
            + "An event must end after it starts.";
    private static final String DATE_QUERY_FORMAT_ERROR = "EVIL LARRY needs a date to inspect. "
            + "Use: on DATE.";
    private static final String DATE_QUERY_DATE_ERROR = "EVIL LARRY cannot rule that date. "
            + "Try: on 06-09-2026.";
    private static final String FIND_FORMAT_ERROR = "EVIL LARRY needs a keyword before he can hunt. "
            + "Use: find KEYWORD.";

    /**
     * Prevents construction of a utility class.
     */
    private Parser() {
    }

    /**
     * Checks whether an input is a command keyword, optionally followed by arguments.
     *
     * @param input Full user input.
     * @param keyword Command keyword to match.
     * @return True when the input invokes the specified command.
     */
    private static boolean isCommand(String input, String keyword) {
        return input.equals(keyword) || input.startsWith(keyword + " ");
    }

    /**
     * Parses a full user command into a command that Larry can execute.
     *
     * @param command Full user command.
     * @return Command represented by the user input.
     * @throws LarryException If the command or its arguments are invalid.
     */
    public static Command parseCommand(String command) throws LarryException {
        String normalizedCommand = Objects.requireNonNull(command, "command").trim();
        if (normalizedCommand.equals("bye")) {
            return new ExitCommand();
        }
        if (normalizedCommand.equals("list")) {
            return new ListCommand();
        }
        if (isCommand(normalizedCommand, "on")) {
            return new DateQueryCommand(parseDate(normalizedCommand, "on"));
        }
        if (isCommand(normalizedCommand, "find")) {
            return new FindCommand(requireArgument(normalizedCommand, "find", FIND_FORMAT_ERROR));
        }
        if (isCommand(normalizedCommand, "mark")) {
            return new MarkCommand(parseTaskIndex(normalizedCommand, "mark"));
        }
        if (isCommand(normalizedCommand, "unmark")) {
            return new UnmarkCommand(parseTaskIndex(normalizedCommand, "unmark"));
        }
        if (isCommand(normalizedCommand, "delete")) {
            return new DeleteCommand(parseTaskIndex(normalizedCommand, "delete"));
        }
        if (isCommand(normalizedCommand, "edit")) {
            return parseEditCommand(normalizedCommand);
        }
        return new AddCommand(parseTask(normalizedCommand));
    }

    /**
     * Parses a command that changes exactly one supported task field.
     *
     * @param command Full edit command.
     * @return Edit command represented by the input.
     * @throws LarryException If the index, field marker, or replacement value is invalid.
     */
    private static EditCommand parseEditCommand(String command) throws LarryException {
        String arguments = command.substring("edit".length()).trim();
        if (arguments.isEmpty()) {
            throw invalidEditSyntax();
        }

        String[] parts = arguments.split("\\s+", 3);
        int taskIndex = parseEditTaskIndex(parts[0]);
        if (parts.length < 2) {
            throw invalidEditSyntax();
        }

        EditField field = EditField.fromMarker(parts[1])
                .orElseThrow(() -> new LarryException(
                        "Edit field must be /description, /by, /from, or /to."));
        if (parts.length < 3 || parts[2].isBlank()) {
            throw new LarryException("The edit replacement value cannot be blank.");
        }
        String replacementValue = parts[2].trim();
        validateEditDateTime(field, replacementValue);
        return new EditCommand(taskIndex, field, replacementValue);
    }

    /**
     * Validates replacement date-time text while leaving descriptions unrestricted.
     */
    private static void validateEditDateTime(EditField field, String replacementValue)
            throws LarryException {
        if (field == EditField.DESCRIPTION) {
            return;
        }

        try {
            new TaskDateTime(replacementValue);
        } catch (DateTimeParseException e) {
            throw new LarryException("The value for " + field.getMarker()
                    + " is not a valid date and time.");
        }
    }

    /**
     * Parses the positive one-based task number in an edit command.
     */
    private static int parseEditTaskIndex(String indexText) throws LarryException {
        try {
            int taskNumber = Integer.parseInt(indexText);
            if (taskNumber <= 0) {
                throw new LarryException(
                        "The edit task index must be a positive whole number.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException e) {
            throw new LarryException(
                    "The edit task index must be a positive whole number.");
        }
    }

    /**
     * Creates the error used when an edit command lacks its required structure.
     */
    private static LarryException invalidEditSyntax() {
        return new LarryException("Use edit INDEX /description DESCRIPTION, "
                + "/by DATE_TIME, /from DATE_TIME, or /to DATE_TIME.");
    }

    /**
     * Converts a task-creation command into the appropriate task subtype.
     *
     * @param command Full task-creation command.
     * @return Task represented by the command.
     * @throws LarryException If the command or any required field is invalid.
     */
    private static Task parseTask(String command) throws LarryException {
        if (isCommand(command, "todo")) {
            return new Todo(requireArgument(command, "todo", TODO_FORMAT_ERROR));
        }

        if (isCommand(command, "deadline")) {
            return parseDeadline(command);
        }

        if (isCommand(command, "event")) {
            return parseEvent(command);
        }

        throw new LarryException();
    }

    /**
     * Parses a deadline command and validates its description and due date.
     *
     * @param command Full deadline command.
     * @return Deadline represented by the command.
     * @throws LarryException If a required field or the due date is invalid.
     */
    private static Deadline parseDeadline(String command) throws LarryException {
        String arguments = requireArgument(command, "deadline", DEADLINE_FORMAT_ERROR);
        int byPosition = arguments.indexOf(DEADLINE_SEPARATOR);
        int dueDatePosition = byPosition + DEADLINE_SEPARATOR.length();
        if (byPosition <= 0 || dueDatePosition >= arguments.length()) {
            throw new LarryException(DEADLINE_FORMAT_ERROR);
        }

        String description = arguments.substring(0, byPosition).trim();
        String dueDate = arguments.substring(dueDatePosition).trim();
        if (description.isEmpty() || dueDate.isEmpty()) {
            throw new LarryException(DEADLINE_FORMAT_ERROR);
        }
        try {
            return new Deadline(description, dueDate);
        } catch (DateTimeParseException e) {
            throw new LarryException(DEADLINE_DATE_ERROR);
        }
    }

    /**
     * Parses an event command and validates its description, start time, and end time.
     *
     * @param command Full event command.
     * @return Event represented by the command.
     * @throws LarryException If a required field or either date and time is invalid.
     */
    private static Event parseEvent(String command) throws LarryException {
        String arguments = requireArgument(command, "event", EVENT_FORMAT_ERROR);
        int fromPosition = arguments.indexOf(EVENT_START_SEPARATOR);
        int startTimePosition = fromPosition + EVENT_START_SEPARATOR.length();
        int toPosition = arguments.indexOf(EVENT_END_SEPARATOR, startTimePosition);
        int endTimePosition = toPosition + EVENT_END_SEPARATOR.length();
        if (fromPosition <= 0 || toPosition <= startTimePosition
                || endTimePosition >= arguments.length()) {
            throw new LarryException(EVENT_FORMAT_ERROR);
        }

        String description = arguments.substring(0, fromPosition).trim();
        String startTime = arguments.substring(startTimePosition, toPosition).trim();
        String endTime = arguments.substring(endTimePosition).trim();
        if (description.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            throw new LarryException(EVENT_FORMAT_ERROR);
        }
        try {
            return new Event(description, startTime, endTime);
        } catch (DateTimeParseException e) {
            throw new LarryException(EVENT_DATE_ERROR);
        } catch (IllegalArgumentException e) {
            throw new LarryException(EVENT_ORDER_ERROR);
        }
    }

    /**
     * Parses and validates a date supplied to a command.
     *
     * @param command Full command containing the date.
     * @param keyword Command keyword preceding the date.
     * @return Parsed date.
     * @throws LarryException If the date is absent or invalid.
     */
    private static LocalDate parseDate(String command, String keyword) throws LarryException {
        String dateText = requireArgument(command, keyword, DATE_QUERY_FORMAT_ERROR);
        try {
            return TaskDateTime.parseDate(dateText);
        } catch (DateTimeParseException e) {
            throw new LarryException(DATE_QUERY_DATE_ERROR);
        }
    }

    /**
     * Parses a one-based task number supplied to a task command.
     *
     * @param command Full mark, unmark, or delete command.
     * @param keyword Command keyword.
     * @return Zero-based task index.
     * @throws LarryException If the task number is absent, non-numeric, zero, or negative.
     */
    private static int parseTaskIndex(String command, String keyword) throws LarryException {
        String indexError = "EVIL LARRY demands a positive task number after " + keyword + ".";
        String indexText = requireArgument(command, keyword, indexError);
        try {
            int taskIndex = Integer.parseInt(indexText) - 1;
            if (taskIndex < 0) {
                throw new LarryException(indexError);
            }
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new LarryException(indexError);
        }
    }

    /**
     * Extracts a required command argument and rejects blank values.
     *
     * @param command Full user command.
     * @param keyword Command keyword preceding the argument.
     * @param errorMessage Message to report when the argument is absent.
     * @return Trimmed argument text.
     * @throws LarryException If the argument is empty.
     */
    private static String requireArgument(String command, String keyword, String errorMessage)
            throws LarryException {
        assert isCommand(command, keyword) : "Command must start with the expected keyword";
        String argument = command.substring(keyword.length()).trim();
        if (argument.isEmpty()) {
            throw new LarryException(errorMessage);
        }
        return argument;
    }
}
