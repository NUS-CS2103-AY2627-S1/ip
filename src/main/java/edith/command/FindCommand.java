package edith.command;

import edith.Storage;
import edith.Ui;
import edith.task.TaskList;

/** Displays tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions.
     *
     * @param keyword text to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, keyword);
    }
}
