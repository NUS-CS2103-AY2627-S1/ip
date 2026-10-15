package elsa.command;

import elsa.storage.Storage;
import elsa.task.TaskList;
import elsa.ui.Ui;

/** Shows the whole task list. */
public class ListCommand extends Command {
    /**
     * Creates a command that shows the whole task list.
     * The list to show is handed to {@link #execute} rather than held here, so
     * this command is built with no arguments.
     */
    public ListCommand() {
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing changes, so the list is not saved. Only commands that alter the
        // list write to the disk.
        return ui.getTasksMessage(tasks);
    }
}
