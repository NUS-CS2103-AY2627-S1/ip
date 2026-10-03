package ramly.parser;

import java.util.Locale;

import ramly.command.Command;
import ramly.command.DeadlineCommand;
import ramly.command.DeleteCommand;
import ramly.command.EventCommand;
import ramly.command.ExitCommand;
import ramly.command.FindCommand;
import ramly.command.ListCommand;
import ramly.command.MarkCommand;
import ramly.command.TodoCommand;
import ramly.command.UndoCommand;
import ramly.command.UnknownCommand;
import ramly.command.UnmarkCommand;
import ramly.exception.CommandFormatException;
import ramly.validation.InputValidator;

/** Converts raw user command text into validated executable commands. */
public class Parser {
    /** Returns the command represented by the supplied user input. */
    public Command parse(String input) {
        assert input != null : "Command input must not be null";
        String command = InputValidator.normalizeCommand(input);
        if (command.isEmpty()) {
            throw new CommandFormatException("Please enter a command.");
        }

        switch (getCommandType(command)) {
        case BYE:
            requireExactCommand(command, "bye");
            return new ExitCommand();
        case LIST:
            requireExactCommand(command, "list");
            return new ListCommand();
        case MARK:
            return new MarkCommand(parseTaskIndex(command, "mark"));
        case UNMARK:
            return new UnmarkCommand(parseTaskIndex(command, "unmark"));
        case TODO:
            return new TodoCommand(parseTodo(command));
        case DEADLINE:
            String[] deadline = parseDeadline(command);
            return new DeadlineCommand(deadline[0], deadline[1]);
        case EVENT:
            String[] event = parseEvent(command);
            return new EventCommand(event[0], event[1], event[2]);
        case DELETE:
            return new DeleteCommand(parseTaskIndex(command, "delete"));
        case FIND:
            return new FindCommand(parseFind(command));
        case UNDO:
            requireExactCommand(command, "undo");
            return new UndoCommand();
        default:
            return new UnknownCommand();
        }
    }

    /** Classifies normalized input by its first command word. */
    public CommandType getCommandType(String input) {
        assert input != null : "Command input must not be null";
        String command = InputValidator.normalizeCommand(input);
        String commandWord = command.contains(" ") ? command.substring(0, command.indexOf(' ')) : command;
        for (CommandType type : CommandType.values()) {
            if (type != CommandType.UNKNOWN && type.name().toLowerCase(Locale.ROOT).equals(commandWord)) {
                return type;
            }
        }
        return CommandType.UNKNOWN;
    }

    /** Splits a deadline command into its description and deadline value. */
    public String[] parseDeadline(String command) {
        String body = bodyAfter(command, "deadline");
        String[] fields = body.split(" /by ", -1);
        if (fields.length != 2 || fields[0].isBlank() || fields[1].isBlank()) {
            throw usageError("deadline <description> /by <date or date-time>");
        }
        return fields;
    }

    /** Returns the description in a todo command. */
    public String parseTodo(String command) {
        String description = bodyAfter(command, "todo");
        if (description.isBlank()) {
            throw usageError("todo <description>");
        }
        return description;
    }

    /** Returns the keyword from a find command. */
    public String parseFind(String command) {
        String keyword = bodyAfter(command, "find");
        if (keyword.isBlank()) {
            throw usageError("find <keyword>");
        }
        return keyword;
    }

    /** Splits an event command into its description, start, and end values. */
    public String[] parseEvent(String command) {
        String body = bodyAfter(command, "event");
        String[] fromFields = body.split(" /from ", -1);
        if (fromFields.length != 2) {
            throw usageError("event <description> /from <date-time> /to <date-time>");
        }
        String[] toFields = fromFields[1].split(" /to ", -1);
        if (toFields.length != 2 || fromFields[0].isBlank()
                || toFields[0].isBlank() || toFields[1].isBlank()) {
            throw usageError("event <description> /from <date-time> /to <date-time>");
        }
        return new String[] {fromFields[0], toFields[0], toFields[1]};
    }

    /** Converts a positive one-based task number into a zero-based index. */
    public int parseTaskIndex(String command, String commandWord) {
        String value = bodyAfter(command, commandWord);
        if (!value.matches("[1-9]\\d*")) {
            throw new CommandFormatException(
                    "Use a positive whole-number trail marker, for example: " + commandWord + " 1");
        }
        try {
            return Integer.parseInt(value) - 1;
        } catch (NumberFormatException e) {
            throw new CommandFormatException("That trail-marker number is too large.");
        }
    }

    /** Checks whether raw input begins with the specified command word. */
    public boolean isCommand(String input, String command) {
        String normalized = InputValidator.normalizeCommand(input);
        return normalized.equals(command) || normalized.startsWith(command + " ");
    }

    /** Returns the normalized text following a command word. */
    private String bodyAfter(String command, String commandWord) {
        String normalized = InputValidator.normalizeCommand(command);
        if (normalized.equals(commandWord)) {
            return "";
        }
        if (!normalized.startsWith(commandWord + " ")) {
            throw usageError(commandWord);
        }
        return normalized.substring(commandWord.length() + 1);
    }

    /** Rejects arguments supplied to a command that accepts none. */
    private void requireExactCommand(String command, String commandWord) {
        if (!command.equals(commandWord)) {
            throw usageError(commandWord);
        }
    }

    /** Creates a consistent command-specific usage error. */
    private CommandFormatException usageError(String usage) {
        return new CommandFormatException("Use this format: " + usage);
    }
}
