package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that displays usage instructions.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays usage instructions.
     *
     * @param commandArguments Additional command arguments, which must be blank.
     */
    public HelpCommand(String commandArguments) {
        super(commandArguments);
    }

    /**
     * Displays every command recognized by Yuno.
     *
     * @param taskList Task list that is not modified.
     * @param ui User interface used to display the instructions.
     * @param storage Storage that is not modified.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If additional command data is supplied.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        if (!getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException("It is just 'help'. How did you manage to complicate that?");
        }
        ui.printHelp();
        return CommandResult.CONTINUE;
    }
}
