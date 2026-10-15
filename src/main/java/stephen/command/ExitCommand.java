package stephen.command;

import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Displays the farewell and signals that the application should stop. */
public class ExitCommand extends Command {
    /** Creates a command that exits the application. */
    public ExitCommand() {
    }

    /**
     * Displays the application's farewell message.
     *
     * @param tasks current task list; not used by this command
     * @param ui console UI used to display the farewell
     * @param storage persistence service; not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that this command ends the application.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
