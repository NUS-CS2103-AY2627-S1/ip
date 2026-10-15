package thomas.command;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Ends the session, serving the bye command.
 * The only command that reports itself as an exit, which is how the read loop
 * knows to stop without testing which command it is holding.
 */
public class ExitCommand extends Command {
    /**
     * Says goodbye.
     * <p>
     * Stopping itself is not done here: that is reported through
     * {@link #isExit()}, leaving whoever ran the command to decide what
     * stopping means -- ending a read loop, or closing a window. There is
     * nothing to save either, since bye changes no tasks.
     *
     * @param tasks Unused.
     * @param ui Used to word the farewell.
     * @param storage Unused.
     * @param history Unused.
     * @return The farewell.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history) {
        return ui.getGoodbyeMessage();
    }

    /**
     * Returns that the chatbot should stop.
     *
     * @return Always true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
