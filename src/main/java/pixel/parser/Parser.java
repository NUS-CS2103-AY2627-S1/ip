package pixel.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pixel.command.AddCommand;
import pixel.command.Command;
import pixel.command.CommandType;
import pixel.command.DateCommand;
import pixel.command.DeleteCommand;
import pixel.command.ExitCommand;
import pixel.command.FindCommand;
import pixel.command.ListCommand;
import pixel.command.MarkCommand;
import pixel.command.UnknownCommand;
import pixel.command.UnmarkCommand;
import pixel.command.UpdateCommand;
import pixel.command.UpdateField;
import pixel.task.Deadline;
import pixel.task.Event;
import pixel.task.Task;
import pixel.task.Todo;

/**
 * Interprets raw user commands and converts their arguments into domain values.
 */
public class Parser {

    /** Creates a parser for Pixel commands. */
    public Parser() {
    }

    /**
     * Parses a full user command into an executable command object.
     *
     * @param command Full command entered by the user.
     * @return Command ready to execute.
     * @throws IllegalArgumentException If a recognized command has invalid arguments.
     */
    public Command parse(String command) {
        if (command == null) {
            throw new IllegalArgumentException("Oops! The command cannot be null.");
        }
        command = command.trim();
        CommandType commandType = parseCommandType(command);
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case DATE -> new DateCommand(parseDate(command));
            case FIND -> new FindCommand(parseFindKeyword(command));
            case MARK -> new MarkCommand(parseTaskIndex(command, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskIndex(command, commandType));
            case TODO, DEADLINE, EVENT -> new AddCommand(parseTask(command, commandType));
            case DELETE -> new DeleteCommand(parseTaskIndex(command, commandType));
            case UPDATE -> parseUpdate(command);
            case UNKNOWN -> new UnknownCommand();
        };
    }

    /**
     * Identifies the type of a command while preserving Pixel's command syntax.
     *
     * @param command Full command entered by the user.
     * @return Matching command type, or {@link CommandType#UNKNOWN} when unrecognized.
     */
    private CommandType parseCommandType(String command) {
        if (command.equals("bye")) {
            return CommandType.BYE;
        } else if (command.equals("list")) {
            return CommandType.LIST;
        } else if (hasKeyword(command, "date")) {
            return CommandType.DATE;
        } else if (hasKeyword(command, "find")) {
            return CommandType.FIND;
        } else if (hasKeyword(command, "mark")) {
            return CommandType.MARK;
        } else if (hasKeyword(command, "unmark")) {
            return CommandType.UNMARK;
        } else if (hasKeyword(command, "todo")) {
            return CommandType.TODO;
        } else if (hasKeyword(command, "deadline")) {
            return CommandType.DEADLINE;
        } else if (hasKeyword(command, "event")) {
            return CommandType.EVENT;
        } else if (hasKeyword(command, "delete")) {
            return CommandType.DELETE;
        } else if (hasKeyword(command, "update")) {
            return CommandType.UPDATE;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses the date argument of a {@code date} command.
     *
     * @param command Full date command.
     * @return Parsed date.
     * @throws IllegalArgumentException If the date is missing or invalid.
     */
    private LocalDate parseDate(String command) {
        try {
            return LocalDate.parse(getArguments(command, "date"));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Oops! Please enter a valid date after date "
                    + "in YYYY-MM-DD format.", exception);
        }
    }

    /**
     * Parses the keyword supplied to a find command.
     *
     * @param command Full find command.
     * @return Search keyword.
     * @throws IllegalArgumentException If no keyword is supplied.
     */
    private String parseFindKeyword(String command) {
        String keyword = getArguments(command, "find");
        if (keyword.isEmpty()) {
            throw new IllegalArgumentException(
                    "Oops! Please specify what you want me to find.");
        }
        return keyword;
    }

    /**
     * Parses a one-based task number and converts it to a zero-based index.
     *
     * @param command Full mark, unmark, or delete command.
     * @param commandType Type of the command being parsed.
     * @return Zero-based task index.
     * @throws IllegalArgumentException If the task number is missing or invalid.
     */
    private int parseTaskIndex(String command, CommandType commandType) {
        String keyword = commandType.name().toLowerCase();
        String taskNumber = getArguments(command, keyword);
        if (!taskNumber.matches("[1-9]\\d*")) {
            throw new IllegalArgumentException("Please specify a valid task number after "
                    + keyword + ".");
        }
        try {
            return Integer.parseInt(taskNumber) - 1;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Please specify a valid task number after "
                    + keyword + ".", exception);
        }
    }

    /**
     * Parses a task-creation command into the corresponding task object.
     *
     * @param command Full task-creation command.
     * @param commandType Type of task to create.
     * @return Parsed task.
     * @throws IllegalArgumentException If required task details are missing or invalid.
     */
    private Task parseTask(String command, CommandType commandType) {
        return switch (commandType) {
            case TODO -> parseTodo(command);
            case DEADLINE -> parseDeadline(command);
            case EVENT -> parseEvent(command);
            default -> throw new IllegalArgumentException(
                    "The command does not create a task: " + commandType);
        };
    }

    private UpdateCommand parseUpdate(String command) {
        String arguments = getArguments(command, "update");
        String[] indexAndDetails = arguments.split("\\s+", 2);
        if (indexAndDetails.length < 2) {
            throw new IllegalArgumentException(
                    "Please specify a task number and detail to update.");
        }

        if (!indexAndDetails[0].matches("[1-9]\\d*")) {
            throw new IllegalArgumentException(
                    "Please specify a valid task number after update.");
        }

        int index;
        try {
            index = Integer.parseInt(indexAndDetails[0]) - 1;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Please specify a valid task number after update.", exception);
        }

        String[] fieldAndValue = indexAndDetails[1].split("\\s+", 2);
        if (fieldAndValue.length < 2 || !fieldAndValue[0].startsWith("/")) {
            throw new IllegalArgumentException(
                    "Use /description, /by, /from, or /to followed by a new value.");
        }

        UpdateField field;
        try {
            field = UpdateField.valueOf(fieldAndValue[0].substring(1).toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Use /description, /by, /from, or /to followed by a new value.", exception);
        }
        if (fieldAndValue[1].isBlank()) {
            throw new IllegalArgumentException("The updated value cannot be empty.");
        }
        if (countUpdateParameters(indexAndDetails[1]) != 1) {
            throw new IllegalArgumentException(
                    "Please update exactly one detail at a time.");
        }
        return new UpdateCommand(index, field, fieldAndValue[1].trim());
    }

    private Todo parseTodo(String command) {
        String description = getArguments(command, "todo");
        if (description.isEmpty()) {
            throw new IllegalArgumentException(
                    "Oops! Please give me a description for the todo.");
        }
        return new Todo(description);
    }

    private Deadline parseDeadline(String command) {
        String details = getArguments(command, "deadline");
        if (details.isEmpty()) {
            throw new IllegalArgumentException(
                    "Oops! Please give me a description and deadline.");
        } else if (!details.matches("(?s).*(?:^|\\s)/by(?:\\s+.*|$)")) {
            throw new IllegalArgumentException(
                    "Oops! Please specify the deadline using /by.");
        } else if (countParameter(details, "/by") != 1) {
            throw new IllegalArgumentException(
                    "Oops! Please specify /by exactly once.");
        } else if (containsAnyParameter(details, "/from", "/to", "/description")) {
            throw new IllegalArgumentException(
                    "Oops! A deadline accepts only the /by parameter.");
        }

        String[] parts = details.split("(?:^|\\s+)/by(?=\\s|$)", 2);
        String description = parts[0].trim();
        String byString = parts[1].trim();
        if (description.isEmpty() || byString.isEmpty()) {
            throw new IllegalArgumentException(
                    "Oops! The deadline description and date cannot be empty.");
        }

        try {
            return new Deadline(description, LocalDate.parse(byString));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("Oops! Please enter the deadline date "
                    + "in YYYY-MM-DD format.", exception);
        }
    }

    private Event parseEvent(String command) {
        String details = getArguments(command, "event");
        if (details.isEmpty()) {
            throw new IllegalArgumentException(
                    "Oops! Please give me an event description and time.");
        } else if (countParameter(details, "/from") == 0
                || countParameter(details, "/to") == 0) {
            throw new IllegalArgumentException(
                    "Oops! Please specify the event using /from and /to.");
        } else if (countParameter(details, "/from") != 1
                || countParameter(details, "/to") != 1) {
            throw new IllegalArgumentException(
                    "Oops! Please specify /from and /to exactly once.");
        } else if (containsAnyParameter(details, "/by", "/description")) {
            throw new IllegalArgumentException(
                    "Oops! An event accepts only the /from and /to parameters.");
        }

        String[] fromParts = details.split("(?:^|\\s+)/from(?=\\s|$)", 2);
        if (countParameter(fromParts[0], "/to") > 0) {
            throw new IllegalArgumentException(
                    "Oops! Please place /from before /to.");
        }
        String[] toParts = fromParts[1].split("(?:^|\\s+)/to(?=\\s|$)", 2);
        if (toParts.length < 2) {
            throw new IllegalArgumentException(
                    "Oops! Please specify the event using /from and /to.");
        } else if (fromParts[0].isBlank() || toParts[0].isBlank()
                || toParts[1].isBlank()) {
            throw new IllegalArgumentException(
                    "Oops! The event description and times cannot be empty.");
        }

        return new Event(fromParts[0].trim(), toParts[0].trim(), toParts[1].trim());
    }

    private String getArguments(String command, String keyword) {
        if (command.length() <= keyword.length()) {
            return "";
        }
        return command.substring(keyword.length()).trim();
    }

    private boolean hasKeyword(String command, String keyword) {
        return command.equals(keyword)
                || command.length() > keyword.length()
                && command.startsWith(keyword)
                && Character.isWhitespace(command.charAt(keyword.length()));
    }

    private int countUpdateParameters(String details) {
        return countParameter(details, "/description")
                + countParameter(details, "/by")
                + countParameter(details, "/from")
                + countParameter(details, "/to");
    }

    private boolean containsAnyParameter(String details, String... parameters) {
        for (String parameter : parameters) {
            if (countParameter(details, parameter) > 0) {
                return true;
            }
        }
        return false;
    }

    private int countParameter(String details, String parameter) {
        Pattern parameterPattern = Pattern.compile(
                "(?:^|\\s)" + Pattern.quote(parameter) + "(?=\\s|$)");
        Matcher matcher = parameterPattern.matcher(details);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
