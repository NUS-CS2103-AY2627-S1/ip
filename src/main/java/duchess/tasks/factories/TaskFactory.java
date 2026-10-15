package duchess.tasks.factories;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import duchess.tasks.DateFormat;
import duchess.tasks.Task;
import duchess.tasks.exceptions.TaskCreationException;
import duchess.tasks.exceptions.TaskException;
import duchess.tasks.exceptions.UnrecognizedCommandException;

/**
 * Handles the creation of tasks.
 */
public class TaskFactory {
    private static final char[] INVALID_CHARACTERS = new char[]{'|'};

    static LocalDate parseDate(String dateStr) throws TaskCreationException {
        try {
            return LocalDate.parse(dateStr, DateFormat.PARSE_FORMAT);
        } catch (DateTimeParseException e) {
            throw TaskCreationException.declareInvalidDateFormat(dateStr);
        }
    }

    static int findMarker(String command, String marker) {
        return command.indexOf(marker);
    }

    static String extractCommandSection(String command, int start, int end) {
        return command.substring(start, end).trim();
    }

    /**
     * Checks if a command contains invalid characters.
     */
    private static boolean containsInvalidCharacters(String command) {
        for (char c : TaskFactory.INVALID_CHARACTERS) {
            if (command.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a task from the user command.
     *
     * @param commandLower The command to create a task, in lower case.
     * @throws TaskException If the command is unrecognized, or otherwise
     *                          contains invalid characters,
     *                          insufficient information,
     *                          invalid date format,
     *                          or invalid date range.
     */
    public static Task createFromCommand(String commandLower) throws TaskException {
        assert (commandLower != null) : "Command to create Task cannot be null!";
        assert (commandLower.equals(commandLower.toLowerCase()))
                : "commandLower must be in lower case!";

        String commandLowerStripped = commandLower.stripLeading();
        validateCommand(commandLowerStripped);
        return createTask(commandLowerStripped);
    }

    private static void validateCommand(String command) throws TaskCreationException {
        if (TaskFactory.containsInvalidCharacters(command)) {
            throw TaskCreationException.declareInvalidCharacters(
                    command,
                    Arrays.toString(TaskFactory.INVALID_CHARACTERS));
        }
    }

    private static Task createTask(String command) throws TaskException {
        if (command.startsWith("todo")) {
            return TodoFactory.create(command);
        } else if (command.startsWith("deadline")) {
            return DeadlineFactory.create(command);
        } else if (command.startsWith("event")) {
            return EventFactory.create(command);
        } else if (command.startsWith("recurring")) {
            return RecurringTaskFactory.create(command);
        } else {
            // Unrecognised command type
            throw UnrecognizedCommandException.declare(command);
        }
    }
}
