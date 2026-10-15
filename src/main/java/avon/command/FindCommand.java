package avon.command;

import avon.storage.Storage;
import avon.task.TaskList;
import avon.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command for the keyword to find.
     *
     * @param keyword the text to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) {
        ui.showMatchingTasks(taskList, taskList.find(keyword));
    }
}
