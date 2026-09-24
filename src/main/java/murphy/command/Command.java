package murphy.command;

import murphy.MurphyException;
import murphy.storage.Storage;
import murphy.task.TaskList;
import murphy.ui.Ui;

/** Represents one executable Murphy command. */
public abstract class Command {
    /** Executes this command using Murphy's collaborators. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MurphyException;

    /** Returns whether executing this command should end Murphy's conversation. */
    public boolean isExit() {
        return false;
    }
}
