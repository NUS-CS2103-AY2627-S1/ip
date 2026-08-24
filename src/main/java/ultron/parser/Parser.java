package ultron.parser;

import ultron.command.Command;
import ultron.command.DeleteCommand;
import ultron.command.ExitCommand;
import ultron.command.FindCommand;
import ultron.command.ListCommand;
import ultron.command.MarkCommand;
import ultron.command.UnmarkCommand;

/**
 * Interprets raw user input as a chatbot command and its arguments.
 */
public class Parser {

    /** Supported commands that can be entered by the user. */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, FIND, TODO, DEADLINE, EVENT, UNKNOWN
    }

    /**
     * Identifies the command represented by the input.
     *
     * @param input the raw user input
     * @return the matching command, or {@code UNKNOWN} when no command matches
     */
    public CommandType parseCommand(String input) {
        if (input.equals("bye")) {
            return CommandType.BYE;
        } else if (input.equals("list")) {
            return CommandType.LIST;
        } else if (input.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (input.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (input.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (input.startsWith("find ")) {
            return CommandType.FIND;
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (input.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (input.startsWith("event ")) {
            return CommandType.EVENT;
        }
        return CommandType.UNKNOWN;
    }

    /**
     * Creates a command object for each command migrated to the command pattern.
     *
     * @param input the raw user input
     * @return an executable command, or {@code null} when the command is not migrated yet
     */
    public Command parseCommandObject(String input) {
        CommandType commandType = parseCommand(input);
        return switch (commandType) {
        case BYE -> new ExitCommand();
        case LIST -> new ListCommand();
        case MARK -> new MarkCommand(getArgument(input, commandType));
        case UNMARK -> new UnmarkCommand(getArgument(input, commandType));
        case DELETE -> new DeleteCommand(getArgument(input, commandType));
        case FIND -> new FindCommand(getArgument(input, commandType));
        default -> null;
        };
    }

    /**
     * Extracts the text following a command word.
     *
     * @param input the raw user input
     * @param command the command already identified from the input
     * @return the trimmed argument text
     */
    public String getArgument(String input, CommandType command) {
        return input.substring(getCommandWord(command).length()).trim();
    }

    private String getCommandWord(CommandType command) {
        return switch (command) {
        case BYE -> "bye";
        case LIST -> "list";
        case MARK -> "mark";
        case UNMARK -> "unmark";
        case DELETE -> "delete";
        case FIND -> "find";
        case TODO -> "todo";
        case DEADLINE -> "deadline";
        case EVENT -> "event";
        case UNKNOWN -> "";
        };
    }
}
