package alzara.command;

import alzara.storage.Storage;
import alzara.task.TaskList;
import alzara.ui.Ui;

/**
 * Ends the program: shows the goodbye message and signals {@link Command#isExit}.
 */
public class ExitCommand extends Command {
    /**
     * Shows the goodbye message. Takes no action on {@code memory} - the
     * program loop stops because {@link #isExit()} returns {@code true}.
     * Read-only, so {@code storage} is unused.
     */
    @Override
    public void execute(TaskList memory, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that the program should stop.
     *
     * @return {@code true}, ending the program's main loop
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
