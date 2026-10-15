package chione.command;

import chione.Storage;
import chione.Ui;
import chione.task.TaskList;

/**
 * Shows every task in the list.
 *
 * <p>Nothing is saved: the list is only being read, so the file on disk is
 * already in step with it.
 */
public class ListCommand extends Command {
    /** Creates the command; showing the whole list needs no further detail. */
    public ListCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
