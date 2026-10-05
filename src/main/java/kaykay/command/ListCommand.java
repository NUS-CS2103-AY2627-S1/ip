package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Displays all tasks in their current order.
 */
public final class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Displays the tasks in their current order.
     *
     * @param data application data containing the task list to display.
     * @param ui UI used to display the tasks.
     * @param storage unused storage component supplied by the command interface.
     * @throws IOException declared by the command interface; not thrown by listing.
     */
    @Override
    public void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        ui.showTaskList(data.getTasks());
    }
}
