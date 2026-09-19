package yuno.command;

import java.util.List;

import yuno.exception.InvalidCommandFormatException;
import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that displays tasks containing specified text in their descriptions.
 */
public class FindByNameCommand extends Command {
    /**
     * Creates a command with the text to find in task descriptions.
     *
     * @param searchText Text to find in task descriptions.
     */
    public FindByNameCommand(String searchText) {
        super(searchText);
    }

    /**
     * Finds and displays tasks whose descriptions contain the requested text.
     *
     * @param taskList Task list to search.
     * @param ui User interface used to display matching tasks.
     * @param storage Storage that is not modified.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the command cannot be completed.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        if (getCommandArguments().isBlank()) {
            throw new InvalidCommandFormatException(
                    "Give me something to find instead of wasting my time.");
        }
        List<Task> matchingTasks = taskList.findTasksFor(getCommandArguments());
        ui.printTasksForName(matchingTasks);
        return CommandResult.CONTINUE;
    }
}
