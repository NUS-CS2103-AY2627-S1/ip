package walter.command;

import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Displays Walter's goodbye and signals that the application loop should stop.
 */
public class ExitCommand extends Command {
    /**
     * Displays Walter's goodbye message without modifying task state.
     *
     * @param tasks Task list supplied by the application loop; it is not modified.
     * @param ui User interface used to display the goodbye message.
     * @param storage Storage component supplied by the application loop; it is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that Walter should stop accepting commands after this command executes.
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
