package carl.commands;

import carl.exceptions.CarlException;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to find and list tasks matching a specific keyword.
 */
public class FindCommand implements Command {

    private final String keyword;

    /**
     * Constructs a {@code FindCommand} with the specified search keyword.
     *
     * @param keyword The keyword to search for in task names.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * {@inheritDoc}
     * Finds all tasks matching the keyword and displays them to the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result containing tasks whose descriptions match the keyword.
     * @throws CarlException If an error occurs during execution.
     */
    @Override
    public CommandResult onRun(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        return CommandResult.createSuccess(ui.showTaskList(tasks.findTask(keyword)));
    }
}
