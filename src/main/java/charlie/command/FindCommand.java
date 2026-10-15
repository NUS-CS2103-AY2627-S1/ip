package charlie.command;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Displays tasks whose descriptions contain a requested keyword or phrase.
 */
public class FindCommand extends Command {
    /** Keyword or phrase to find in task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword Keyword or phrase that matching descriptions must contain.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays matching tasks without changing or saving the task list.
     *
     * @param tasks Task collection to search.
     * @param ui User interface used to display the results.
     * @param storage Storage collaborator, which this read-only command does not use.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("I sniffed around and found these matching tasks:");
        int matchCount = 0;
        for (Task task : tasks.findByKeyword(this.keyword)) {
            matchCount++;
            ui.showMessage(matchCount + "." + task);
        }

        if (matchCount == 0) {
            ui.showMessage("Hmm... I couldn't track down a matching task.");
        }
    }
}
