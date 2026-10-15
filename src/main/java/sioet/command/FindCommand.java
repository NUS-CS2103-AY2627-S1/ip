package sioet.command;

import java.util.List;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that searches for tasks containing a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword the keyword to search for
     * @throws SioetException if no keyword is provided
     */
    public FindCommand(String keyword) throws SioetException {
        if (keyword.isBlank()) {
            throw new SioetException("Please provide a keyword to search for.");
        }

        this.keyword = keyword;
    }

    /**
     * Executes the find command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matchingTasks = tasks.find(keyword);
        ui.showMatchingTasks(matchingTasks);
    }
}
