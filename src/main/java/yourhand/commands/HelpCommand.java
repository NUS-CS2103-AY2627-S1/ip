package yourhand.commands;

import yourhand.exceptions.YourHandException;
import yourhand.storage.Storage;
import yourhand.tasks.TaskList;
import yourhand.ui.Ui;

/** Displays the commands supported by YourHand. */
public class HelpCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showHelp();
    }
}
