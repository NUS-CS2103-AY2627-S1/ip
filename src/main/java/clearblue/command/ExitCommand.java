package clearblue.command;

import clearblue.storage.Storage;
import clearblue.task.TaskList;
import clearblue.ui.Ui;

/**
 * Ends the chatbot's session, after printing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Prints the farewell message. Does not change or save {@code tasks}.
     *
     * @param tasks unused
     * @param ui user interface to report through
     * @param storage unused
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showBye();
    }

    /**
     * Returns {@code true}, since this command ends the chatbot's session.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
