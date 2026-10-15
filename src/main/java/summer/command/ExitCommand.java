package summer.command;

import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "bye" command: says goodbye and ends the chatbot. */
public class ExitCommand extends Command {
    /** Prints the farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** @return always {@code true}; this command ends the chatbot */
    @Override
    public boolean isExit() {
        return true;
    }
}
