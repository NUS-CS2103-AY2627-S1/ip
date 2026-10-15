package magnus.command;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import magnus.exception.CommandNotFoundException;
import magnus.exception.CommandSyntaxException;
import magnus.exception.MagnusException;
import magnus.parser.CommandParser;
import magnus.task.TaskList;

/**
 * Routes user input to the appropriate command for execution.
 * Recognized command words are matched to their corresponding commands.
 */
public class CommandRouter {
    private static final int COMMAND_PART_INDEX_KEYWORD = 0;
    private static final int COMMAND_PART_INDEX_ARGUMENTS = 1;

    private final Map<CommandType, Command> commands;
    private final CommandParser parser;
    private final UpdateCommand updateCommand;

    /**
     * Creates a command router whose commands operate on the specified task list.
     *
     * @param tasks The task list used by commands that manage tasks.
     */
    public CommandRouter(TaskList tasks) {
        this.commands = new EnumMap<>(CommandType.class);
        this.commands.put(CommandType.BYE, new ExitCommand());
        this.commands.put(CommandType.LIST, new ListCommand(tasks));
        this.commands.put(CommandType.LIST_DEADLINE, new ListDeadlineCommand(tasks));
        this.commands.put(CommandType.LIST_EVENT, new ListEventCommand(tasks));
        this.commands.put(CommandType.FIND, new FindCommand(tasks));
        this.commands.put(CommandType.MARK, new MarkCommand(tasks));
        this.commands.put(CommandType.UNMARK, new UnmarkCommand(tasks));
        this.commands.put(CommandType.TODO, new ToDoCommand(tasks));
        this.commands.put(CommandType.DEADLINE, new DeadlineCommand(tasks));
        this.commands.put(CommandType.EVENT, new EventCommand(tasks));
        this.updateCommand = new UpdateCommand(tasks);
        this.commands.put(CommandType.UPDATE, this.updateCommand);
        this.commands.put(CommandType.DELETE, new DeleteCommand(tasks));
        assert this.commands.size() == CommandType.values().length
                : "Every command type must have a registered command";

        this.parser = new CommandParser();
    }

    /**
     * Parses the user input and executes the command identified by its first token.
     *
     * @param userInput The raw user input to route.
     * @return The type and user-facing message produced by the executed command.
     * @throws CommandNotFoundException If the first token is not a recognized command word.
     * @throws MagnusException If the matching command cannot be executed.
     */
    public CommandResult route(String userInput) throws MagnusException {
        if (this.updateCommand.isAwaitingUpdatedTask()) {
            String resultMessage = this.updateCommand.completeUpdate(userInput);
            return new CommandResult(CommandType.UPDATE, resultMessage, true);
        }
        if (userInput != null && (userInput.contains("\n") || userInput.contains("\r"))) {
            throw new CommandSyntaxException(
                    "\tThat move spans multiple lines - please enter one command at a time.");
        }

        String[] parsedCommandParts = this.parser.parse(userInput);
        if (parsedCommandParts.length == 0) {
            throw new CommandSyntaxException("\tYour move - please enter a command.");
        }
        String commandKeyword = parsedCommandParts[COMMAND_PART_INDEX_KEYWORD];
        String[] commandArguments = Arrays.copyOfRange(
                parsedCommandParts, COMMAND_PART_INDEX_ARGUMENTS, parsedCommandParts.length);

        CommandType commandType;
        try {
            commandType = CommandType.parseKeyword(commandKeyword);
        } catch (IllegalArgumentException exception) {
            throw new CommandNotFoundException(
                    "\tThat move is not in my playbook - I don't recognize the command '"
                            + commandKeyword + "'.");
        }

        Command command = this.commands.get(commandType);
        assert command != null : "The parsed command type must have a registered command";
        String resultMessage = command.execute(commandArguments);
        assert resultMessage != null : "A successful command must return a response message";
        return new CommandResult(commandType, resultMessage, commandType.canChangeTaskList());
    }
}
