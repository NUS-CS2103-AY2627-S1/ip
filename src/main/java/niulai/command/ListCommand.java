package niulai.command;

import niulai.model.TaskList;
import niulai.service.Storage;
import niulai.service.Ui;

/**
 * Displays all tasks currently managed by NiuLai.
 */
public class ListCommand extends Command {
    /** Creates a list command. */
    public ListCommand() {
        super(Type.LIST);
    }

    /** Shows every task in display order. */
    @Override
    public UndoAction execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showList(tasks);
        return null;
    }
}
