package edith.command;

import edith.EdithException;
import edith.Storage;
import edith.Ui;
import edith.task.TaskList;

/** A parsed command that can be executed by Edith. */
public abstract class Command {
    /** Executes this command. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws EdithException;
    /** Returns whether this command ends the session. */
    public boolean isExit() {
        return false;
    }
}
