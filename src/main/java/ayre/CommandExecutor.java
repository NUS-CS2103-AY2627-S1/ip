package ayre;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ayre.commands.ByeCommand;
import ayre.commands.Command;
import ayre.commands.DeadlineCommand;
import ayre.commands.DeleteCommand;
import ayre.commands.EventCommand;
import ayre.commands.FindCommand;
import ayre.commands.ListCommand;
import ayre.commands.MarkCommand;
import ayre.commands.TodoCommand;
import ayre.commands.UnmarkCommand;
import ayre.enums.CommandType;
import ayre.exceptions.InvalidCommandException;

/**
 * Handles the execution of Commands with an EnumMap mapping each Command to its manner of execution.
 * Constructed with some modifications from Claude Sonnet 5 medium.
 */
public class CommandExecutor {
    private final Map<CommandType, Command> handler = new EnumMap<>(CommandType.class);

    /**
     * Class constructor.
     * Initializes the EnumMap by adding all mappings between CommandTypes and their Command class.
     *
     * @param tasks The LiveTaskList used by the main process.
     * @throws IllegalStateException If programmer failed to include a handler for a CommandType.
     */
    public CommandExecutor(LiveTaskList tasks) throws IllegalStateException {
        handler.put(CommandType.BYE, new ByeCommand(tasks));
        handler.put(CommandType.LIST, new ListCommand(tasks));
        handler.put(CommandType.FIND, new FindCommand(tasks));
        handler.put(CommandType.MARK, new MarkCommand(tasks));
        handler.put(CommandType.UNMARK, new UnmarkCommand(tasks));
        handler.put(CommandType.DELETE, new DeleteCommand(tasks));
        handler.put(CommandType.TODO, new TodoCommand(tasks));
        handler.put(CommandType.DEADLINE, new DeadlineCommand(tasks));
        handler.put(CommandType.EVENT, new EventCommand(tasks));

        for (CommandType c : CommandType.values()) {
            if (!handler.containsKey(c)) {
                throw new AssertionError("No executor registered for command: " + c);
            }
        }
    }

    /**
     * Runs the handler of the given command.
     *
     * @param cmd Command to be executed.
     * @param args Arguments required for Command execution.
     * @return A Record of the message on completion and the process status.
     * @throws InvalidCommandException If arguments will cause a RunTimeException.
     */
    public CommandResult execute(CommandType cmd, List<String> args) throws InvalidCommandException {
        return handler.get(cmd).doCommand(args);
    }
}
