package yuno.command;

import yuno.exception.InvalidCommandFormatException;
import yuno.storage.Storage;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that displays a farewell and ends the chatbot session.
 */
public class ByeCommand extends Command {
    /**
     * Creates a command that ends the chatbot session.
     *
     * @param commandArguments Additional command arguments, which must be blank.
     */
    public ByeCommand(String commandArguments) {
        super(commandArguments);
    }

    /**
     * Displays a farewell and ends the chatbot session.
     *
     * @param taskList Task list that is not modified.
     * @param ui User interface used to display the farewell.
     * @param storage Storage that is not modified.
     * @return Always {@link CommandResult#EXIT}.
     * @throws InvalidCommandFormatException If additional command data is supplied.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws InvalidCommandFormatException {
        if (!getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException(
                    "If you're leaving, just say 'bye'. I don't need the unnecessary details.");
        }
        ui.printBye();
        return CommandResult.EXIT;
    }
}
