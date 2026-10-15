package clover.command;

import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Displays all tutorees in Clover's tutoree directory. */
public class ListTutoreesCommand extends Command {
    /** Displays the tutoree directory. */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) {
        ui.showTutoreeList(tutorees.asList());
    }
}
