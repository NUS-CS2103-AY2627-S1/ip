package mary.command;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Command that ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Displays the farewell without modifying or saving tasks.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that the command loop should end.
     *
     * @return true for this exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
