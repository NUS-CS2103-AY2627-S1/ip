package matthew.command;

import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Terminates the command loop after displaying a goodbye message. */
public class ExitCommand extends Command {

    /** Creates an exit command. */
    public ExitCommand() {
    }

    /**
     * Displays the goodbye message.
     *
     * @param tasks Unused task list supplied by the command loop.
     * @param ui User interface used to display the message.
     * @param storage Unused storage supplied by the command loop.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /** Returns {@code true} to signal that the application should exit. */
    @Override
    public boolean isExit() {
        return true;
    }
}
