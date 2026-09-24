package hampster.command;

import hampster.exception.HampsterException;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Command that ends the Hampster session. */
public class ByeCommand extends Command {

    /** Creates a bye command and validates that it has no arguments. */
    public ByeCommand(String[] parts) throws HampsterException {
        if (parts.length != 1) {
            throw new HampsterException("Even my evil plans need exactly zero extra arguments.");
        }
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.shutdown();
    }
}
