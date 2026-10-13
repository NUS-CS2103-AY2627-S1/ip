package jimbo.command;

import jimbo.JimboException;
import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;

/**
 * Command to save to file.
 */
public class SaveCommand extends Command {
    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) throws JimboException {
        // saving is handled externally, display any message there instead
        return "tasks are saved automatically but alright";
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
