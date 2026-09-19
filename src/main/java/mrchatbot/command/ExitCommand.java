package mrchatbot.command;

import mrchatbot.storage.Storage;
import mrchatbot.task.TaskList;
import mrchatbot.ui.Ui;

/**
 * Exits the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Shows the farewell message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Returns true because this command ends the chatbot session.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
