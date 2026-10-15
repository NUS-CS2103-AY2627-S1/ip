package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Exits the chatbot.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        ui.showBye();
        storage.save(tasks);
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_EXIT;
    }
}
