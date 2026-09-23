package sallman.command;

import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * Shows the tasks whose description contains a given keyword.
 */
public class FindCommand extends Command {

    /** The text being searched for. */
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword the text to look for in task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows the tasks matching this command's keyword. Nothing is changed, so
     * nothing is saved.
     *
     * @param tasks   the list to search
     * @param ui      used to show the matches
     * @param storage unused, since this command changes nothing
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, tasks.find(keyword), keyword);
    }
}
