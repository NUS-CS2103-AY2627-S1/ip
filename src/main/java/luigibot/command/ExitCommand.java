package luigibot.command;

import luigibot.storage.Storage;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Ends the current LuigiBot session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current LuigiBot session.
     */
    public ExitCommand() {
    }

    /**
     * Displays LuigiBot's goodbye message.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display the goodbye message.
     * @param storage storage dependency required by the common command interface.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that LuigiBot should stop accepting commands.
     *
     * @return true because this command exits LuigiBot.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
