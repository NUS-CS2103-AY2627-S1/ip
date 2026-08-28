import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Interprets command text and validates arguments without reading input or changing the task list. */
public final class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE = "Sir, I don't know what you are saying :-(";

    /** The supported actions; execution belongs to Friday rather than the parser. */
    public enum CommandType {
        BYE, HELLO, THANKS, HELP, LIST, TODO, DEADLINE, EVENT, ON, DELETE, MARK, UNMARK
    }

    private Parser() {
        // Parsing has no state, so callers use the static methods directly.
    }

    /**
     * Identifies a command while preserving the existing case and whitespace rules.
     * Commands without arguments must match exactly; others use a space after the command word.
     *
     * @throws IllegalArgumentException if the command is unknown
     */
    public static CommandType parseCommandType(String command) {
        int firstSpace = command.indexOf(' ');
        String word = firstSpace == -1 ? command : command.substring(0, firstSpace);
        CommandType type = switch (word) {
        case "bye" -> CommandType.BYE;
        case "hello" -> CommandType.HELLO;
        case "thanks" -> CommandType.THANKS;
        case "help" -> CommandType.HELP;
        case "list" -> CommandType.LIST;
        case "todo" -> CommandType.TODO;
        case "deadline" -> CommandType.DEADLINE;
        case "event" -> CommandType.EVENT;
        case "on" -> CommandType.ON;
        case "delete" -> CommandType.DELETE;
        case "mark" -> CommandType.MARK;
        case "unmark" -> CommandType.UNMARK;
        default -> throw new IllegalArgumentException(UNKNOWN_COMMAND_MESSAGE);
        };
        switch (type) {
        case BYE, HELLO, THANKS, HELP, LIST -> {
            if (!command.equals(word)) {
                throw new IllegalArgumentException(UNKNOWN_COMMAND_MESSAGE);
            }
        }
        default -> {
            // Argument validation happens in the corresponding parsing method.
        }
        }
        return type;
    }

    /**
     * Creates a task from a todo, deadline, or event command, without adding it to a list.
     * Task constructors validate calendar dates and event ordering.
     *
     * @throws IllegalArgumentException if required fields, dates, or the command type are invalid
     */
    public static Task parseTask(String command) {
        return switch (parseCommandType(command)) {
        case TODO -> {
            String description = parseCommandBody(command, "todo ");
            if (description.isEmpty()) {
                throw new IllegalArgumentException("Sir, description of a todo cannot be empty.");
            }
            yield new ToDo(description);
        }
        case DEADLINE -> {
            String body = parseCommandBody(command, "deadline ");
            String description = parseTextBefore(body, " /by ");
            String by = parseTextAfter(body, " /by ");
            if (description.isEmpty() || by.isEmpty()) {
                throw new IllegalArgumentException("Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE");
            }
            yield new Deadline(description, by);
        }
        case EVENT -> {
            String body = parseCommandBody(command, "event ");
            String description = parseTextBefore(body, " /from ");
            String fromAndTo = parseTextAfter(body, " /from ");
            String from = parseTextBefore(fromAndTo, " /to ");
            String to = parseTextAfter(fromAndTo, " /to ");
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new IllegalArgumentException("Invalid event format. Use: event DESCRIPTION /from START /to END");
            }
            yield new Event(description, from, to);
        }
        default -> throw new IllegalArgumentException("This command does not add a task.");
        };
    }

    /** Parses an on command's ISO date and reports the existing command-specific error on failure. */
    public static LocalDate parseDate(String command) {
        try {
            return LocalDate.parse(parseCommandBody(command, "on "));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).", e);
        }
    }

    /**
     * Parses a mark, unmark, or delete task number; TaskList checks whether that number exists.
     * Preserves the previous format error for -1, which was the old parser's failure sentinel.
     *
     * @throws IllegalArgumentException if the argument is missing, nonnumeric, -1, or outside the integer range
     */
    public static int parseTaskNumber(String command) {
        String errorMessage = switch (parseCommandType(command)) {
        case MARK -> "Sir, Invalid mark format. Use: mark TASK_NUMBER";
        case UNMARK -> "Sir, Invalid unmark format. Use: unmark TASK_NUMBER";
        case DELETE -> "Sir, Invalid delete format. Use: delete TASK_NUMBER";
        default -> throw new IllegalArgumentException("This command does not select a task number.");
        };
        int firstSpace = command.indexOf(' ');
        String argument = firstSpace == -1 ? "" : command.substring(firstSpace + 1).trim();
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage, e);
        }
        if (taskNumber == -1) {
            throw new IllegalArgumentException(errorMessage);
        }
        return taskNumber;
    }

    /** Returns the trimmed command body, including an empty body for a missing argument. */
    private static String parseCommandBody(String command, String prefix) {
        if (!command.startsWith(prefix)) {
            return "";
        }
        return command.substring(prefix.length()).trim();
    }

    /** Returns text before the first delimiter, or an empty string when it is missing. */
    private static String parseTextBefore(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(0, delimiterIndex).trim();
    }

    /** Returns text after the first delimiter, or an empty string when it is missing. */
    private static String parseTextAfter(String text, String delimiter) {
        int delimiterIndex = text.indexOf(delimiter);
        if (delimiterIndex == -1) {
            return "";
        }
        return text.substring(delimiterIndex + delimiter.length()).trim();
    }
}
