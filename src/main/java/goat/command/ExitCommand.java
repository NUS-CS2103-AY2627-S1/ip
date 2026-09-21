package goat.command;

import goat.storage.Storage;
import goat.task.TaskList;
import goat.ui.Ui;

/**
 * Ends the program.
 *
 * {@link #execute} does nothing at all, which is deliberate: the farewell is
 * owed whether the user typed "bye" or the input simply ran out, so
 * {@link goat.Goat} shows it once after the loop rather than here. All this command
 * has to do is answer yes to {@link #isExit()}.
 */
public class ExitCommand extends Command {

    /**
     * {@inheritDoc}
     *
     * <p>Does nothing. The farewell is shown by {@link goat.Goat} after the
     * loop ends, so that it appears whether the user typed "bye" or the input
     * simply ran out.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Nothing to do: see the class comment.
    }

    /**
     * {@inheritDoc}
     *
     * @return always true; this is the one command that ends the program
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
