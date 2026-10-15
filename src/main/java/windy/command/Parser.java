package windy.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import windy.exception.InvalidInputFormatException;
import windy.task.Deadline;
import windy.task.Event;
import windy.task.Task;
import windy.task.TaskDateParser;
import windy.task.Todo;

/**
 * Parses user input and validates command arguments for the Windy application.
 */
public class Parser {
    private static final String DEADLINE_COMMAND_FORMAT =
            "Invalid command format. Use: deadline DESCRIPTION /by yyyy-M-d";
    private static final String EVENT_COMMAND_FORMAT =
            "Invalid command format. Use: event DESCRIPTION /from yyyy-M-d /to yyyy-M-d";
    private static final String INVALID_DESCRIPTION_MESSAGE = "Task description cannot contain '|'.";

    private Parser() {
    }

    /**
     * Parses a complete input line into an executable command.
     *
     * @param input the complete command entered by the user.
     * @param taskCount the current number of tasks.
     * @return the parsed command.
     * @throws InvalidInputFormatException if the command or its arguments are invalid.
     */
    public static Command parseCommand(String input, int taskCount) throws InvalidInputFormatException {
        String[] commandParts = splitCommand(input);
        CommandType commandType = parseCommandType(commandParts[0]);
        validateCommandArgumentCount(commandType, commandParts.length);

        return createCommand(input, commandParts, commandType, taskCount);
    }

    private static Command createCommand(String input, String[] commandParts,
            CommandType commandType, int taskCount) throws InvalidInputFormatException {
        return switch (commandType) {
            case UNDO -> new UndoCommand();
            case REDO -> new RedoCommand();
            case BYE -> new ByeCommand();
            case LIST -> new ListCommand();
            case MARK -> new MarkCommand(parseTaskNumber(commandParts[1], taskCount));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(commandParts[1], taskCount));
            case DELETE -> new DeleteCommand(parseTaskNumber(commandParts[1], taskCount));
            case TODO, DEADLINE, EVENT -> new AddTaskCommand(parseNewTask(input, commandType));
            case FIND -> new FindCommand(commandParts[1]);
            case DATE -> new DateCommand(parseDate(commandParts[1]));
            case UNKNOWN -> throw new InvalidInputFormatException(
                    "Unknown command '" + commandParts[0] + "'. Available commands: bye, list, mark, "
                            + "unmark, delete, todo, deadline, event, date, find, undo, redo.");
        };
    }

    /**
     * Creates a task from the details supplied with a task-creation command.
     *
     * @param input the complete command entered by the user.
     * @param commandType the type of task to create.
     * @return the task described by the command.
     * @throws InvalidInputFormatException if required task details are missing or malformed.
     */
    public static Task parseNewTask(String input, CommandType commandType) throws InvalidInputFormatException {
        assert commandType == CommandType.TODO
                || commandType == CommandType.DEADLINE
                || commandType == CommandType.EVENT
                : "Command type should represent a task-creation command";

        String taskDetails = extractTaskDetails(input, commandType);
        Task task = switch (commandType) {
            case TODO -> new Todo(taskDetails, false);
            case DEADLINE -> parseDeadline(taskDetails);
            case EVENT -> parseEvent(taskDetails);
            default -> throw new AssertionError("Expected a task-creation command");
        };
        if (task.getDescription().contains("|")) {
            throw new InvalidInputFormatException(INVALID_DESCRIPTION_MESSAGE);
        }
        return task;
    }

    private static String extractTaskDetails(String input, CommandType commandType)
            throws InvalidInputFormatException {
        String[] commandParts = input.split("\\s+", 2);
        if (commandParts.length < 2 || commandParts[1].isBlank()) {
            String usage = switch (commandType) {
                case TODO -> "todo DESCRIPTION";
                case DEADLINE -> "deadline DESCRIPTION /by yyyy-M-d";
                case EVENT -> "event DESCRIPTION /from yyyy-M-d /to yyyy-M-d";
                default -> throw new AssertionError("Expected a task-creation command");
            };
            throw new InvalidInputFormatException("Task description cannot be empty. Use: " + usage);
        }
        return commandParts[1].trim();
    }

    private static Deadline parseDeadline(String taskDetails) throws InvalidInputFormatException {
        String[] deadlineParts = taskDetails.split("(?<!\\S)/by(?!\\S)", -1);
        if (deadlineParts.length != 2 || deadlineParts[1].isBlank()) {
            throw new InvalidInputFormatException(DEADLINE_COMMAND_FORMAT);
        }
        if (deadlineParts[0].isBlank()) {
            throw new InvalidInputFormatException("Task description cannot be empty. Use: "
                    + "deadline DESCRIPTION /by yyyy-M-d");
        }
        return new Deadline(deadlineParts[0].trim(), false, deadlineParts[1].trim());
    }

    private static Event parseEvent(String taskDetails) throws InvalidInputFormatException {
        String[] eventParts = taskDetails.split("(?<!\\S)/from(?!\\S)", -1);
        if (eventParts.length != 2) {
            throw new InvalidInputFormatException(EVENT_COMMAND_FORMAT);
        }
        if (eventParts[0].isBlank()) {
            throw new InvalidInputFormatException("Task description cannot be empty. Use: "
                    + "event DESCRIPTION /from yyyy-M-d /to yyyy-M-d");
        }

        String[] dateParts = eventParts[1].split("(?<!\\S)/to(?!\\S)", -1);
        if (dateParts.length != 2 || dateParts[0].isBlank() || dateParts[1].isBlank()) {
            throw new InvalidInputFormatException(EVENT_COMMAND_FORMAT);
        }

        String taskDescription = eventParts[0].trim();
        String startDate = dateParts[0].trim();
        String endDate = dateParts[1].trim();
        return new Event(taskDescription, false, startDate, endDate);
    }

    /**
     * Converts a one-based task number from user input to a valid list index.
     *
     * @param taskNumber the task number entered by the user.
     * @param taskCount the current number of tasks.
     * @return the corresponding zero-based task index.
     * @throws InvalidInputFormatException if the number is not an integer or is outside the task list.
     */
    public static int parseTaskNumber(String taskNumber, int taskCount) throws InvalidInputFormatException {
        if (taskCount == 0) {
            throw new InvalidInputFormatException("There are no tasks in the list.");
        }
        int taskNumberValue;
        try {
            taskNumberValue = Integer.parseInt(taskNumber);
        } catch (NumberFormatException exception) {
            if (taskNumber.matches("[+-]?\\d+")) {
                throw new InvalidInputFormatException(
                        "Task number must be between 1 and " + taskCount + ".");
            }
            throw new InvalidInputFormatException("Task number must be an integer.");
        }
        if (taskNumberValue < 1 || taskNumberValue > taskCount) {
            throw new InvalidInputFormatException(
                    "Task number must be between 1 and " + taskCount + ".");
        }
        int taskIndex = taskNumberValue - 1;
        assert taskIndex >= 0 && taskIndex < taskCount
                : "Parsed task index should be within the task list";
        return taskIndex;
    }

    /**
     * Parses a date in the {@code yyyy-M-d} format.
     *
     * @param date the date text to parse.
     * @return the parsed date.
     * @throws InvalidInputFormatException if the date is invalid or uses an unsupported format.
     */
    public static LocalDate parseDate(String date) throws InvalidInputFormatException {
        try {
            return TaskDateParser.parse(date);
        } catch (DateTimeParseException exception) {
            throw new InvalidInputFormatException(TaskDateParser.INVALID_DATE_MESSAGE);
        }
    }

    /**
     * Splits a command into whitespace-separated words.
     *
     * @param input the command entered by the user.
     * @return the words in the command.
     */
    public static String[] splitCommand(String input) {
        return input.split("\\s+");
    }

    /**
     * Identifies the command represented by a command word.
     *
     * @param command the first word of the user's input.
     * @return the matching command type, or {@link CommandType#UNKNOWN} if none matches.
     */
    public static CommandType parseCommandType(String command) {
        return CommandType.from(command);
    }

    /**
     * Validates that a command contains the expected number of words.
     *
     * @param commandType the command being validated.
     * @param commandLength the number of words in the command.
     * @throws InvalidInputFormatException if the command has an unsupported type or argument count.
     */
    public static void validateCommandArgumentCount(CommandType commandType, int commandLength)
            throws InvalidInputFormatException {
        switch (commandType) {
            case UNDO -> requireArgumentCount(commandLength, 1, "Invalid format. Please use: undo");
            case REDO -> requireArgumentCount(commandLength, 1, "Invalid format. Please use: redo");
            case BYE -> requireArgumentCount(commandLength, 1,
                    "The bye command does not accept arguments. Use: bye");
            case LIST -> requireArgumentCount(commandLength, 1,
                    "The list command does not accept arguments. Use: list");
            case MARK, UNMARK, DELETE -> requireArgumentCount(commandLength, 2,
                    "Invalid format. Please use: "
                            + commandType.name().toLowerCase() + " TASK_NUMBER");
            case FIND -> requireArgumentCount(commandLength, 2,
                    "Invalid format. Please use: find keyword");
            case DATE -> requireArgumentCount(commandLength, 2,
                    "Invalid format. Please use: date yyyy-M-d");
            case UNKNOWN -> {
            }
            default -> {
            }
        }
    }

    private static void requireArgumentCount(int actualCount, int expectedCount, String errorMessage)
            throws InvalidInputFormatException {
        if (actualCount != expectedCount) {
            throw new InvalidInputFormatException(errorMessage);
        }
    }
}
