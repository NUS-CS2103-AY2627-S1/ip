package luna.command;

import luna.exception.LunaException;
import luna.storage.Storage;
import luna.task.TaskList;

/**
 * Creates commands from parsed command words and arguments.
 */
public final class CommandFactory {

    private CommandFactory() {
    }

    /**
     * Creates the command represented by the given input.
     *
     * @param commandWord Command word from the user input.
     * @param commandArgs User input after the command word.
     * @param tasks Task list used by the command.
     * @param storage Storage used by commands that modify tasks.
     * @return Command to execute.
     * @throws LunaException If the command word is unknown.
     */
    public static Command create(String commandWord, String commandArgs,
            TaskList tasks, Storage storage) throws LunaException {
        switch (commandWord) {
            case "list":
                return new ListCommand(tasks);
            case "mark":
                return new MarkCommand(commandArgs, tasks, storage);
            case "unmark":
                return new UnmarkCommand(commandArgs, tasks, storage);
            case "delete":
                return new DeleteCommand(commandArgs, tasks, storage);
            case "find":
                return new FindCommand(commandArgs, tasks);
            case "todo":
                return new AddTodoCommand(commandArgs, tasks, storage);
            case "deadline":
                return new AddDeadlineCommand(commandArgs, tasks, storage);
            case "event":
                return new AddEventCommand(commandArgs, tasks, storage);
            case "bye":
                return new ExitCommand();
            default:
                throw new LunaException("I'm sorry, I don't know what that means.");
        }
    }
}
