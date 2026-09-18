package zhangwei.command;

import zhangwei.storage.Storage;
import zhangwei.task.TaskList;
import zhangwei.ui.Ui;

/** Says goodbye and ends the session. */
public class ExitCommand extends Command {

    /** Creates the command that ends the session. */
    public ExitCommand() {
    }

    /**
     * Prints the farewell message. The session ends because {@link #isExit}
     * returns true, not because this method stops the program itself.
     *
     * @param tasks unused, because exiting changes no task.
     * @param ui used to print the farewell.
     * @param storage unused, because every earlier command already saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns true: this is the one command that stops the chatbot.
     *
     * @return always true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
