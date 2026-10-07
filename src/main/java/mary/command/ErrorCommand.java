package mary.command;

import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Reports a specific parse error without changing tasks or ending the session.
 */
public class ErrorCommand extends Command {
    private final String message;

    /**
     * Retains corrective guidance for display when the command executes.
     */
    public ErrorCommand(String message) {
        this.message = message;
    }

    /**
     * Displays the validation failure through the shared interface.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showError(message);
    }
}
