package chausistant.command;

import chausistant.storage.Storage;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that displays a farewell message and ends the chatbot session. */
public class ExitCommand extends Command {

    /** Shows the chatbot's farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns whether this command ends the chatbot session. */
    @Override
    public boolean isExit() {
        return true;
    }
}
