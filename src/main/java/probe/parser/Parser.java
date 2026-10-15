package probe.parser;

import java.time.LocalDateTime;

import probe.ProbeException;
import probe.task.Deadline;
import probe.task.Event;
import probe.task.Task;
import probe.task.Todo;

/**
 * Converts user commands into tasks and task numbers.
 */
public class Parser {
    /**
     * Returns a task parsed from a todo, deadline, or event command.
     *
     * @param command User command to parse.
     * @return Parsed task.
     * @throws ProbeException If the command is invalid.
     */
    public Task parseTask(String command) throws ProbeException {
        if (command == null || command.isBlank()) {
            throw new ProbeException("Please enter a command.");
        }
        String normalizedCommand = command.trim().replaceAll("\\s+", " ");
        if (normalizedCommand.equals("todo") || normalizedCommand.startsWith("todo ")) {
            String description = normalizedCommand.substring(4).trim();
            if (description.isBlank()) {
                throw new ProbeException("A todo description cannot be empty.");
            }
            return new Todo(description);
        }
        if (normalizedCommand.equals("deadline") || normalizedCommand.startsWith("deadline ")) {
            String[] parts = normalizedCommand.substring(8).trim().split(" /by ", 2);
            if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
                throw new ProbeException("A deadline must include a description and /by a date or time.");
            }
            try {
                return new Deadline(parts[0], DateTimeParser.parse(parts[1]));
            } catch (IllegalArgumentException e) {
                throw new ProbeException(e.getMessage());
            }
        }
        if (!normalizedCommand.equals("event") && !normalizedCommand.startsWith("event ")) {
            throw new ProbeException("Please use todo, deadline, or event to add a task.");
        }
        String[] parts = normalizedCommand.substring(5).trim().split(" /from ", 2);
        if (parts.length != 2 || parts[0].isBlank()) {
            throw new ProbeException("An event must include a description and /from a starting date or time.");
        }
        String[] times = parts[1].split(" /to ", 2);
        if (times.length != 2 || times[0].isBlank() || times[1].isBlank()) {
            throw new ProbeException("An event must include /to and an ending date or time.");
        }
        try {
            LocalDateTime from = DateTimeParser.parse(times[0]);
            LocalDateTime to = DateTimeParser.parse(times[1]);
            if (!to.isAfter(from)) {
                throw new ProbeException("An event must end after it starts.");
            }
            assert to.isAfter(from) : "An event must end after its start";
            return new Event(parts[0], from, to);
        } catch (IllegalArgumentException e) {
            throw new ProbeException(e.getMessage());
        }
    }
    /**
     * Returns the integer task number extracted from a command.
     *
     * @param command Command containing the task number.
     * @param error Error message for an incorrectly formatted command.
     * @return Parsed task number.
     * @throws ProbeException If the task number is invalid.
     */
    public int parseNumber(String command, String error) throws ProbeException {
        if (command == null) {
            throw new ProbeException(error);
        }
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new ProbeException(error);
        }
        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ProbeException("The task number must be a number.");
        }
    }
}
