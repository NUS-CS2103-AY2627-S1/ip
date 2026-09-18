package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.ui.Ui;

/** Represents the "list" command, which displays the stored tasks. */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showResponse(tasks.getDisplayLines());
    }
}
