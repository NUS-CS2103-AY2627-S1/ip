package arin.command;

import java.util.List;

import arin.storage.Storage;
import arin.task.Task;
import arin.ui.Ui;

/**
 * Ends Arin after showing its goodbye message.
 */
public class ExitCommand extends Command {
    /**
     * Displays Arin's goodbye message. This command does not change task data.
     *
     * @param tasks Current tasks, which are not changed.
     * @param ui User interface for feedback.
     * @param storage Persistent task storage, which is not changed.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that Arin should stop reading commands.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
