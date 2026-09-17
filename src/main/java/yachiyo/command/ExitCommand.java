package yachiyo.command;

import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Displays Yachiyo's farewell and signals that the application should stop.
 */
public class ExitCommand extends Command {
    /**
     * Displays the farewell without changing or saving tasks.
     *
     * @param tasks unused because exiting does not inspect tasks.
     * @param ui user interface used to display the farewell.
     * @param storage unused because exiting does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showExit();
    }

    /**
     * Indicates that this command ends the application.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
