package myriad.command;

import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Shows every task whose description contains a given keyword, ignoring
 * case (see {@link TaskList#getTasksMatching} and
 * {@link myriad.task.Task#descriptionContains}). Reads the list without
 * changing it, so it never saves.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Creates a command that reports the tasks whose descriptions contain
     * {@code keyword}.
     *
     * @param keyword the text to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows the tasks whose descriptions contain the keyword, or a "none
     * found" message if there are none.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks.getTasksMatching(keyword), keyword);
    }
}
