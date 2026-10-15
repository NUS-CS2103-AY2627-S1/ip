package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Finds tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword keyword to search for
     */
    public FindCommand(String keyword) {
        assert keyword != null : "Search keyword must not be null";
        this.keyword = keyword;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changesTasks() {
        return false;
    }

    /**
     * Finds matching tasks and displays them to the user.
     *
     * @param tasks list of tasks to search
     * @param ui user interface used to display the matching tasks
     * @throws DukeException if the search keyword is empty
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        if (keyword.isEmpty()) {
            throw new DukeException("The keyword to search for cannot be empty. Use: find [keyword]");
        }

        TaskList matchingTasks = new TaskList();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.getDescription().contains(keyword)) {
                matchingTasks.add(task);
            }
        }
        ui.showMatchingTasks(matchingTasks);
    }
}
