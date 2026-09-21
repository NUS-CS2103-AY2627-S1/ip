package ted.command;

import ted.Storage;
import ted.TedException;
import ted.Ui;
import ted.task.TaskList;

/**
 * Something the user has asked Ted to do, ready to be carried out.
 * <p>
 * Each kind of command is its own subclass, so adding a command means adding a
 * class rather than adding a branch to a growing switch statement in
 * {@link ted.Ted}. A command is built by {@link ted.Parser} from the user's input,
 * which means the details are already checked by the time it is executed.
 */
public abstract class Command {
    /** Creates a command. Called only by the subclass being created. */
    protected Command() {
    }

    /**
     * Carries out this command.
     *
     * @param tasks   the task list to read or change.
     * @param ui      used to tell the user what happened.
     * @param storage used to persist the task list if this command changes it.
     * @throws TedException if the command cannot be carried out.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws TedException;

    /**
     * Returns whether Ted should stop after this command.
     * Only {@link ExitCommand} overrides this, so the main loop does not need
     * to know which command is which.
     *
     * @return {@code true} if this command ends the conversation.
     */
    public boolean isExit() {
        return false;
    }
}
