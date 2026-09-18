package aster.command;

import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Shows the tasks whose description contains a keyword.
 *
 * <p>Like {@link ListCommand}, this command only reads the list, so it is one of the
 * commands that never save. The keyword has already been checked by the {@code Parser}, so
 * by the time this command exists it is known to hold something.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword the text to look for in each task description.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows the tasks whose description contains the keyword.
     *
     * @param tasks the task list to search.
     * @param ui the user interface to show the matches through.
     * @param storage the store; unused, because searching changes nothing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFound(tasks.find(keyword));
    }
}
