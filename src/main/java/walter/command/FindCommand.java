package walter.command;

import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword without changing persisted state.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a task-description search command.
     *
     * @param keyword Text to match against task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFindResults(tasks.getTasks(), tasks.findMatchingIndices(this.keyword));
    }
}
