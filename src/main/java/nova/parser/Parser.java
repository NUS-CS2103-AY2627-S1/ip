package nova.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nova.command.Command;
import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Parses user input into commands and task data.
 */
public class Parser {
    private static final String DEADLINE_DELIMITER = "\\s+/by\\s+";
    private static final String EVENT_FROM_DELIMITER = "\\s+/from\\s+";
    private static final String EVENT_TO_DELIMITER = "\\s+/to\\s+";
    private static final String DATE_FORMAT_MESSAGE = "Please use the date format yyyy-MM-dd.";

    /**
     * Returns the command type represented by the input.
     *
     * @param input Full user input.
     * @return Parsed command type.
     */
    public Command parseCommand(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        return switch (parts[0]) {
            case "bye" -> Command.BYE;
            case "list" -> Command.LIST;
            case "mark" -> Command.MARK;
            case "unmark" -> Command.UNMARK;
            case "delete" -> Command.DELETE;
            case "todo" -> Command.TODO;
            case "deadline" -> Command.DEADLINE;
            case "event" -> Command.EVENT;
            case "find" -> Command.FIND;
            case "sort" -> Command.SORT;
            default -> Command.UNKNOWN;
        };
    }

    /**
     * Returns the keyword supplied to a find command.
     *
     * @param input Full user input.
     * @return Search keyword.
     * @throws NovaException If the keyword is missing.
     */
    public String parseFindKeyword(String input) throws NovaException {
        return requireArgument(input, "find", "A find command needs a keyword.");
    }

    /**
     * Returns a validated one-based task number.
     *
     * @param input Full user input.
     * @param commandWord Command word preceding the number.
     * @param taskCount Number of available tasks.
     * @return Validated task number.
     * @throws NovaException If the number is missing, invalid, or out of range.
     */
    public int parseTaskNumber(String input, String commandWord, int taskCount) throws NovaException {
        String numberText = requireArgument(input, commandWord, "Please provide a valid task number.");
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please provide a valid task number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new NovaException("That task number does not exist.");
        }
        return taskNumber;
    }

    /**
     * Returns a task parsed from an add command.
     *
     * @param input Full user input.
     * @param command Type of task to create.
     * @return Parsed task.
     * @throws NovaException If required task details are missing or invalid.
     */
    public Task parseTask(String input, Command command) throws NovaException {
        return switch (command) {
            case TODO -> new Todo(requireArgument(input, "todo", "A todo needs a description."));
            case DEADLINE -> parseDeadline(requireArgument(
                    input, "deadline", "A deadline needs a description."));
            case EVENT -> parseEvent(requireArgument(
                    input, "event", "An event needs a description."));
            default -> throw new NovaException("I'm sorry, but I don't know what that means.");
        };
    }

    private Task parseDeadline(String arguments) throws NovaException {
        String[] parts = arguments.split(DEADLINE_DELIMITER, 2);
        if (parts.length < 2) {
            throw new NovaException("A deadline needs a /by date or time.");
        }
        String description = requireNonBlank(parts[0], "A deadline needs a description.");
        String dueDate = requireNonBlank(parts[1], "A deadline needs a /by date or time.");
        return new Deadline(description, parseDate(dueDate));
    }

    private Task parseEvent(String arguments) throws NovaException {
        String[] fromParts = arguments.split(EVENT_FROM_DELIMITER, 2);
        if (fromParts.length < 2) {
            throw new NovaException("An event needs /from and /to date or time values.");
        }
        String[] toParts = fromParts[1].split(EVENT_TO_DELIMITER, 2);
        if (toParts.length < 2) {
            throw new NovaException("An event needs /from and /to date or time values.");
        }
        String description = requireNonBlank(fromParts[0], "An event needs a description.");
        String from = requireNonBlank(toParts[0], "An event needs a /from date or time.");
        String to = requireNonBlank(toParts[1], "An event needs a /to date or time.");

        LocalDate startDate = parseDate(from);
        LocalDate endDate = parseDate(to);
        if (!endDate.isAfter(startDate)) {
            throw new NovaException("An event's /to date must be after its /from date.");
        }
        return new Event(description, startDate, endDate);
    }

    private LocalDate parseDate(String value) throws NovaException {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new NovaException(DATE_FORMAT_MESSAGE);
        }
    }

    private String requireArgument(String input, String commandWord, String errorMessage)
            throws NovaException {
        return requireNonBlank(input.substring(commandWord.length()), errorMessage);
    }

    private String requireNonBlank(String value, String errorMessage) throws NovaException {
        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new NovaException(errorMessage);
        }
        return trimmedValue;
    }
}
