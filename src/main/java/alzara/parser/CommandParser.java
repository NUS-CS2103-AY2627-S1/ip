package alzara.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import alzara.AlzaraException;
import alzara.command.AddCommand;
import alzara.command.Command;
import alzara.command.DeleteCommand;
import alzara.command.ExitCommand;
import alzara.command.FindCommand;
import alzara.command.ListCommand;
import alzara.command.MarkCommand;
import alzara.command.UnmarkCommand;
import alzara.command.ViewCommand;
import alzara.task.Deadline;
import alzara.task.Event;
import alzara.task.Task;
import alzara.task.ToDo;

/**
 * Makes sense of the raw command text typed by the user and builds the
 * {@link Command} it describes, or reports why the command is malformed.
 */
public class CommandParser {
    private static final String TODO_PREFIX = "todo ";
    private static final String DEADLINE_PREFIX = "deadline ";
    private static final String EVENT_PREFIX = "event ";
    private static final String FIND_PREFIX = "find ";
    private static final String VIEW_PREFIX = "view ";
    private static final String BY_MARKER = " /by ";
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";

    /**
     * Parses a full command line into the {@link Command} it describes.
     *
     * <p>Leading and trailing spaces around the whole line are stripped
     * first, since they are typing noise rather than part of the command.
     */
    public static Command parse(String command) throws AlzaraException {
        String trimmedCommand = command.trim();
        switch (CommandType.from(trimmedCommand)) {
            case BYE:
                return new ExitCommand();
            case MARK:
                return new MarkCommand(parseTaskIndex(trimmedCommand));
            case UNMARK:
                return new UnmarkCommand(parseTaskIndex(trimmedCommand));
            case TODO:
                return new AddCommand(parseTodo(trimmedCommand), "You have something to do...");
            case DEADLINE:
                return new AddCommand(parseDeadline(trimmedCommand), "Do not miss the deadline.");
            case EVENT:
                return new AddCommand(parseEvent(trimmedCommand), "Am I invited?");
            case DELETE:
                return new DeleteCommand(parseTaskIndex(trimmedCommand));
            case LIST:
                return new ListCommand();
            case FIND:
                return new FindCommand(parseKeywords(trimmedCommand));
            case VIEW:
                return new ViewCommand(parseViewDate(trimmedCommand));
            case UNKNOWN:
            default:
                throw new AlzaraException(AlzaraException.UNRECOGNISED_COMMAND_MESSAGE);
        }
    }

    /**
     * Parses the 0-based task index out of a {@code mark}/{@code unmark}/{@code delete}
     * command. Does not check the index against the task list's bounds, since that
     * depends on the list's current size rather than the command text itself - each
     * command's {@code execute} checks that itself.
     *
     * @throws AlzaraException if the task number is missing, not a number, or
     *         followed by extra unexpected arguments
     */
    private static int parseTaskIndex(String command) throws AlzaraException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length < 2) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_NUMBER_MESSAGE);
        }
        if (parts.length > 2) {
            throw new AlzaraException(AlzaraException.TOO_MANY_ARGUMENTS_MESSAGE);
        }

        try {
            return Integer.parseInt(parts[1]) - 1;
        } catch (NumberFormatException exception) {
            throw new AlzaraException(AlzaraException.NON_NUMERIC_TASK_NUMBER_MESSAGE);
        }
    }

    /**
     * Parses a {@code todo} command into a {@link ToDo}.
     *
     * @throws AlzaraException if the command has no description, or the
     *         description contains the {@code |} character
     */
    private static Task parseTodo(String command) throws AlzaraException {
        if (command.trim().equalsIgnoreCase("todo")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        assert command.toLowerCase().startsWith(TODO_PREFIX)
                : "parseTodo is only called after CommandType.from classified the command as TODO, "
                + "which guarantees this prefix, ignoring case";
        String description = command.substring(TODO_PREFIX.length()).trim();
        validateDescription(description);
        return new ToDo(description);
    }

    /**
     * Rejects a description containing the {@code |} character, since it is
     * the field separator {@link alzara.storage.Storage} uses in the save
     * file - allowing it through would silently corrupt that task on the
     * next save/load round trip.
     */
    private static void validateDescription(String description) throws AlzaraException {
        if (description.contains("|")) {
            throw new AlzaraException(AlzaraException.FORBIDDEN_CHARACTER_MESSAGE);
        }
    }

    /**
     * Parses a {@code deadline} command into a {@link Deadline}, extracting the
     * description before the {@code /by} marker and the date after it.
     *
     * @throws AlzaraException if the description or {@code /by} date is missing
     *         or malformed, {@code /by} appears more than once, or the
     *         description contains the {@code |} character
     */
    private static Task parseDeadline(String command) throws AlzaraException {
        if (command.trim().equalsIgnoreCase("deadline")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        assert command.toLowerCase().startsWith("deadline ")
                : "parseDeadline is only called after CommandType.from classified the command as DEADLINE, "
                + "which guarantees this prefix, ignoring case";

        int deadlineMarker = command.indexOf(BY_MARKER);
        if (deadlineMarker == -1) {
            throw new AlzaraException(AlzaraException.MISSING_DEADLINE_MARKER_MESSAGE);
        }
        if (command.indexOf(BY_MARKER, deadlineMarker + BY_MARKER.length()) != -1) {
            throw new AlzaraException(AlzaraException.DUPLICATE_MARKER_MESSAGE);
        }

        String description = command.substring(DEADLINE_PREFIX.length(), deadlineMarker).trim();
        if (description.isEmpty()) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        validateDescription(description);

        String deadlineText = command.substring(deadlineMarker + BY_MARKER.length());
        LocalDate deadline;
        try {
            deadline = LocalDate.parse(deadlineText.trim());
        } catch (DateTimeParseException exception) {
            throw new AlzaraException(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE);
        }

        return new Deadline(description, deadline);
    }

    /**
     * Parses an {@code event} command into an {@link Event}, extracting the
     * description before the {@code /from} marker and the two dates between
     * the {@code /from} and {@code /to} markers.
     *
     * @throws AlzaraException if the description or either date is missing,
     *         the markers are out of order or repeated, either date is
     *         malformed, the parsed start date is after the end date, or the
     *         description contains the {@code |} character
     */
    private static Task parseEvent(String command) throws AlzaraException {
        if (command.trim().equalsIgnoreCase("event")) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        assert command.toLowerCase().startsWith("event ")
                : "parseEvent is only called after CommandType.from classified the command as EVENT, "
                + "which guarantees this prefix, ignoring case";

        int startMarker = command.indexOf(FROM_MARKER);
        int endMarker = command.indexOf(TO_MARKER);
        if (startMarker == -1 || endMarker == -1 || endMarker < startMarker) {
            throw new AlzaraException(AlzaraException.MISSING_EVENT_MARKER_MESSAGE);
        }
        if (command.indexOf(FROM_MARKER, startMarker + FROM_MARKER.length()) != -1
                || command.indexOf(TO_MARKER, endMarker + TO_MARKER.length()) != -1) {
            throw new AlzaraException(AlzaraException.DUPLICATE_MARKER_MESSAGE);
        }

        String description = command.substring(EVENT_PREFIX.length(), startMarker).trim();
        if (description.isEmpty()) {
            throw new AlzaraException(AlzaraException.MISSING_TASK_DESC);
        }
        validateDescription(description);

        String startText = command.substring(startMarker + FROM_MARKER.length(), endMarker);
        String endText = command.substring(endMarker + TO_MARKER.length());
        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startText.trim());
            end = LocalDate.parse(endText.trim());
        } catch (DateTimeParseException exception) {
            throw new AlzaraException(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE);
        }
        if (start.isAfter(end)) {
            throw new AlzaraException(AlzaraException.EVENT_DATES_OUT_OF_ORDER_MESSAGE);
        }

        return new Event(description, start, end);
    }

    /**
     * Parses a {@code find} command into the whitespace-separated keywords to
     * search for.
     *
     * @throws AlzaraException if the command has no keyword at all
     */
    private static String[] parseKeywords(String command) throws AlzaraException {
        if (command.trim().equalsIgnoreCase("find")) {
            throw new AlzaraException(AlzaraException.MISSING_KEYWORD_MESSAGE);
        }
        return command.substring(FIND_PREFIX.length()).trim().split("\\s+");
    }

    /**
     * Parses a {@code view} command into the queried {@link LocalDate}.
     *
     * @throws AlzaraException if the date is missing or malformed
     */
    private static LocalDate parseViewDate(String command) throws AlzaraException {
        if (command.trim().equalsIgnoreCase("view")) {
            throw new AlzaraException(AlzaraException.MISSING_VIEW_DATE_MESSAGE);
        }
        assert command.toLowerCase().startsWith(VIEW_PREFIX)
                : "parseViewDate is only called after CommandType.from classified the command as VIEW, "
                + "which guarantees this prefix, ignoring case";

        String dateText = command.substring(VIEW_PREFIX.length()).trim();
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new AlzaraException(AlzaraException.INVALID_DEADLINE_DATE_MESSAGE);
        }
    }
}
