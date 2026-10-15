package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.TaskList;
import shai.ui.Ui;

/** A command that displays tasks whose descriptions contain a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Creates a find command for the supplied keyword. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Finds matching tasks and displays them without changing application state. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
