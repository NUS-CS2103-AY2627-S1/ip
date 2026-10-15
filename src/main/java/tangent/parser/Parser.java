package tangent.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tangent.command.AddCommand;
import tangent.command.Command;
import tangent.command.DeleteCommand;
import tangent.command.ExitCommand;
import tangent.command.FindCommand;
import tangent.command.ListCommand;
import tangent.command.MarkCommand;
import tangent.command.UnmarkCommand;
import tangent.exception.ErrorMessages;
import tangent.exception.TangentException;
import tangent.task.Deadline;
import tangent.task.Event;
import tangent.task.Task;
import tangent.task.ToDo;

/** Interprets and validates commands entered by the user. */
public class Parser {
    /** The specified format of the date the user inputs for {@code Event} and {@code Deadline} objects. */
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter
            .ofPattern("d/M/uuuu HHmm")
            .withResolverStyle(ResolverStyle.STRICT);
    /** Pattern used to separate date-format errors from invalid calendar dates. */
    private static final String INPUT_DATE_PATTERN = "\\d{1,2}/\\d{1,2}/\\d{4} \\d{4}";
    /** The separator to be used in the data file to separate the details of a task. */
    private static final String FIELD_SEPARATOR = " | ";
    /** The marker to identify when a deadline should follow in a {@code Deadline} object. */
    private static final String BY_MARKER = " /by ";
    /** The marker to identify when a start time should follow in an {@code Event} object. */
    private static final String FROM_MARKER = " /from ";
    /** The marker to identify when an end time should follow in an {@code Event} object. */
    private static final String TO_MARKER = " /to ";

    /**
     * Converts a complete user command into the command object that performs its action.
     *
     * @param fullCommand The full response entered by the user.
     * @throws TangentException if the command is not part of the recognised keywords for existing commands,
     *     or the task description is empty for {@code ToDo}, {@code Deadline} and {@code Event} objects.
     */
    public static Command parse(String fullCommand) throws TangentException {
        assert fullCommand != null : "command input must exist";
        String[] inputs = fullCommand.split(" ", 2);
        CommandTypes type = CommandTypes.fromInput(inputs[0]);
        switch (type) {
            case MARK:
                return new MarkCommand(parseTaskIndexes(inputs));
            case UNMARK:
                return new UnmarkCommand(parseTaskIndexes(inputs));
            case FIND:
                requireArgument(inputs, ErrorMessages.MISSING_SEARCH_KEYWORD_MESSAGE);
                return new FindCommand(inputs[1].trim());
            case DELETE:
                return new DeleteCommand(parseTaskIndexes(inputs));
            case TODO:
            case DEADLINE:
            case EVENT:
                requireArgument(inputs, ErrorMessages.MISSING_TASK_DESCRIPTION_MESSAGE);
                return new AddCommand(parseTask(inputs[1], type));
            case LIST:
                return new ListCommand();
            case BYE:
                return new ExitCommand();
            default:
                throw new TangentException(ErrorMessages.INVALID_COMMAND_MESSAGE);
        }
    }

    /** Ensures a command has non-empty text after its keyword. */
    private static void requireArgument(String[] inputs, String errorMessage) throws TangentException {
        if (inputs.length < 2 || inputs[1].trim().isEmpty()) {
            throw new TangentException(errorMessage);
        }
    }

    /**
     * Parses one or more space-separated task indexes or inclusive task-index ranges.
     *
     * @param inputs An array containing the command type followed by task selectors.
     * @return 0-based task indexes in the order supplied by the user
     * @throws TangentException if selectors are invalid, i.e. the indexes are
     *     missing, in the wrong format, reversed, duplicated or overlapping.
     */
    public static List<Integer> parseTaskIndexes(String[] inputs) throws TangentException {
        if (inputs.length < 2 || inputs[1].trim().isEmpty()) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
        List<Integer> taskIndexes = new ArrayList<>();
        Set<Integer> seenIndexes = new HashSet<>();
        String[] selectors = inputs[1].trim().split(" +");
        for (String selector : selectors) {
            parseSelector(selector, taskIndexes, seenIndexes);
        }
        return taskIndexes;
    }

    /** Parses one selector and appends its zero-based indexes to the supplied collections. */
    private static void parseSelector(String selector, List<Integer> taskIndexes, Set<Integer> seenIndexes)
            throws TangentException {
        // Checks for cases without a start index (delete -3) or extra leading zero (delete 04)
        if (selector.matches("-[0-9]+") || selector.matches("0[0-9]*")) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
        // Checks for cases with a single index (e.g. delete 2 / delete 20)
        if (selector.matches("[1-9][0-9]*")) {
            addTaskIndex(parseTaskNumber(selector), taskIndexes, seenIndexes);
            return;
        }
        // Checks for cases without a valid separator (e.g. delete 3&4)
        if (!selector.contains("-") && !selector.contains(",")) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
        // Checks for cases that are not a range bounded by 2 integers (e.g. delete 2a-3)
        if (!selector.matches("[1-9][0-9]*-[1-9][0-9]*")) {
            throw new TangentException(ErrorMessages.TASK_INDEX_FORMAT_MESSAGE);
        }
        String[] range = selector.split("-", -1);
        int start = parseTaskNumber(range[0]);
        int end = parseTaskNumber(range[1]);
        // Checks for cases that have a start index greater than the end index (e.g. delete 6-4)
        if (start >= end) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
        // Adds all task numbers within the range
        for (int taskNumber = start; taskNumber <= end; taskNumber++) {
            addTaskIndex(taskNumber, taskIndexes, seenIndexes);
            // Prevents overflow in cases like 1-2147483647
            if (taskNumber == end) {
                break;
            }
        }
    }

    /** Adds one parsed task number after checking for duplicate or overlapping selectors. */
    private static void addTaskIndex(int taskNumber, List<Integer> taskIndexes, Set<Integer> seenIndexes)
            throws TangentException {
        int taskIndex = taskNumber - 1;
        if (!seenIndexes.add(taskIndex)) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
        taskIndexes.add(taskIndex);
    }

    /** Converts one task number to an integer. */
    private static int parseTaskNumber(String taskNumber) throws TangentException {
        try {
            return Integer.parseInt(taskNumber);
        } catch (NumberFormatException e) {
            throw new TangentException(ErrorMessages.INVALID_TASK_INDEX_MESSAGE);
        }
    }

    /**
     * Creates a new Task object based on the user's input.
     *
     * @param details The string that comes after the command type.
     * @param type The command type parsed from the user's command.
     * @return {@code ToDo}, {@code Deadline} or {@code Event} object corresponding to {@code type}.
     * @throws TangentException if {@code type} cannot be understood or if {@code details} contains invalid characters,
     *     is not in the correct format, uses a wrong date format, or contains an end time earlier than its start time.
     */
    public static Task parseTask(String details, CommandTypes type) throws TangentException {
        assert details != null : "task details must exist";
        assert type != null : "task type must exist";
        String description = details.trim();
        switch (type) {
            case TODO:
                validateDescription(description);
                return new ToDo(description);
            case DEADLINE:
                return parseDeadline(details);
            case EVENT:
                return parseEvent(details);
            default:
                throw new TangentException(ErrorMessages.UNKNOWN_TASK_TYPE_MESSAGE);
        }
    }

    /**
     * Creates a new {@code Deadline} object based on the description and the deadline given in {@code details}.
     *
     * @throws TangentException if the details are not in the correct format or contains an invalid date format.
     */
    private static Deadline parseDeadline(String details) throws TangentException {
        int byIndex = details.indexOf(BY_MARKER);
        if (byIndex <= 0 || details.indexOf(BY_MARKER, byIndex + BY_MARKER.length()) != -1) {
            throw new TangentException(ErrorMessages.DEADLINE_FORMAT_MESSAGE);
        }
        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new TangentException(ErrorMessages.DEADLINE_FORMAT_MESSAGE);
        }
        validateDescription(description);
        return new Deadline(description, parseDateTime(by));
    }

    /**
     * Creates a new {@code Event} object based on the description, start time and end time given in {@code details}.
     *
     * @throws TangentException if the details are not in the correct format, contains an invalid date format,
     *     or contains an end time that is earlier than its start time.
     */
    private static Event parseEvent(String details) throws TangentException {
        int fromIndex = details.indexOf(FROM_MARKER);
        int toIndex = details.indexOf(TO_MARKER);
        boolean hasValidMarkerOrder = fromIndex > 0 && toIndex > fromIndex;
        boolean hasRepeatedFromMarker = details.indexOf(FROM_MARKER,
                fromIndex + FROM_MARKER.length()) != -1;
        boolean hasRepeatedToMarker = details.indexOf(TO_MARKER,
                toIndex + TO_MARKER.length()) != -1;
        if (!hasValidMarkerOrder || hasRepeatedFromMarker || hasRepeatedToMarker) {
            throw new TangentException(ErrorMessages.EVENT_FORMAT_MESSAGE);
        }
        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = details.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new TangentException(ErrorMessages.EVENT_FORMAT_MESSAGE);
        }
        validateDescription(description);
        LocalDateTime fromDateTime = parseDateTime(from);
        LocalDateTime toDateTime = parseDateTime(to);
        if (!toDateTime.isAfter(fromDateTime)) {
            throw new TangentException(ErrorMessages.END_TIME_ORDER_MESSAGE);
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    /**
     * Ensures descriptions do not contain the {@code FIELD_SEPARATOR}.
     *
     * @throws TangentException if the description contains the {@code FIELD_SEPARATOR}.
     */
    private static void validateDescription(String description) throws TangentException {
        if (description.contains(FIELD_SEPARATOR)) {
            throw new TangentException(String.format(ErrorMessages.DESCRIPTION_SEPARATOR_MESSAGE, FIELD_SEPARATOR));
        }
    }

    /**
     * Parses a user-supplied date and time.
     *
     * @throws TangentException if the provided date and time has a wrong format or is not a valid calendar value.
     */
    private static LocalDateTime parseDateTime(String input) throws TangentException {
        String trimmedInput = input.trim();
        if (!trimmedInput.matches(INPUT_DATE_PATTERN)) {
            throw new TangentException(ErrorMessages.BAD_DATE_MESSAGE);
        }
        try {
            return LocalDateTime.parse(trimmedInput, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new TangentException(ErrorMessages.INVALID_DATE_MESSAGE);
        }
    }
}
