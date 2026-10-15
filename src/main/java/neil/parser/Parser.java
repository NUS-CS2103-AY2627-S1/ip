package neil.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

import neil.exception.NeilException;
import neil.task.DeadlineTask;
import neil.task.EventTask;
import neil.task.Task;
import neil.task.ToDoTask;

/**
 * Converts raw user input into values understood by the application.
 */
public final class Parser {
    private static final char STORAGE_DELIMITER = '|';
    private static final Set<String> TASK_COMMANDS =
            Set.of("todo", "deadline", "event");

    private Parser() {
        // Prevent construction of this utility class.
    }

    /**
     * Parses a task-creation command into a task.
     *
     * @param input raw user command.
     * @return task represented by the command.
     * @throws NeilException if the command is invalid.
     */
    public static Task parseTask(String input) throws NeilException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new NeilException("Please provide a command");
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        String command = parts[0];

        if (!TASK_COMMANDS.contains(command)) {
            throw new NeilException("command " + command + " not supported");
        }

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new NeilException("Please provide a task description");
        }

        String arguments = parts[1].trim();
        validateTaskArguments(arguments);

        switch (command) {
            case "todo":
                return new ToDoTask(arguments);
            case "deadline":
                return parseDeadlineTask(arguments);
            case "event":
                return parseEventTask(arguments);
            default:
                assert false : "Unhandled validated task command: " + command;
                throw new NeilException("Unknown Task type");
        }
    }

    private static void validateTaskArguments(String arguments) throws NeilException {
        if (arguments.indexOf(STORAGE_DELIMITER) >= 0) {
            throw new NeilException("Task details cannot contain the | character");
        }
    }

    private static DeadlineTask parseDeadlineTask(String arguments) throws NeilException {
        String[] deadlineParts = arguments.split("\\s+/by\\s+", 2);
        if (deadlineParts.length != 2
                || deadlineParts[0].isBlank()
                || deadlineParts[1].isBlank()) {
            throw new NeilException("Use: deadline DESCRIPTION /by DATE");
        }

        try {
            LocalDate deadline = LocalDate.parse(deadlineParts[1]);
            return new DeadlineTask(deadlineParts[0], deadline);
        } catch (DateTimeParseException e) {
            throw new NeilException("Please provide a valid date in yyyy-MM-dd format");
        }
    }

    private static EventTask parseEventTask(String arguments) throws NeilException {
        String[] fromParts = arguments.split("\\s+/from\\s+", 2);
        if (fromParts.length != 2) {
            throw new NeilException("Use: event DESCRIPTION /from START /to END");
        }

        String[] toParts = fromParts[1].split("\\s+/to\\s+", 2);
        if (toParts.length != 2
                || fromParts[0].isBlank()
                || toParts[0].isBlank()
                || toParts[1].isBlank()) {
            throw new NeilException("Use: event DESCRIPTION /from START /to END");
        }

        try {
            return new EventTask(
                    fromParts[0].trim(),
                    toParts[0].trim(),
                    toParts[1].trim()
            );
        } catch (DateTimeParseException e) {
            throw new NeilException(
                    "Please provide valid event dates and times in yyyy-MM-dd HH:mm format");
        } catch (IllegalArgumentException e) {
            throw new NeilException("The event start must be earlier than the event end");
        }
    }

    /**
     * Parses a positive task number from a command split into words.
     *
     * @param parts command words containing one task number.
     * @return parsed positive task number.
     * @throws NeilException if the command does not contain one positive integer.
     */
    public static int parseTaskNumber(String[] parts) throws NeilException {
        if (parts.length != 2) {
            throw new NeilException("Please specify a task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new NeilException("The task number must be a positive integer");
        }

        if (taskNumber <= 0) {
            throw new NeilException("The task number must be a positive integer");
        }

        return taskNumber;
    }

    /**
     * Parses the keyword from a find command.
     *
     * @param input raw find command.
     * @return keyword to search for.
     * @throws NeilException if the command does not contain a keyword.
     */
    public static String parseFindKeyword(String input) throws NeilException {
        String[] parts = input.trim().split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new NeilException("Please provide a keyword to search for");
        }

        return parts[1].trim();
    }
}
