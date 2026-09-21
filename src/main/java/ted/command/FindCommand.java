package ted.command;

import ted.Storage;
import ted.Ui;
import ted.task.TaskList;

/**
 * Shows only the tasks whose description contains a keyword.
 * <p>
 * Nothing is changed, so nothing is saved: this is a way of looking at the
 * list, not of editing it.
 */
public class FindCommand extends Command {
    /** Text to look for inside task descriptions. */
    private final String keyword;

    /**
     * Creates a command that searches for the given keyword.
     *
     * @param keyword text to look for inside task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Shows the matching tasks. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMatchingTasks(tasks, tasks.find(keyword), keyword);
    }
}
