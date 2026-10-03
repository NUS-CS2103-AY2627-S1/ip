package ramly.command;

import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Command that displays a parsing or validation error. */
public class ErrorCommand extends Command {
    private final String message;

    /** Creates an error command with the supplied message. */
    public ErrorCommand(String message) {
        this.message = message;
    }

    /** Displays the parsing error. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show(message);
    }
}
