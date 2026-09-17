package sace;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user commands and converts their arguments into task data.
 */
public class Parser {
    private static final Pattern DEADLINE_BY_MARKER =
            Pattern.compile("(?i)(?:^|\\s)/by(?=\\s|$)");
    private static final Pattern EVENT_FROM_MARKER =
            Pattern.compile("(?i)(?:^|\\s)/from(?=\\s|$)");
    private static final Pattern EVENT_TO_MARKER =
            Pattern.compile("(?i)(?:^|\\s)/to(?=\\s|$)");

    /**
     * Identifies the supported kinds of user commands.
     */
    public enum CommandType {
        BYE,
        HELP,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        FIND,
        TODO,
        DEADLINE,
        EVENT
    }

    /**
     * Identifies which supported command the user entered.
     *
     * @param command full command entered by the user.
     * @return type of the command.
     * @throws SaceException if the command is blank or unknown.
     */
    public static CommandType parseCommandType(String command) throws SaceException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            throw new SaceException("Your command scroll is blank. Enter an order.");
        }

        String[] commandParts = trimmedCommand.split("\\s+", 2);
        String commandWord = commandParts[0].toLowerCase(Locale.ROOT);
        boolean hasArguments = commandParts.length == 2;
        if (commandWord.equals("bye") && !hasArguments) {
            return CommandType.BYE;
        } else if (commandWord.equals("help") && !hasArguments) {
            return CommandType.HELP;
        } else if (commandWord.equals("list") && !hasArguments) {
            return CommandType.LIST;
        } else if (commandWord.equals("mark")) {
            return CommandType.MARK;
        } else if (commandWord.equals("unmark")) {
            return CommandType.UNMARK;
        } else if (commandWord.equals("delete")) {
            return CommandType.DELETE;
        } else if (commandWord.equals("find")) {
            return CommandType.FIND;
        } else if (commandWord.equals("todo")) {
            return CommandType.TODO;
        } else if (commandWord.equals("deadline")) {
            return CommandType.DEADLINE;
        } else if (commandWord.equals("event")) {
            return CommandType.EVENT;
        }
        throw new SaceException("That order is not recorded in the battle manual.");
    }

    /**
     * Converts the number in a task operation command to a valid list index.
     *
     * @param command full command entered by the user.
     * @param action command word, such as {@code mark}, {@code unmark}, or {@code delete}.
     * @param taskCount current number of stored tasks.
     * @return zero-based index of the selected task.
     * @throws SaceException if the number is missing, invalid, or out of range.
     */
    public static int parseTaskIndex(String command, String action, int taskCount)
            throws SaceException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new SaceException("Name a quest number after " + action + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new SaceException(
                    "The quest number for " + action + " must be a whole number.");
        }

        if (taskCount == 0) {
            throw new SaceException(
                    "Your quest log is empty; there is nothing to " + action + ".");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new SaceException("Choose a quest number between 1 and " + taskCount + ".");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command full command entered by the user.
     * @return keyword to search for in task descriptions.
     * @throws SaceException if the keyword is missing.
     */
    public static String parseFindKeyword(String command) throws SaceException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new SaceException("The scouts need a keyword to begin their search.");
        }
        return keyword;
    }

    /**
     * Creates a task from an add command.
     *
     * @param command full command entered by the user.
     * @param commandType type of task creation command.
     * @return task described by the command.
     * @throws SaceException if required task information is missing or invalid.
     */
    public static Task parseTask(String command, CommandType commandType)
            throws SaceException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "parseTask requires a task-creation command";

        switch (commandType) {
            case TODO:
                return parseTodo(command);
            case DEADLINE:
                return parseDeadline(command);
            case EVENT:
                return parseEvent(command);
            default:
                throw new SaceException("That order does not create a quest.");
        }
    }

    /**
     * Creates a todo from a validated command.
     */
    private static Todo parseTodo(String command) throws SaceException {
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new SaceException("Every quest needs a description after todo.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline from a validated command.
     */
    private static Deadline parseDeadline(String command) throws SaceException {
        Matcher byMarker = DEADLINE_BY_MARKER.matcher(command);
        if (!byMarker.find()) {
            throw new SaceException(
                    "Use this formation: deadline DESCRIPTION /by yyyy-MM-dd.");
        }

        int byMarkerIndex = byMarker.start();
        int byValueIndex = byMarker.end();
        if (byMarker.find()) {
            throw new SaceException("A deadline accepts only one /by parameter.");
        }

        String description = command.substring("deadline".length(), byMarkerIndex).trim();
        String by = command.substring(byValueIndex).trim();
        if (description.isEmpty()) {
            throw new SaceException("Every deadline quest needs a description.");
        }
        if (by.isEmpty()) {
            throw new SaceException("A deadline needs a battle date after /by.");
        }

        try {
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException e) {
            throw new SaceException(
                    "Use a valid battle date in yyyy-MM-dd format, for example 2026-08-31.");
        }
    }

    /**
     * Creates an event from a validated command.
     */
    private static Event parseEvent(String command) throws SaceException {
        Matcher fromMarker = EVENT_FROM_MARKER.matcher(command);
        Matcher toMarker = EVENT_TO_MARKER.matcher(command);
        int fromMarkerIndex = -1;
        int fromValueIndex = -1;
        int toMarkerIndex = -1;
        int toValueIndex = -1;

        if (fromMarker.find()) {
            fromMarkerIndex = fromMarker.start();
            fromValueIndex = fromMarker.end();
            if (fromMarker.find()) {
                throw new SaceException("An event accepts only one /from parameter.");
            }
        }
        if (toMarker.find()) {
            toMarkerIndex = toMarker.start();
            toValueIndex = toMarker.end();
            if (toMarker.find()) {
                throw new SaceException("An event accepts only one /to parameter.");
            }
        }
        if (fromMarkerIndex < 0 || toMarkerIndex < 0 || toMarkerIndex <= fromMarkerIndex) {
            throw new SaceException(
                    "Use this formation: event DESCRIPTION /from START /to END.");
        }

        String description = command.substring("event".length(), fromMarkerIndex).trim();
        String from = command.substring(fromValueIndex, toMarkerIndex).trim();
        String to = command.substring(toValueIndex).trim();
        if (description.isEmpty()) {
            throw new SaceException("Every event quest needs a description.");
        }
        if (from.isEmpty()) {
            throw new SaceException("An event needs a starting time after /from.");
        }
        if (to.isEmpty()) {
            throw new SaceException("An event needs an ending time after /to.");
        }
        if (from.equalsIgnoreCase(to)) {
            throw new SaceException("An event must have different start and end times.");
        }
        return new Event(description, from, to);
    }
}
