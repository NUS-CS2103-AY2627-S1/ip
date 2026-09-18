package eden.command;

import eden.exception.EdenException;
import eden.task.TaskList;
import eden.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;
    private final TaskList tasks;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword text to find within task descriptions.
     * @param tasks task list to search.
     */
    public FindCommand(String keyword, TaskList tasks) {
        this.keyword = keyword.trim();
        this.tasks = tasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(Ui ui) throws EdenException {
        if (keyword.isEmpty()) {
            throw new EdenException("OOPS!!! The keyword for find cannot be empty.");
        }
        return ui.formatMatchingTasks(tasks.find(keyword));
    }
}
