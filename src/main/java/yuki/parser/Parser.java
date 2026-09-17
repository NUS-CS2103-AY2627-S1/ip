package yuki.parser;

import java.time.Clock;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yuki.command.AddCommand;
import yuki.command.Command;
import yuki.command.DeleteCommand;
import yuki.command.ExitCommand;
import yuki.command.FindCommand;
import yuki.command.ListCommand;
import yuki.command.MarkCommand;
import yuki.command.RemindersCommand;
import yuki.command.UnmarkCommand;
import yuki.exception.YukiException;
import yuki.task.Deadline;
import yuki.task.Event;
import yuki.task.Task;
import yuki.task.ToDo;
import yuki.time.DateTimeParser;
import yuki.time.TaskDateTime;

/**
 * Interprets user commands and converts their arguments into values used by Yuki.
 */
public final class Parser {
    /** Matches a command parameter that is separated from its value by whitespace. */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/(by|from|to)(?!\\S)");

    /** Prevents creation of this stateless utility class. */
    private Parser() {
    }

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param command The complete command entered by the user.
     * @return The command object representing the user's instruction.
     * @throws YukiException If the command or its arguments are invalid.
     */
    public static Command parse(String command) {
        return parse(command, Clock.systemDefaultZone());
    }

    /**
     * Converts a complete user command using the supplied clock.
     *
     * @param command The complete command entered by the user.
     * @param clock Clock used by commands that depend on the current time.
     * @return The command object representing the user's instruction.
     * @throws YukiException If the command or its arguments are invalid.
     */
    public static Command parse(String command, Clock clock) {
        Objects.requireNonNull(clock, "clock must not be null");
        validateCommandText(command);
        CommandType commandType = parseCommandType(command);

        return switch (commandType) {
            case TODO, DEADLINE, EVENT -> new AddCommand(createTask(command, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(command, commandType));
            case MARK -> new MarkCommand(parseTaskNumber(command, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(command, commandType));
            case FIND -> new FindCommand(parseKeyword(command, commandType));
            case REMINDERS -> {
                validateRemindersCommand(command, commandType);
                yield new RemindersCommand(clock);
            }
            case LIST -> {
                validateNoArguments(command, commandType);
                yield new ListCommand();
            }
            case BYE -> {
                validateNoArguments(command, commandType);
                yield new ExitCommand();
            }
        };
    }

    /**
     * Identifies the type of a command from its first word.
     *
     * @param command The complete command entered by the user.
     * @return The matching command type.
     * @throws YukiException If the command is empty or unsupported.
     */
    private static CommandType parseCommandType(String command) {
        String normalizedCommand = command.trim();
        if (normalizedCommand.isEmpty()) {
            throw new YukiException("No command was entered. Please enter a command.");
        }

        String[] commandParts = normalizedCommand.split("\\s+", 2);
        String commandWord = commandParts[0];

        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.getKeyword().equals(commandWord))
                .findFirst()
                .orElseThrow(() -> new YukiException("That command isn't familiar to me."));
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @return The task described by the command.
     * @throws YukiException If the command does not contain the required arguments.
     */
    private static Task createTask(String command, CommandType commandType) {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "createTask requires a task-creation command type";

        String arguments = command.trim()
                .substring(commandType.getKeyword().length())
                .trim();

        return switch (commandType) {
            case TODO -> createTodo(arguments);
            case DEADLINE -> createDeadline(arguments);
            case EVENT -> createEvent(arguments);
            default -> throw new YukiException("That command isn't familiar to me.");
        };
    }

    /** Creates a to-do task from its command arguments. */
    private static ToDo createTodo(String description) {
        if (description.isBlank()) {
            throw new YukiException(
                    "The todo description is missing. Please add a task description after 'todo'.");
        }
        return new ToDo(normalizeWhitespace(description));
    }

    /** Creates a deadline task from its command arguments. */
    private static Deadline createDeadline(String arguments) {
        String usageMessage = "The deadline needs one description and one '/by' date. For example: "
                + "deadline return book /by 26/8/2026 1800.";
        String[] descriptionAndDate = splitParameters(arguments, "/by", usageMessage);
        String description = normalizeWhitespace(descriptionAndDate[0]);
        TaskDateTime deadline = DateTimeParser.parse(descriptionAndDate[1]);
        return new Deadline(description, deadline);
    }

    /** Creates an event task from its command arguments. */
    private static Event createEvent(String arguments) {
        String usageMessage = "The event needs one description, one '/from' value and one '/to' value. "
                + "For example: event meeting /from 26/8/2026 1800 /to 26/8/2026 2000.";
        String[] descriptionAndTimes = splitParameters(arguments, "/from", usageMessage);
        String[] times = splitParameters(descriptionAndTimes[1], "/to", usageMessage);
        if (containsParameter(descriptionAndTimes[0]) || containsParameter(times[1])) {
            throw new YukiException(usageMessage);
        }

        TaskDateTime from = DateTimeParser.parse(times[0]);
        TaskDateTime to = DateTimeParser.parse(times[1]);
        boolean isFromParsed = from.hasDateTime() || from.hasDateOnly();
        boolean isToParsed = to.hasDateTime() || to.hasDateOnly();
        if (isFromParsed && isToParsed && !from.hasComparableType(to)) {
            throw new YukiException(
                    "The event start and end must either both include a time or both omit it.");
        }
        if (from.hasComparableType(to) && !from.isBefore(to)) {
            throw new YukiException("The event's end must be later than its start.");
        }

        return new Event(normalizeWhitespace(descriptionAndTimes[0]), from, to);
    }

    /**
     * Parses the task number following a command keyword.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @return The parsed task number.
     * @throws YukiException If the task number is missing or is not an integer.
     */
    private static int parseTaskNumber(String command, CommandType commandType) {
        assert commandType == CommandType.DELETE
                || commandType == CommandType.MARK
                || commandType == CommandType.UNMARK
                : "parseTaskNumber requires a numbered command type";

        String normalizedCommand = command.trim();
        if (normalizedCommand.equals(commandType.getKeyword())) {
            throw new YukiException("The task number is missing.");
        }

        String taskNumberText = normalizedCommand
                .substring(commandType.getKeyword().length())
                .trim();
        if (!taskNumberText.matches("[1-9][0-9]*")) {
            throw new YukiException("The task number must be a positive integer.");
        }
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new YukiException("The task number is too large.");
        }
    }

    /**
     * Returns the non-blank keyword following a find command.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified find command type.
     * @return The trimmed keyword or phrase to search for.
     * @throws YukiException If the keyword is missing.
     */
    private static String parseKeyword(String command, CommandType commandType) {
        String keyword = command.trim()
                .substring(commandType.getKeyword().length())
                .trim();
        if (keyword.isBlank()) {
            throw new YukiException("The keyword is missing. Please enter a keyword after 'find'.");
        }
        return normalizeWhitespace(keyword);
    }

    /**
     * Checks that a command which takes no arguments contains only its keyword.
     *
     * @param command The complete command entered by the user.
     * @param commandType The already identified command type.
     * @throws YukiException If additional text follows the command keyword.
     */
    private static void validateNoArguments(String command, CommandType commandType) {
        if (!command.trim().equals(commandType.getKeyword())) {
            throw new YukiException(
                    "There’s no need to add anything else to the "
                            + commandType.getKeyword() + " command.");
        }
    }

    /** Checks that a reminders command contains only its keyword. */
    private static void validateRemindersCommand(String command, CommandType commandType) {
        if (!command.trim().equals(commandType.getKeyword())) {
            throw new YukiException(
                    "There’s no need to add anything else to the reminders command.");
        }
    }

    /** Rejects command values that cannot safely be parsed or saved. */
    private static void validateCommandText(String command) {
        if (command == null) {
            throw new YukiException("No command was entered. Please enter a command.");
        }
        if (command.chars().anyMatch(character -> character == '\r' || character == '\n'
                || (Character.isISOControl(character) && !Character.isWhitespace(character)))) {
            throw new YukiException("A command must be entered on one line without control characters.");
        }
    }

    /** Splits text around exactly one occurrence of the requested parameter. */
    private static String[] splitParameters(String text, String parameter, String usageMessage) {
        Matcher matcher = PARAMETER_PATTERN.matcher(text);
        int matchStart = -1;
        int matchEnd = -1;
        int parameterCount = 0;
        while (matcher.find()) {
            if (matcher.group().equals(parameter)) {
                parameterCount++;
                matchStart = matcher.start();
                matchEnd = matcher.end();
            }
        }
        if (parameterCount != 1) {
            throw new YukiException(usageMessage);
        }

        String before = text.substring(0, matchStart).trim();
        String after = text.substring(matchEnd).trim();
        if (before.isEmpty() || after.isEmpty()) {
            throw new YukiException(usageMessage);
        }
        return new String[] {before, after};
    }

    /** Returns whether text contains a recognized task parameter. */
    private static boolean containsParameter(String text) {
        return PARAMETER_PATTERN.matcher(text).find();
    }

    /** Collapses user-entered whitespace into single spaces for stable comparisons and storage. */
    private static String normalizeWhitespace(String text) {
        return text.trim().replaceAll("\\s+", " ");
    }
}
