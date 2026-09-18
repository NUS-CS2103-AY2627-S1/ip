package bobby.parser;

import java.time.LocalDateTime;

import bobby.BobbyException;
import bobby.task.Deadline;
import bobby.task.Event;
import bobby.task.Task;
import bobby.task.TaskList;
import bobby.task.Todo;
import bobby.util.DateTimeParser;

/**
 * Parses user commands into task operations or task objects.
 */
public class Parser {
    private static final String MARK_COMMAND = "mark";
    private static final String UNMARK_COMMAND = "unmark";
    private static final String DELETE_COMMAND = "delete";
    private static final String TAG_COMMAND = "tag";
    private static final String FIND_COMMAND = "find";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String BY_SEPARATOR = " /by";
    private static final String FROM_SEPARATOR = " /from";
    private static final String TO_SEPARATOR = " /to";
    private static final int REQUIRED_TASK_NUMBER_PARTS = 1;
    private static final int TAG_COMMAND_PARTS = 2;

    /**
     * Returns whether the input is the command word or starts with the command word followed by a space.
     *
     * @param command user input.
     * @param commandWord command word to test.
     * @return true if the command matches.
     */
    public static boolean isCommand(String command, String commandWord) {
        assert command != null : "Command should be non-null before matching.";
        assert commandWord != null : "Command word should be non-null before matching.";

        String normalizedCommand = normalizeCommand(command);
        return normalizedCommand.equals(commandWord) || normalizedCommand.startsWith(commandWord + " ");
    }

    /**
     * Returns whether the input is a mark command.
     *
     * @param command user input.
     * @return true if this is a mark command.
     */
    public static boolean isMark(String command) {
        return isCommand(command, MARK_COMMAND);
    }

    /**
     * Returns whether the input is an unmark command.
     *
     * @param command user input.
     * @return true if this is an unmark command.
     */
    public static boolean isUnmark(String command) {
        return isCommand(command, UNMARK_COMMAND);
    }

    /**
     * Returns whether the input is a delete command.
     *
     * @param command user input.
     * @return true if this is a delete command.
     */
    public static boolean isDelete(String command) {
        return isCommand(command, DELETE_COMMAND);
    }

    /**
     * Returns whether the input is a tag command.
     *
     * @param command user input.
     * @return true if this is a tag command.
     */
    public static boolean isTag(String command) {
        return isCommand(command, TAG_COMMAND);
    }

    /**
     * Returns whether the input is a find command.
     *
     * @param command user input
     * @return true if this is a find command
     */
    public static boolean isFind(String command) {
        return isCommand(command, FIND_COMMAND);
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param command full user command
     * @return keyword to search for
     * @throws BobbyException if the keyword is missing
     */
    public static String parseFindKeyword(String command) throws BobbyException {
        command = normalizeCommand(command);
        return getRequiredSegment(command, FIND_COMMAND.length(), command.length(),
                "Please provide a keyword after find.");
    }

    /**
     * Parses the one-based task number in a command into a zero-based index.
     *
     * @param command full user command.
     * @param commandWord command word before the task number.
     * @param taskList current task list.
     * @return zero-based task index.
     * @throws BobbyException if the task number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, String commandWord, TaskList taskList) throws BobbyException {
        command = normalizeCommand(command);
        String arguments = getRequiredSegment(command, commandWord.length(), command.length(),
                "Please provide a task number after " + commandWord + ".");
        String[] parts = arguments.split(" ");
        if (parts.length != REQUIRED_TASK_NUMBER_PARTS) {
            throw new BobbyException("Please provide only one task number after " + commandWord + ".");
        }
        String taskNumber = parts[0];
        if (taskNumber.isEmpty()) {
            throw new BobbyException("Please provide a task number after " + commandWord + ".");
        }
        try {
            int taskIndex = Integer.parseInt(taskNumber) - 1;
            if (!taskList.isValidIndex(taskIndex)) {
                throw new BobbyException("I couldn't find that task number.");
            }
            assert taskIndex >= 0 : "Parsed task index should be zero or positive.";
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new BobbyException("Task numbers should be whole numbers.");
        }
    }

    /**
     * Parses a tag command into the selected task index and tag text.
     *
     * @param command full user command.
     * @param taskList current task list.
     * @return parsed tag command.
     * @throws BobbyException if the task number or tag is invalid.
     */
    public static TagCommand parseTagCommand(String command, TaskList taskList) throws BobbyException {
        command = normalizeCommand(command);
        String arguments = getRequiredSegment(command, TAG_COMMAND.length(), command.length(),
                "Please provide a task number and tag after tag.");
        String[] parts = arguments.split(" ");
        if (parts.length != TAG_COMMAND_PARTS) {
            throw new BobbyException("Please provide a task number and tag after tag.");
        }

        int taskIndex = parseTaskIndex(TAG_COMMAND + " " + parts[0], TAG_COMMAND, taskList);
        String tag = parts[1];
        if (!Task.isValidTag(tag)) {
            throw new BobbyException("Tags should start with # and use only letters, numbers, underscores, "
                    + "or hyphens.");
        }
        return new TagCommand(taskIndex, tag);
    }

    /**
     * Parses a task-creating command.
     *
     * @param command user input.
     * @return task represented by the command.
     * @throws BobbyException if the command is invalid or unknown.
     */
    public static Task parseTask(String command) throws BobbyException {
        command = normalizeCommand(command);
        if (isCommand(command, TODO_COMMAND)) {
            return new Todo(getDescription(command, TODO_COMMAND, "todo"));
        } else if (isCommand(command, DEADLINE_COMMAND)) {
            return parseDeadline(command);
        } else if (isCommand(command, EVENT_COMMAND)) {
            return parseEvent(command);
        } else {
            throw new BobbyException("I don't know what that means yet.");
        }
    }

    /**
     * Parses a deadline command after confirming that it starts with the deadline command word.
     *
     * @param command full deadline command.
     * @return deadline task represented by the command.
     * @throws BobbyException if the description or /by field is missing or invalid.
     */
    private static Deadline parseDeadline(String command) throws BobbyException {
        assert isCommand(command, DEADLINE_COMMAND) : "Deadline parser should receive a deadline command.";

        requireSingleSeparator(command, BY_SEPARATOR, "/by", "Please tell me the deadline using /by.");
        int byIndex = command.indexOf(BY_SEPARATOR);
        String description = getRequiredSegment(command, DEADLINE_COMMAND.length(), byIndex,
                "The description of a deadline cannot be empty.");
        validateDescription(description);
        String by = getRequiredSegment(command, byIndex + BY_SEPARATOR.length(), command.length(),
                "The /by part of a deadline cannot be empty.");
        return new Deadline(description, DateTimeParser.parse(by));
    }

    /**
     * Parses an event command after confirming that it starts with the event command word.
     *
     * @param command full event command.
     * @return event task represented by the command.
     * @throws BobbyException if the description, /from field, or /to field is missing or invalid.
     */
    private static Event parseEvent(String command) throws BobbyException {
        assert isCommand(command, EVENT_COMMAND) : "Event parser should receive an event command.";

        requireSingleSeparator(command, FROM_SEPARATOR, "/from",
                "Please tell me the event time using /from and /to.");
        requireSingleSeparator(command, TO_SEPARATOR, "/to",
                "Please tell me the event time using /from and /to.");
        int fromIndex = command.indexOf(FROM_SEPARATOR);
        int toIndex = command.indexOf(TO_SEPARATOR);
        if (toIndex < fromIndex) {
            throw new BobbyException("Please tell me the event time using /from and /to.");
        }
        String description = getRequiredSegment(command, EVENT_COMMAND.length(), fromIndex,
                "The description of an event cannot be empty.");
        validateDescription(description);
        String from = getRequiredSegment(command, fromIndex + FROM_SEPARATOR.length(), toIndex,
                "The /from part of an event cannot be empty.");
        String to = getRequiredSegment(command, toIndex + TO_SEPARATOR.length(), command.length(),
                "The /to part of an event cannot be empty.");
        LocalDateTime fromDateTime = DateTimeParser.parse(from);
        LocalDateTime toDateTime = DateTimeParser.parse(to);
        if (!fromDateTime.isBefore(toDateTime)) {
            throw new BobbyException("The event start time should be before the end time.");
        }
        return new Event(description, fromDateTime, toDateTime);
    }

    /**
     * Extracts and validates the description after a task command word.
     *
     * @param command full task command.
     * @param commandWord command word before the description.
     * @param taskType task type name used in the error message.
     * @return non-empty task description.
     * @throws BobbyException if the description is empty.
     */
    private static String getDescription(String command, String commandWord, String taskType) throws BobbyException {
        String description = getRequiredSegment(command, commandWord.length(), command.length(),
                "The description of a " + taskType + " cannot be empty.");
        validateDescription(description);
        return description;
    }

    /**
     * Extracts and validates a required command segment.
     *
     * @param command full user command.
     * @param startIndex inclusive start index of the segment.
     * @param endIndex exclusive end index of the segment.
     * @param emptyMessage error shown if the segment is empty.
     * @return trimmed non-empty command segment.
     * @throws BobbyException if the segment is empty.
     */
    private static String getRequiredSegment(String command, int startIndex, int endIndex, String emptyMessage)
            throws BobbyException {
        String segment = command.substring(startIndex, endIndex).trim();
        if (segment.isEmpty()) {
            throw new BobbyException(emptyMessage);
        }
        return segment;
    }

    /**
     * Returns a command with leading/trailing and repeated whitespace normalized.
     *
     * @param command raw user command.
     * @return command with single spaces between tokens.
     */
    public static String normalizeCommand(String command) {
        assert command != null : "Command should be non-null before normalizing.";

        return command.trim().replaceAll("\\s+", " ");
    }

    private static void requireSingleSeparator(String command, String separator, String userFacingName,
            String missingMessage) throws BobbyException {
        int firstIndex = command.indexOf(separator);
        if (firstIndex == -1) {
            throw new BobbyException(missingMessage);
        }
        if (command.indexOf(separator, firstIndex + separator.length()) != -1) {
            throw new BobbyException("Please include " + userFacingName + " only once.");
        }
    }

    private static void validateDescription(String description) throws BobbyException {
        if (!Task.isValidDescription(description)) {
            throw new BobbyException("Descriptions cannot contain | or control characters.");
        }
    }

    /**
     * Represents a parsed tag command.
     *
     * @param taskIndex zero-based task index.
     * @param tag tag text, including the leading #.
     */
    public record TagCommand(int taskIndex, String tag) {
    }
}
