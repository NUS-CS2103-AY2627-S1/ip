package yawned.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import yawned.exception.YawnedException;
import yawned.task.Deadline;
import yawned.task.Event;
import yawned.task.Task;
import yawned.task.ToDo;

/**
 * Interprets user commands and creates tasks from task-creation commands.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Identifies the type of user command.
     *
     * @param userInput Complete user command.
     * @return Matching command type, or {@link CommandType#UNKNOWN}.
     */
    public CommandType parseCommandType(String userInput) {
        return CommandType.fromInput(userInput);
    }

    /**
     * Creates the task described by a task-creation command.
     *
     * @param commandType Type of task to create.
     * @param command Complete user command.
     * @return The created task.
     * @throws YawnedException If the command is incomplete or unknown.
     * @throws IllegalArgumentException If {@code commandType} is not a task-creation command.
     */
    public Task parseTask(CommandType commandType, String command) throws YawnedException {
        String details = getCommandArguments(command);
        return switch (commandType) {
            case TODO -> createToDo(details);
            case DEADLINE -> createDeadline(details);
            case EVENT -> createEvent(details);
            case UNKNOWN -> throw new YawnedException("*yawn* I don't recognize that command. "
                    + "Try todo, deadline, event, list, mark, unmark, delete, find, or alias.");
            default -> throw new IllegalArgumentException("Cannot create a task from command type: " + commandType);
        };
    }

    /**
     * Extracts a one-based task number from a task-selection command.
     *
     * @param commandType Type of task-selection command.
     * @param command Complete user command.
     * @return Parsed task number; range validation is performed by the task list caller.
     * @throws YawnedException If no whole-number task number was provided.
     */
    public int parseTaskNumber(CommandType commandType, String command) throws YawnedException {
        String taskNumberText = getCommandArguments(command);
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new YawnedException(missingTaskNumberMessage(commandType));
        }
    }

    /**
     * Extracts the keyword from a {@code find <keyword>} command.
     *
     * @param command complete user command
     * @return search keyword
     * @throws YawnedException if no keyword was provided
     */
    public String parseFindKeyword(String command) throws YawnedException {
        String keyword = getCommandArguments(command);
        if (keyword.isEmpty()) {
            throw new YawnedException("I need something to search for. For example: find book");
        }
        return keyword;
    }

    /**
     * Parses an alias listing, creation, or removal command.
     *
     * @param command Complete alias command.
     * @return Parsed alias command.
     * @throws YawnedException If the command does not contain exactly the required arguments.
     */
    public AliasCommand parseAliasCommand(String command) throws YawnedException {
        String details = getCommandArguments(command);
        String[] arguments = details.split(" +");
        if (arguments[0].equals("list")) {
            if (arguments.length == 1) {
                return new AliasCommand("", "", AliasAction.LIST);
            }
            throw new YawnedException("I need an alias command. Use: alias list, alias <name> <command>, "
                    + "or alias remove <name>.");
        }
        if (arguments.length != 2) {
            throw new YawnedException("I need an alias command. Use: alias list, alias <name> <command>, "
                    + "or alias remove <name>.");
        }
        if (arguments[0].equals("remove")) {
            return new AliasCommand(arguments[1], "", AliasAction.REMOVE);
        }
        return new AliasCommand(arguments[0], arguments[1], AliasAction.DEFINE);
    }

    /** Returns the text after the command word supplied by the user. */
    private static String getCommandArguments(String command) {
        int firstSpaceIndex = command.indexOf(' ');
        if (firstSpaceIndex < 0) {
            return "";
        }
        return command.substring(firstSpaceIndex + 1).trim();
    }

    /** Returns the appropriate task-number validation message for a command. */
    private static String missingTaskNumberMessage(CommandType commandType) {
        return switch (commandType) {
            case DELETE -> "Which task should I delete? For example: delete 2";
            case MARK -> "Which task should I mark? For example: mark 2";
            case UNMARK -> "Which task should I unmark? For example: unmark 2";
            default -> throw new IllegalArgumentException("Cannot select a task from command type: " + commandType);
        };
    }

    /** Creates a to-do from its command details. */
    private static ToDo createToDo(String details) throws YawnedException {
        if (details.isEmpty()) {
            throw new YawnedException("I need a task description before I can save it. For example: todo buy milk");
        }
        return new ToDo(details);
    }

    /** Creates a deadline from its command details. */
    private static Deadline createDeadline(String details) throws YawnedException {
        int byIndex = details.indexOf(" /by");
        if (details.isEmpty() || details.startsWith("/by")) {
            throw new YawnedException("I need a deadline description before I can save it. "
                    + "Use: deadline <description> /by yyyy-MM-dd HHmm");
        }
        if (byIndex < 0) {
            throw new YawnedException("I need a /by date and time in yyyy-MM-dd HHmm format. "
                    + "Example: deadline submit report /by 2026-01-01 0900");
        }
        assert byIndex > 0 : "A validated deadline must contain a description before /by.";
        String endDate = details.substring(byIndex + " /by".length()).trim();
        if (endDate.isEmpty()) {
            throw new YawnedException("I need a /by date and time in yyyy-MM-dd HHmm format. "
                    + "Example: deadline submit report /by 2026-01-01 0900");
        }
        String description = details.substring(0, byIndex).trim();
        assert !description.isEmpty() : "A validated deadline must have a description.";
        return new Deadline(description, parseDateTime(endDate));
    }

    /** Creates an event from its command details. */
    private static Event createEvent(String details) throws YawnedException {
        int fromIndex = details.indexOf(" /from");
        if (details.isEmpty() || details.startsWith("/from") || details.startsWith("/to")) {
            throw new YawnedException("I need an event description before I can save it. "
                    + "Use: event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm");
        }
        if (fromIndex < 0) {
            throw new YawnedException("I need /from and /to times in yyyy-MM-dd HHmm format. "
                    + "Example: event meeting /from 2026-01-01 0900 /to 2026-01-01 1000");
        }
        assert fromIndex > 0 : "A validated event must contain a description before /from.";
        int toIndex = details.indexOf(" /to", fromIndex + " /from".length());
        if (toIndex < 0) {
            throw new YawnedException("I need /from and /to times in yyyy-MM-dd HHmm format. "
                    + "Example: event meeting /from 2026-01-01 0900 /to 2026-01-01 1000");
        }
        assert toIndex > fromIndex : "The /to marker must follow the /from marker.";
        String fromDate = details.substring(fromIndex + " /from".length(), toIndex).trim();
        String toDate = details.substring(toIndex + " /to".length()).trim();
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            throw new YawnedException("I need /from and /to times in yyyy-MM-dd HHmm format. "
                    + "Example: event meeting /from 2026-01-01 0900 /to 2026-01-01 1000");
        }
        String description = details.substring(0, fromIndex).trim();
        assert !description.isEmpty() : "A validated event must have a description.";
        return new Event(description, parseDateTime(fromDate), parseDateTime(toDate));
    }

    /**
     * Parses a date and time written in {@code yyyy-MM-dd HHmm} format.
     *
     * @param dateTimeText Date and time text to parse.
     * @return Parsed date and time.
     * @throws YawnedException If the input is not a valid date and time.
     */
    private static LocalDateTime parseDateTime(String dateTimeText) throws YawnedException {
        try {
            return LocalDateTime.parse(dateTimeText, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new YawnedException("I need a valid date and time in yyyy-MM-dd HHmm format. *yawn*");
        }
    }
}
