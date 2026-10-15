package lumine.parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import lumine.LumineException;
import lumine.command.AddCommand;
import lumine.command.Command;
import lumine.command.DateCommand;
import lumine.command.DeleteCommand;
import lumine.command.ExitCommand;
import lumine.command.FindCommand;
import lumine.command.ListCommand;
import lumine.command.MarkCommand;
import lumine.command.UndoCommand;
import lumine.command.UnmarkCommand;
import lumine.task.Deadline;
import lumine.task.Event;
import lumine.task.Todo;

/**
 * Converts raw user commands into validated values and task objects.
 */
public class Parser {
    /**
     * Normalizes a command before it is checked or interpreted.
     */
    public String normalize(String command) {
        assert command != null : "Command to normalize must not be null";
        return command.trim();
    }

    /**
     * Parses a raw user command string and returns the corresponding Command object.
     *
     * @param command the raw input from the user.
     * @return a Command ready to be executed.
     * @throws LumineException if the command is not recognized.
     */
    public Command parse(String command) {
        String normalizedCommand = normalize(command);
        if (normalizedCommand.equals("bye")) {
            return new ExitCommand();
        } else if (normalizedCommand.equals("list")) {
            return new ListCommand();
        } else if (normalizedCommand.equals("undo")) {
            return new UndoCommand();
        } else if (isCommand(normalizedCommand, "date")) {
            return new DateCommand(parseDateCommand(normalizedCommand));
        } else if (isCommand(normalizedCommand, "todo")) {
            return new AddCommand(parseTodoCommand(normalizedCommand));
        } else if (isCommand(normalizedCommand, "deadline")) {
            return new AddCommand(parseDeadlineCommand(normalizedCommand));
        } else if (isCommand(normalizedCommand, "event")) {
            return new AddCommand(parseEventCommand(normalizedCommand));
        } else if (isCommand(normalizedCommand, "mark")) {
            return new MarkCommand(parseTaskNumber(normalizedCommand, "mark"));
        } else if (isCommand(normalizedCommand, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(normalizedCommand, "unmark"));
        } else if (isCommand(normalizedCommand, "delete")) {
            return new DeleteCommand(parseTaskNumber(normalizedCommand, "delete"));
        } else if (isCommand(normalizedCommand, "find")) {
            return new FindCommand(parseFindCommand(normalizedCommand));
        } else {
            throw new LumineException("Hmmmm, I can't understand what that means. ;-;\n"
                    + "Try entering a command instead.");
        }
    }

    /**
     * Checks whether a command is either an exact command or starts with its name.
     */
    public boolean isCommand(String command, String commandName) {
        assert command != null : "Command being checked must not be null";
        assert commandName != null && !commandName.isBlank() : "Command name must be non-blank";
        return command.equals(commandName) || command.matches(commandName + "\\s+.*");
    }

    /**
     * Parses a date filter command without accepting invalid calendar dates.
     */
    public LocalDate parseDateCommand(String command) {
        assert isCommand(command, "date") : "Date parser must receive a date command";
        String dateText = command.substring("date".length()).trim().replaceAll("\\s+", " ");
        if (!dateText.matches("\\d{4} \\d{2} \\d{2}")) {
            throw invalidDateCommand();
        }
        try {
            return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("uuuu MM dd")
                    .withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            throw invalidDateCommand();
        }
    }

    /**
     * Parses a find command to extract the search keyword.
     */
    public String parseFindCommand(String command) {
        assert isCommand(command, "find") : "Find parser must receive a find command";
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new LumineException("Sorry, the search keyword cannot be empty. :C");
        }
        return keyword;
    }

    /**
     * Parses a task number used by mark, unmark, and delete commands.
     */
    public int parseTaskNumber(String command, String commandName) {
        assert commandName != null : "Task number parser requires a command name";
        assert commandName.equals("mark") || commandName.equals("unmark")
                || commandName.equals("delete") : "Task number parser received an unsupported command name";
        assert isCommand(command, commandName) : "Task number parser received a mismatched command";
        String taskNumber = command.substring(commandName.length()).trim();
        try {
            return Integer.parseInt(taskNumber);
        } catch (NumberFormatException e) {
            throw new LumineException("Task not found :<.\nPlease enter a valid task number.");
        }
    }

    /**
     * Creates a todo task, rejecting an empty description.
     */
    public Todo parseTodoCommand(String command) {
        assert isCommand(command, "todo") : "Todo parser must receive a todo command";
        String description = command.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new LumineException("Sorry, todo task cannot be empty. :C");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline task after validating its description and /by field.
     */
    public Deadline parseDeadlineCommand(String command) {
        assert isCommand(command, "deadline") : "Deadline parser must receive a deadline command";
        String details = command.substring("deadline".length()).trim();
        String[] parts = details.split("\\s+/by\\s+", 2);
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new LumineException(
                    "Sorry, I can't read your deadline task. :C\n"
                            + "It needs a description and a /by time.\n"
                            + "e.g. deadline test /by Mon 2pm");
        }
        return new Deadline(parts[0].trim(), parts[1].trim());
    }

    /**
     * Creates an event task after validating its description, /from, and /to fields.
     */
    public Event parseEventCommand(String command) {
        assert isCommand(command, "event") : "Event parser must receive an event command";
        String details = command.substring("event".length()).trim();
        String[] parts = details.split("\\s+/from\\s+|\\s+/to\\s+", 3);
        if (parts.length != 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            throw new LumineException(
                    "Sorry, I can't read your event task. :C\n"
                            + "It needs a description, /from time, and /to time.\n"
                            + "e.g. event test /from Mon 2pm /to 4pm");
        }
        return new Event(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    /**
     * Creates a {@link LumineException} describing a bad {@code date} command argument.
     *
     * @return the exception, ready to be thrown.
     */
    private LumineException invalidDateCommand() {
        return new LumineException("Sorry, I can't understand what date is it. :C\n"
                + "It needs a valid date as the format yyyy mm dd");
    }
}
