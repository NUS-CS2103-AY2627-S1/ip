package clover.command;

import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Displays the commands that Clover supports and their input formats. */
public class HelpCommand extends Command {
    /** Displays Clover's command reference. */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
