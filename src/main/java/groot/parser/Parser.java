package groot.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import groot.exception.GrootException;
import groot.task.Deadline;
import groot.task.Event;
import groot.task.Task;
import groot.task.Todo;

/**
 * Interprets user input and converts command arguments into application objects.
 */
public class Parser {
    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";

    /**
     * Creates a parser for Groot commands.
     */
    public Parser() {
    }

    /**
     * Identifies and validates the command represented by the user's input.
     *
     * @param command Trimmed command entered by the user.
     * @return Recognized command type.
     * @throws GrootException If the command is empty or unknown.
     */
    public CommandType parseCommandType(String command) throws GrootException {
        if (command.isEmpty()) {
            throw new GrootException("Oops! Please enter a command.");
        }

        CommandType commandType = CommandType.from(command);
        if (commandType == CommandType.UNKNOWN) {
            throw new GrootException("Oops! I don't recognise that command.");
        }
        return commandType;
    }

    /**
     * Converts a task command into the corresponding task subtype.
     *
     * @param command Full command entered by the user.
     * @param commandType Type of task to create.
     * @return A todo, deadline, or event described by the command.
     * @throws GrootException If required task details are missing or invalid.
     */
    public Task createTask(String command, CommandType commandType) throws GrootException {
        return switch (commandType) {
            case TODO -> parseTodo(command);
            case DEADLINE -> parseDeadline(command);
            case EVENT -> parseEvent(command);
            default -> throw new IllegalArgumentException("Command type does not create a task: " + commandType);
        };
    }

    /**
     * Parses a todo command, requiring a non-empty description.
     */
    private Todo parseTodo(String command) throws GrootException {
        String description = command.substring(CommandType.TODO.getKeyword().length()).trim();
        if (description.isEmpty()) {
            throw new GrootException("Oops! A todo needs a description.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command, requiring a description and a valid ISO date.
     */
    private Deadline parseDeadline(String command) throws GrootException {
        String arguments = command.substring(CommandType.DEADLINE.getKeyword().length()).trim();
        int byIndex = arguments.indexOf(DEADLINE_MARKER);
        if (byIndex < 0) {
            throw new GrootException("Oops! Use: deadline DESCRIPTION /by DATE");
        }
        String description = arguments.substring(0, byIndex).trim();
        String dueDateText = arguments.substring(byIndex + DEADLINE_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new GrootException("Oops! A deadline needs a description.");
        }
        if (dueDateText.isEmpty()) {
            throw new GrootException("Oops! A deadline needs a date after /by.");
        }
        try {
            return new Deadline(description, LocalDate.parse(dueDateText));
        } catch (DateTimeParseException error) {
            throw new GrootException(
                    "Oops! Use deadline dates in yyyy-MM-dd format, e.g. 2019-10-15.");
        }
    }

    /**
     * Parses an event command, requiring both time markers and non-empty fields.
     */
    private Event parseEvent(String command) throws GrootException {
        String arguments = command.substring(CommandType.EVENT.getKeyword().length()).trim();
        int fromIndex = arguments.indexOf(EVENT_START_MARKER);
        int toIndex = fromIndex < 0 ? -1 : arguments.indexOf(EVENT_END_MARKER,
                fromIndex + EVENT_START_MARKER.length());
        if (fromIndex < 0 || toIndex < 0) {
            throw new GrootException("Oops! Use: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String start = arguments.substring(fromIndex + EVENT_START_MARKER.length(), toIndex).trim();
        String end = arguments.substring(toIndex + EVENT_END_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new GrootException("Oops! An event needs a description.");
        }
        if (start.isEmpty()) {
            throw new GrootException("Oops! An event needs a start date or time after /from.");
        }
        if (end.isEmpty()) {
            throw new GrootException("Oops! An event needs an end date or time after /to.");
        }
        return new Event(description, start, end);
    }

    /**
     * Parses and validates the one-based task number in a mark, unmark, or delete command.
     *
     * @param command Full command entered by the user.
     * @param commandType Type of command that selects a task.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based index of the selected task.
     * @throws GrootException If the task number is missing, non-numeric, or out of range.
     */
    public int parseTaskIndex(String command, CommandType commandType, int taskCount)
            throws GrootException {
        // The caller supplies a collection size, which can never be negative even for an empty list.
        assert taskCount >= 0 : "Task count must not be negative";
        String action = commandType.getKeyword();
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new GrootException("Oops! Tell me which task to " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException error) {
            throw new GrootException("Oops! The task number must be a whole number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new GrootException("Oops! Task " + taskNumber + " is not in the list.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses the optional reverse flag of a recognized sort command.
     *
     * @param command Full sort command, allowing case variations, spaces, and tabs.
     * @return Whether deadlines should be displayed latest first.
     * @throws GrootException If an unsupported or additional argument is present.
     */
    public boolean parseSortReverseFlag(String command) throws GrootException {
        String[] tokens = command.trim().split("[ \\t]+");
        if (tokens.length == 1) {
            return false;
        }
        if (tokens.length == 2 && (tokens[1].equalsIgnoreCase("-r")
                || tokens[1].equalsIgnoreCase("--reverse"))) {
            return true;
        }
        throw new GrootException("Oops! Use: sort [-r | --reverse].");
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command Full command entered by the user.
     * @return Non-empty keyword to match against task descriptions.
     * @throws GrootException If no keyword follows the find command.
     */
    public String parseFindKeyword(String command) throws GrootException {
        String keyword = command.substring(CommandType.FIND.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new GrootException("Oops! Tell me what to find.");
        }
        return keyword;
    }
}
