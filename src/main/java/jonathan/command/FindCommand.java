package jonathan.command;

import jonathan.storage.Storage;
import jonathan.task.TaskList;
import jonathan.ui.UI;

/** Finds tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword search term
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Executes the search and delegates the display of matching tasks to the UI.
     *
     * @param tasks   The current list of tasks.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage handler (unused for this command).
     */
    @Override
    public void execute(TaskList tasks, UI ui, Storage storage) {
        ui.showFoundTasks(keyword, tasks.getAllTasks(), tasks.getSize());
    }
}
