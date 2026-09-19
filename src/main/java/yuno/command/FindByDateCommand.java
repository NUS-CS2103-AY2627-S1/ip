package yuno.command;

import java.util.List;

import yuno.exception.YunoException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

/**
 * Represents a command that displays tasks relevant for a specified date.
 */
public class FindByDateCommand extends Command {
    /**
     * Creates a command with the date for which tasks should be found.
     *
     * @param dateText Date text entered by the user.
     */
    public FindByDateCommand(String dateText) {
        super(dateText);
    }

    /**
     * Finds and displays tasks relevant for the requested date.
     *
     * @param taskList Task list to search.
     * @param ui User interface used to display matching tasks.
     * @param storage Storage that is not modified.
     * @return Always {@link CommandResult#CONTINUE}.
     * @throws YunoException If the date is invalid.
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui, Storage storage) throws YunoException {
        List<Task> matchingTasks = taskList.findTasksFor(parseInputDate(getCommandArguments()));
        ui.printTasksForDate(matchingTasks);
        return CommandResult.CONTINUE;
    }
}
