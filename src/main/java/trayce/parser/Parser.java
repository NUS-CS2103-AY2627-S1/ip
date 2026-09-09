package trayce.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import trayce.task.Deadline;
import trayce.task.Event;
import trayce.task.Task;

/** Converts supported task-creation commands into task objects. */
public class Parser {
    /**
     * Creates a new Parser instance.
     */
    public Parser() {
    }

    /**
     * Parses a todo, deadline, or event command.
     *
     * @param command the complete user command
     * @return a new task, or {@code null} if the command is invalid
     */
    public Task parseTask(String command) {
        String lowerCaseCommand = command.toLowerCase();
        if (lowerCaseCommand.startsWith("todo ")) {
            String description = command.substring(5).trim();
            return description.isEmpty() ? null : new Task(description);
        }
        if (lowerCaseCommand.startsWith("deadline ")) {
            return parseDeadline(command.substring(9).trim());
        }
        if (lowerCaseCommand.startsWith("event ")) {
            return parseEvent(command.substring(6).trim());
        }
        return null;
    }

    /**
     * Parses the description and date in a deadline command.
     *
     * @param taskDetails the details string containing the description and deadline date
     * @return the parsed deadline task, or {@code null} if parsing failed or input format was invalid
     */
    private Task parseDeadline(String taskDetails) {
        int byIndex = taskDetails.toLowerCase().indexOf(" /by ");
        if (byIndex <= 0 || taskDetails.substring(byIndex + 5).trim().isEmpty()) {
            return null;
        }
        try {
            return new Deadline(taskDetails.substring(0, byIndex).trim(),
                    LocalDate.parse(taskDetails.substring(byIndex + 5).trim()));
        } catch (DateTimeParseException exception) {
            return null;
        }
    }

    /**
     * Parses the description and dates in an event command.
     *
     * @param taskDetails the details string containing the description and event start/end dates
     * @return the parsed event task, or {@code null} if parsing failed or input format was invalid
     */
    private Task parseEvent(String taskDetails) {
        int fromIndex = taskDetails.toLowerCase().indexOf(" /from ");
        int toIndex = taskDetails.toLowerCase().indexOf(" /to ");
        if (fromIndex <= 0 || toIndex <= fromIndex || taskDetails.substring(toIndex + 5).trim().isEmpty()) {
            return null;
        }
        try {
            return new Event(taskDetails.substring(0, fromIndex).trim(),
                    LocalDate.parse(taskDetails.substring(fromIndex + 7, toIndex).trim()),
                    LocalDate.parse(taskDetails.substring(toIndex + 5).trim()));
        } catch (DateTimeParseException exception) {
            return null;
        }
    }
}
