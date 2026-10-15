package summer.command;

import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "find KEYWORD" command: lists every task whose description contains the keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates the command.
     *
     * @param keyword text to search for within task descriptions
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Lists every task whose description contains the keyword. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(tasks.find(this.keyword));
    }
}
