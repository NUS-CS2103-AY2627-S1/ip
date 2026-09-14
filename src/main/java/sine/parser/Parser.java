package sine.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sine.command.AddCommand;
import sine.command.Command;
import sine.command.DeleteCommand;
import sine.command.ExitCommand;
import sine.command.FindCommand;
import sine.command.ListCommand;
import sine.command.MarkCommand;
import sine.command.UndoCommand;
import sine.command.UnknownCommand;
import sine.command.UnmarkCommand;
import sine.exception.SineException;
import sine.task.Deadline;
import sine.task.Event;
import sine.task.Task;
import sine.task.Todo;

/**
 * Interprets user input and validates command arguments.
 */
public class Parser {
    /**
     * Command categories understood by Sine.
     */
    private enum CommandType {
        UNDO, BYE, LIST, FIND, DELETE, UNMARK, MARK, ADD_TASK, UNKNOWN
    }

    /**
     * Identifies the operation requested by a command.
     *
     * @param command Raw user command.
     * @return Matching command category.
     */
    private CommandType getCommandType(String command) throws SineException {
        if (command.equals("undo") || command.equals("UNDO")) {
            return CommandType.UNDO;
        }
        String firstWord = command.strip().split("\\s+", 2)[0];
        if (firstWord.equalsIgnoreCase("undo")) {
            throw new SineException("Usage: undo or UNDO");
        }
        if (matches(command, "bye") && !command.equals("bye")) {
            throw new SineException("Usage: bye");
        }
        if (matches(command, "list") && !command.equals("list")) {
            throw new SineException("Usage: list");
        }
        if (command.equals("bye")) {
            return CommandType.BYE;
        }
        if (command.equals("list")) {
            return CommandType.LIST;
        }
        if (matches(command, "find")) {
            return CommandType.FIND;
        }
        if (matches(command, "delete")) {
            return CommandType.DELETE;
        }
        if (matches(command, "unmark")) {
            return CommandType.UNMARK;
        }
        if (matches(command, "mark")) {
            return CommandType.MARK;
        }
        if (matches(command, "todo") || matches(command, "deadline")
                || matches(command, "event")) {
            return CommandType.ADD_TASK;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Parses user input into a command that is ready to execute.
     *
     * @param command Raw user command.
     * @param taskCount Number of tasks currently stored.
     * @return Parsed command object.
     * @throws SineException If a command argument is missing or invalid.
     */
    public Command parse(String command, int taskCount) throws SineException {
        if (command == null || command.isBlank()) {
            throw new SineException("Please enter a command.");
        }
        // Keep undo's exact syntax while accepting surrounding whitespace elsewhere.
        if (!command.strip().split("\\s+", 2)[0].equalsIgnoreCase("undo")) {
            command = command.strip();
        }
        CommandType commandType = getCommandType(command);
        switch (commandType) {
            case UNDO:
                return new UndoCommand();
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case FIND:
                return new FindCommand(parseFindKeyword(command));
            case DELETE:
                return new DeleteCommand(parseTaskIndex(command, taskCount));
            case UNMARK:
                return new UnmarkCommand(parseTaskIndex(command, taskCount));
            case MARK:
                return new MarkCommand(parseTaskIndex(command, taskCount));
            case ADD_TASK:
                return new AddCommand(parseTask(command));
            case UNKNOWN:
                return new UnknownCommand();
            default:
                throw new AssertionError("Every command type is handled above");
        }
    }

    /**
     * Extracts and validates the keyword of a find command.
     *
     * @param command Complete find command.
     * @return Non-empty keyword to search for.
     * @throws SineException If the keyword is missing.
     */
    private String parseFindKeyword(String command) throws SineException {
        String keyword = command.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new SineException("The search keyword cannot be empty.");
        }
        return keyword;
    }

    /**
     * Converts the numeric argument of a task command to a zero-based index.
     *
     * @param command Complete delete, mark, or unmark command.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based index of the selected task.
     * @throws SineException If the argument is not a valid stored task number.
     */
    private int parseTaskIndex(String command, int taskCount) throws SineException {
        String[] parts = command.split("\\s+", 2);
        String argument = parts.length < 2 ? "" : parts[1];
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument.trim());
        } catch (NumberFormatException exception) {
            throw new SineException("Please enter a valid task number.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new SineException("That task number does not exist.");
        }
        return taskNumber - 1;
    }

    /**
     * Creates the task described by an add-task command.
     *
     * @param command Todo, deadline, or event command entered by the user.
     * @return Task represented by the command.
     * @throws SineException If a required field is missing or invalid.
     */
    private Task parseTask(String command) throws SineException {
        if (matches(command, "todo")) {
            return parseTodo(command);
        }
        if (matches(command, "deadline")) {
            return parseDeadline(command);
        }
        if (matches(command, "event")) {
            return parseEvent(command);
        }
        throw new AssertionError("Only add-task commands reach task parsing");
    }

    /**
     * Parses a todo command and rejects an empty description.
     */
    private Todo parseTodo(String command) throws SineException {
        String description = command.substring(4).trim();
        if (description.isEmpty()) {
            throw new SineException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a deadline command and validates its description and date.
     */
    private Deadline parseDeadline(String command) throws SineException {
        String details = command.substring(8).trim();
        int byIndex = findParameter(details, "/by");
        String description = byIndex < 0 ? details : details.substring(0, byIndex).trim();
        if (description.isEmpty()) {
            throw new SineException("The description of a deadline cannot be empty.");
        }
        if (byIndex < 0 || details.substring(byIndex + 3).trim().isEmpty()) {
            throw new SineException("The deadline of a deadline cannot be empty.");
        }
        String dateText = details.substring(byIndex + 3).trim();
        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException exception) {
            throw new SineException("Please enter the deadline as yyyy-MM-dd.");
        }
    }

    /**
     * Parses an event command and validates its description and time fields.
     */
    private Event parseEvent(String command) throws SineException {
        String details = command.substring(5).trim();
        int fromIndex = findParameter(details, "/from");
        int toIndex = findParameter(details, "/to");
        if (fromIndex >= 0 && toIndex >= 0 && toIndex < fromIndex) {
            throw new SineException("Place /from before /to.");
        }
        String description = fromIndex < 0 ? details : details.substring(0, fromIndex).trim();
        if (description.isEmpty()) {
            throw new SineException("The description of an event cannot be empty.");
        }
        if (fromIndex < 0 || toIndex < 0
                || details.substring(fromIndex + 5, toIndex).trim().isEmpty()) {
            throw new SineException("The start time of an event cannot be empty.");
        }
        if (details.substring(toIndex + 3).trim().isEmpty()) {
            throw new SineException("The end time of an event cannot be empty.");
        }
        String from = details.substring(fromIndex + 5, toIndex).trim();
        String to = details.substring(toIndex + 3).trim();
        return new Event(description, from, to);
    }

    /**
     * Finds a standalone parameter and rejects repeated occurrences.
     *
     * @param details Task description and parameter values.
     * @param parameter Parameter token to locate.
     * @return Token position, or -1 if it is missing.
     * @throws SineException If the parameter appears more than once.
     */
    private int findParameter(String details, String parameter) throws SineException {
        Pattern pattern = Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
        Matcher matcher = pattern.matcher(details);
        if (!matcher.find()) {
            return -1;
        }
        int index = matcher.start();
        if (matcher.find()) {
            throw new SineException("Specify " + parameter + " only once.");
        }
        return index;
    }

    /**
     * Checks whether input is a command word, optionally followed by arguments.
     *
     * @param input Raw user input.
     * @param commandWord Command word to match.
     * @return True if the input starts with the complete command word.
     */
    private boolean matches(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ")
                || input.startsWith(commandWord + "\t");
    }
}
