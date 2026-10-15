package vector.parser;

import vector.VectorException;
import vector.command.AddCommand;
import vector.command.Command;
import vector.command.DeleteCommand;
import vector.command.ExitCommand;
import vector.command.FindCommand;
import vector.command.HelpCommand;
import vector.command.ListCommand;
import vector.command.MarkCommand;
import vector.command.ScheduleCommand;
import vector.command.UnmarkCommand;
import vector.task.Deadline;
import vector.task.Event;
import vector.task.Todo;

/**
 * Parses user input into executable commands.
 */
public class Parser {
    /**
     * Parses the full user input command string and returns the corresponding Command object.
     *
     * @param fullCommand The full command string input by the user.
     * @return The parsed Command object to be executed.
     * @throws VectorException If the user input is invalid or improperly formatted.
     */
    public static Command parse(String fullCommand) throws VectorException {
        assert fullCommand != null : "Command string should not be null";
        if (fullCommand.contains("|")) {
            throw new VectorException("The character '|' is reserved and cannot be used in tasks.");
        }
        String[] parts = fullCommand.split(" ", 2);
        String action = parts[0].trim().toLowerCase();

        switch (action) {
            case "bye":
                return prepareBye(parts);
            case "list":
                return prepareList(parts);
            case "delete":
                return prepareDelete(parts);
            case "mark":
                return prepareMark(parts);
            case "unmark":
                return prepareUnmark(parts);
            case "todo":
                return prepareTodo(parts);
            case "deadline":
                return prepareDeadline(parts);
            case "event":
                return prepareEvent(parts);
            case "schedule":
                return prepareSchedule(parts);
            case "find":
                return prepareFind(parts);
            case "help":
                return prepareHelp(parts);
            default:
                throw new VectorException("I don't recognize that command. "
                        + "Valid commands are: todo, deadline, event, list, mark, unmark, "
                        + "delete, schedule, find, help, bye.");
        }
    }

    private static Command prepareBye(String[] parts) throws VectorException {
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            throw new VectorException("The bye command does not take any arguments.");
        }
        return new ExitCommand();
    }

    private static Command prepareList(String[] parts) throws VectorException {
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            throw new VectorException("The list command does not take any arguments.");
        }
        return new ListCommand();
    }

    private static Command prepareDelete(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("Please specify which task you want to delete. For example: delete 1");
        }
        return new DeleteCommand(Integer.parseInt(parts[1].trim()) - 1);
    }

    private static Command prepareMark(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("Please specify which task you want to mark. For example: mark 1");
        }
        return new MarkCommand(Integer.parseInt(parts[1].trim()) - 1);
    }

    private static Command prepareUnmark(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("Please specify which task you want to unmark. For example: unmark 1");
        }
        return new UnmarkCommand(Integer.parseInt(parts[1].trim()) - 1);
    }

    private static Command prepareTodo(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("A todo task must have a description. Please try again.");
        }
        return new AddCommand(new Todo(parts[1].trim()));
    }

    private static Command prepareDeadline(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("A deadline task requires a description and a /by date. "
                    + "Please try again.");
        }
        String[] deadlineParts = parts[1].split("\\s+/by\\s+");
        if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty()
                || deadlineParts[1].trim().isEmpty()) {
            throw new VectorException("The deadline format is incorrect. "
                    + "Use: deadline <task> /by <date/time>");
        }
        if (deadlineParts.length > 2) {
            throw new VectorException("Multiple /by parameters are not allowed.");
        }
        return new AddCommand(new Deadline(deadlineParts[0].trim(), deadlineParts[1].trim()));
    }

    private static Command prepareEvent(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("An event task requires a description, a /from time, and a /to time.");
        }
        String[] eventParts = parts[1].split("\\s+/from\\s+");
        if (eventParts.length < 2) {
            throw new VectorException("The event format is incomplete. "
                    + "Ensure you have a /from time and a /to time.");
        }
        if (eventParts.length > 2) {
            throw new VectorException("Multiple /from parameters are not allowed.");
        }
        String desc = eventParts[0].trim();
        String[] timeParts = eventParts[1].split("\\s+/to\\s+");
        if (timeParts.length < 2 || desc.isEmpty() || timeParts[0].trim().isEmpty()
                || timeParts[1].trim().isEmpty()) {
            throw new VectorException("The event format is incorrect. "
                    + "Use: event <task> /from <start> /to <end>");
        }
        if (timeParts.length > 2) {
            throw new VectorException("Multiple /to parameters are not allowed.");
        }
        return new AddCommand(new Event(desc, timeParts[0].trim(), timeParts[1].trim()));
    }

    private static Command prepareSchedule(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("Please specify a date to check the schedule. "
                    + "For example: schedule 2019-12-02");
        }
        java.time.LocalDate searchDate = DateTimeParser.parseDate(parts[1].trim());
        return new ScheduleCommand(searchDate);
    }

    private static Command prepareFind(String[] parts) throws VectorException {
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new VectorException("Please specify a keyword to find. "
                    + "For example: find book");
        }
        return new FindCommand(parts[1].trim());
    }

    private static Command prepareHelp(String[] parts) throws VectorException {
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            throw new VectorException("The help command does not take any arguments.");
        }
        return new HelpCommand();
    }
}

