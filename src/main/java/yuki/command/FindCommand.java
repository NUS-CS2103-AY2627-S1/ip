package yuki.command;

import yuki.storage.Storage;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    /** Text to search for in task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches for the supplied keyword.
     *
     * @param keyword Text to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Displays matching tasks without changing or saving the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.findTasks(keyword));
    }
}
