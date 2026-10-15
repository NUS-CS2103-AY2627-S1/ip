package alex.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alex.exception.AlexException;
import alex.task.Deadline;
import alex.task.Event;
import alex.task.Task;
import alex.task.Todo;
import alex.util.DateParser;
import alex.util.TaskDateTime;

/**
 * Interprets user commands and converts their arguments into domain objects.
 */
public class Parser {
    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_DATE_MARKER = "/from";
    private static final String EVENT_END_DATE_MARKER = "/to";
    private static final String DATE_TIME_FORMAT_GUIDANCE =
            "Use yyyy-MM-dd and optionally HHmm, like 2026-09-20 1830.";
    private static final Pattern DEADLINE_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(DEADLINE_DATE_MARKER) + "(?!\\S)");
    private static final Pattern EVENT_START_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(EVENT_START_DATE_MARKER) + "(?!\\S)");
    private static final Pattern EVENT_END_DATE_MARKER_PATTERN = Pattern.compile(
            "(?<!\\S)" + Pattern.quote(EVENT_END_DATE_MARKER) + "(?!\\S)");

    /**
     * Identifies the type of a user command.
     *
     * @param command full user command.
     * @return the matching command type.
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.parse(command);
    }

    /**
     * Ensures that a command which takes no arguments has no additional details.
     *
     * @param command full user command.
     * @param commandType command whose arguments are being validated.
     * @throws AlexException if additional details follow the command keyword.
     */
    public static void validateNoArguments(String command, CommandType commandType)
            throws AlexException {
        assert commandType != CommandType.UNKNOWN && !commandType.canAcceptArguments()
                : "Command type must be a known command that rejects arguments";
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";

        if (!getArguments(command, commandType).isEmpty()) {
            throw new AlexException("The '" + commandType.getKeyword()
                    + "' order comes as-is. No extras needed.");
        }
    }

    /**
     * Extracts and validates the one-based task number in a command.
     *
     * @param command full user command.
     * @param commandType command whose argument is being parsed.
     * @param taskCount number of tasks currently available.
     * @return corresponding zero-based task index.
     * @throws AlexException if the argument is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws AlexException {
        assert taskCount >= 0 : "Task count cannot be negative";
        assert commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                || commandType == CommandType.DELETE
                : "Only task-selection commands have a task index";
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";

        String commandName = commandType.getKeyword();
        String taskNumberText = command.substring(commandName.length()).trim();

        if (taskNumberText.isEmpty()) {
            throw new AlexException(
                    "Which order number? Put a task number after '" + commandName + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new AlexException("'" + taskNumberText
                    + "' isn't an order number. I need an actual number here.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            if (taskCount == 0) {
                throw new AlexException("There isn't anything on the order yet.");
            }
            throw new AlexException("Order numbers run from 1 to " + taskCount
                    + ". Pick one from the display case.");
        }

        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "Parsed task index must be within the task list";
        return taskIndex;
    }

    /**
     * Extracts and validates the keyword in a find command.
     *
     * @param command full user command.
     * @return keyword to search for.
     * @throws AlexException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws AlexException {
        String keyword = getArguments(command, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new AlexException(
                    "What am I looking for? Give me a keyword after 'find'.");
        }
        return keyword;
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param command full user command.
     * @param commandType type of task to create.
     * @return task represented by the command.
     * @throws AlexException if required task details are missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType) throws AlexException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Only task-creation commands can be parsed as tasks";

        return switch (commandType) {
            case TODO -> parseTodo(command);
            case DEADLINE -> parseDeadline(command);
            case EVENT -> parseEvent(command);
            default -> throw new AlexException("This command does not create a task.");
        };
    }

    private static Task parseTodo(String command) throws AlexException {
        String description = getArguments(command, CommandType.TODO);
        if (description.isEmpty()) {
            throw new AlexException(
                    "One todo with no description? That's basically an empty cup.");
        }
        validateDescriptionCharacters(description);
        return new Todo(description);
    }

    private static Task parseDeadline(String command) throws AlexException {
        String details = getArguments(command, CommandType.DEADLINE);
        if (details.isEmpty()) {
            throw new AlexException("A deadline needs a description first. "
                    + "Tell me what you're ordering before '/by'.");
        }

        List<Integer> dueDateSeparators = findMarkerPositions(
                details, DEADLINE_DATE_MARKER_PATTERN);

        if (!dueDateSeparators.isEmpty() && dueDateSeparators.get(0) == 0) {
            throw new AlexException("I have the due date, but not what you're ordering. "
                    + "Add a description before '/by'.");
        }
        if (dueDateSeparators.size() != 1) {
            throw new AlexException(
                    "A deadline needs one '" + DEADLINE_DATE_MARKER + "' before its due date. House rule.");
        }

        int dueDateSeparator = dueDateSeparators.get(0);
        String description = details.substring(0, dueDateSeparator).trim();
        String dueDateText = details.substring(
                dueDateSeparator + DEADLINE_DATE_MARKER.length()).trim();
        validateDescriptionCharacters(description);
        if (dueDateText.isEmpty()) {
            throw new AlexException("You forgot the due date. "
                    + "Add one after '/by' so I know when to serve it. "
                    + DATE_TIME_FORMAT_GUIDANCE);
        }

        TaskDateTime dueDateTime = DateParser.parseTaskDateTime(dueDateText);
        return new Deadline(description, dueDateTime);
    }

    private static Task parseEvent(String command) throws AlexException {
        String details = getArguments(command, CommandType.EVENT);
        if (details.isEmpty()) {
            throw new AlexException("An event needs a description first. "
                    + "Tell me what you're booking before '/from'.");
        }

        List<Integer> startDateSeparators = findMarkerPositions(
                details, EVENT_START_DATE_MARKER_PATTERN);
        List<Integer> endDateSeparators = findMarkerPositions(
                details, EVENT_END_DATE_MARKER_PATTERN);

        if ((!startDateSeparators.isEmpty() && startDateSeparators.get(0) == 0)
                || (!endDateSeparators.isEmpty() && endDateSeparators.get(0) == 0)) {
            throw new AlexException("I have the booking time, but no idea what it's for. "
                    + "Add a description first.");
        }
        if (startDateSeparators.size() != 1) {
            throw new AlexException(
                    "An event needs exactly one '/from'. One starting time is usually enough.");
        }
        int startDateSeparator = startDateSeparators.get(0);
        String description = details.substring(0, startDateSeparator).trim();
        validateDescriptionCharacters(description);

        int startDateTextEnd = endDateSeparators.isEmpty()
                ? details.length()
                : endDateSeparators.get(0);
        if (startDateSeparator >= startDateTextEnd) {
            throw new AlexException(
                    "Put '/from' before '/to'. Time still works that way here.");
        }

        String startDateText = details.substring(
                startDateSeparator + EVENT_START_DATE_MARKER.length(), startDateTextEnd).trim();
        if (startDateText.isEmpty()) {
            throw new AlexException("When does this start? Add a date after '/from'. "
                    + DATE_TIME_FORMAT_GUIDANCE);
        }
        TaskDateTime startDateTime = DateParser.parseTaskDateTime(startDateText);

        if (endDateSeparators.size() != 1) {
            throw new AlexException(
                    "An event needs exactly one '/to'. Let's not keep the table indefinitely.");
        }
        int endDateSeparator = endDateSeparators.get(0);
        String endDateText = details.substring(
                endDateSeparator + EVENT_END_DATE_MARKER.length()).trim();
        if (endDateText.isEmpty()) {
            throw new AlexException("When does this end? Add a date after '/to'. "
                    + DATE_TIME_FORMAT_GUIDANCE);
        }

        TaskDateTime endDateTime = DateParser.parseTaskDateTime(endDateText);
        try {
            return new Event(description, startDateTime, endDateTime);
        } catch (IllegalArgumentException e) {
            throw new AlexException(e.getMessage());
        }
    }

    private static void validateDescriptionCharacters(String description) throws AlexException {
        if (description.contains("|")) {
            throw new AlexException("Task descriptions can't contain '|'. House rule, apparently.");
        }
    }

    /**
     * Returns the starting positions of standalone markers in the supplied details.
     * A marker is standalone when whitespace or a string boundary appears on each side.
     *
     * @param details command details to search.
     * @param markerPattern pattern representing a standalone marker.
     * @return marker positions in their original order.
     */
    private static List<Integer> findMarkerPositions(String details, Pattern markerPattern) {
        ArrayList<Integer> markerPositions = new ArrayList<>();
        Matcher matcher = markerPattern.matcher(details);
        while (matcher.find()) {
            markerPositions.add(matcher.start());
        }
        return markerPositions;
    }

    private static String getArguments(String command, CommandType commandType) {
        assert CommandType.parse(command) == commandType
                : "Command type must match the command text";
        return command.substring(commandType.getKeyword().length()).trim();
    }
}
