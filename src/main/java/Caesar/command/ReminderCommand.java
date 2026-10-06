package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Displays overdue and soon-due pending dated tasks. */
public class ReminderCommand extends Command {

    /** Displays the current reminder report through the supplied user interface. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        ui.showReminders(tasks);
    }
}
