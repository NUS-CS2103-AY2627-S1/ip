package clover.command;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/** Finds tutorees by a keyword in their names. */
public class FindTutoreeCommand extends Command {
    private final String keyword;

    /** Creates a command for the supplied tutoree-name keyword. */
    public FindTutoreeCommand(String arguments) throws CloverException {
        keyword = arguments.trim();
        if (keyword.isEmpty()) {
            throw new CloverException("Give the grove a learning companion name to search for.");
        }
    }

    /** Displays tutorees whose names contain the supplied keyword. */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) {
        ui.showTutoreeFindResults(tutorees.findNameContaining(keyword));
    }
}
