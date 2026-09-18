package computah.command;

import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Shows all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays the current task list.
     *
     * @param model current application data.
     * @param ui user interface used to display the task list.
     * @param storage storage component; unused for this command.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ui.showTaskList(model.getTasks());
    }
}
