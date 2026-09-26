package dulio.command;

import java.io.IOException;

import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword The case-insensitive description keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds matching tasks and displays them.
     *
     * @param tasks The task list to search.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        ui.showFind(tasks.find(keyword));
    }
}
