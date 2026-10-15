package lumine.command;

import lumine.task.TaskList;
import lumine.ui.Ui;

/**
 * Reverses the most recent successful task-list change.
 */
public class UndoCommand extends Command {

    /**
     * {@inheritDoc}
     * Reverses the most recent change and prints a confirmation naming the reverted command.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) {
        String undoneCommand = taskList.undoLastChange();
        ui.showMessage("Done! I had undo the latest command. :D (" + undoneCommand + ")");
    }
}
