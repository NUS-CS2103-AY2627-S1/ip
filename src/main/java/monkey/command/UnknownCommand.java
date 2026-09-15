package monkey.command;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for reporting an unsupported user command. */
public class UnknownCommand extends CommandAction {
    private static final String DEFAULT_MESSAGE = "OOPS! Monkey says: This monkey does not recognize that command. "
            + "Try todo, deadline, event, list, mark, unmark, delete, or bye.";

    private final String message;

    /** Creates a command that reports an unsupported command. */
    public UnknownCommand() {
        this(DEFAULT_MESSAGE);
    }

    /** Creates a command that reports the supplied validation message. */
    public UnknownCommand(String message) {
        this.message = message;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(message);
    }
}
