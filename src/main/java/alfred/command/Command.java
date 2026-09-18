package alfred.command;

import alfred.AlfredException;
import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Represents something the user has asked for, ready to be carried out.
 */
public abstract class Command {
    /** Sets up the part of a command that every kind shares. */
    protected Command() {
    }

    /**
     * Carries out what the user asked for. A command that changes the task
     * list is responsible for saving it.
     *
     * @param tasks the tasks to act on.
     * @param ui what to tell the user through.
     * @param storage where to keep the tasks.
     * @throws AlfredException if the command cannot be carried out.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws AlfredException;

    /**
     * Says whether this command ends the conversation.
     *
     * @return true if nothing more should be read after this command.
     */
    public boolean isExit() {
        return false;
    }
}
